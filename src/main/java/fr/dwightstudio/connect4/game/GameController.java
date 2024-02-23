package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

import java.util.concurrent.ConcurrentHashMap;

public class GameController {

    public static final int[] COLUMN_ORDER = new int[]{3, 4, 2, 5, 1, 6, 0};

    private final static ConcurrentHashMap<GameState, Integer> TRANSPOSITION_TABLE = new ConcurrentHashMap<>(20000, 1.1F);

    private final DisplayController displayController;
    private GameState state;

    public GameController(DisplayController displayController) {
        this.displayController = displayController;
        state = new GameState();
    }

    /*
     * Recursively solve a connect 4 position using negamax variant of min-max algorithm.
     * @return the score of a position:
     *  - 0 for a draw game
     *  - positive score if you can win whatever your opponent is playing. Your score is
     *    the number of moves before the end you can win (the faster you win, the higher your score)
     *  - negative score if your opponent can force you to lose. Your score is the opposite of
     *    the number of moves before the end you will lose (the faster you lose, the lower your score).
     */
    public static int negamax(GameState state, int alpha, int beta, Runnable depthUpdater) {
        depthUpdater.run();
        if (TRANSPOSITION_TABLE.containsKey(state)) return TRANSPOSITION_TABLE.get(state);

        // check for draw game
        if (state.isDraw()) return 0;

        // check if current player can win next move
        for (int x = 0; x < GameState.GRID_WIDTH; x++) {
            if (state.isPlayable(x) && (state.play(x).getWinner() != ' ')) {
                return (GameState.GRID_WIDTH * GameState.GRID_HEIGHT + 1 - state.getNbMoves()) / 2;
            }
        }

        // upper bound of our score as we cannot win immediately
        int max = (GameState.FLAT_LENGTH - 1 - state.getNbMoves()) / 2;
        if (beta > max) {
            // there is no need to keep beta above our max possible score.
            beta = max;
            // prune the exploration if the [alpha;beta] window is empty.
            if (alpha >= beta) return beta;
        }

        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            // compute the score of all possible next move and keep the best one
            int x = COLUMN_ORDER[i];

            if (state.isPlayable(x)) {
                // It's opponent turn in P2 position after current player plays x column.
                GameState state2 = state.play(x);
                // explore opponent's score within [-beta;-alpha] windows:
                int score = -negamax(state2, -beta, -alpha, depthUpdater);
                // no need to have good precision for score better than beta (opponent's score worse than -beta)
                // no need to check for score worse than alpha (opponent's score worse better than -alpha)

                if (score >= beta)
                    // prune the exploration if we find a possible move better than what we were looking for.
                    return score;
                // reduce the [alpha;beta] window for next exploration, as we only
                if (score > alpha) alpha = score;
                // need to search for a position that is better than the best so far.
            }
        }

        TRANSPOSITION_TABLE.put(state, alpha);

        return alpha;
    }

    public void play() {
        while (state.getWinner() == ' ') {
            state = state.play(displayController.play(state));

            displayController.render(state);

            SearchThread[] searchThreads = new SearchThread[GameState.GRID_WIDTH];

            for (int i = 0; i < searchThreads.length; i++) {
                if (state.isPlayable(i)) {
                    GameState state2 = state.play(i);
                    searchThreads[i] = new SearchThread(state2);
                    searchThreads[i].start();
                }
            }

            final int WAITING_MILLIS = 100;

            boolean done = false;
            int lastTotal = 0;
            int total = 0;
            System.out.println();

            while (!done) {
                done = true;
                lastTotal = total;
                total = 0;

                System.out.print("\rSearching... Threads: ");
                for (int i = 0; i < searchThreads.length; i++) {
                    if (searchThreads[i] == null) continue;
                    done &= !searchThreads[i].isAlive();
                    total += searchThreads[i].getNb();
                    System.out.printf(" %6d", searchThreads[i].getNb());
                }

                System.out.printf("  Total: %9d", total);

                System.out.printf("  %4d states/s", (total - lastTotal) * 1000 / WAITING_MILLIS);

                try {
                    Thread.sleep(WAITING_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println();

            int best = 0;
            int bestScore = -GameState.GRID_WIDTH * GameState.GRID_HEIGHT;

            for (int i = 0; i < searchThreads.length; i++) {
                if (searchThreads[i] == null) continue;
                int score = searchThreads[i].getResult();
                if (score > bestScore) {
                    bestScore = score;
                    best = i;
                }
            }

            state = state.play(best);
        }
    }
}
