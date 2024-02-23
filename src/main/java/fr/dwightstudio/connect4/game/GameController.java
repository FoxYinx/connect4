package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

public class GameController {

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
    public static int negamax(GameState state, int alpha, int beta) {
        // check for draw game
        if (state.isDraw()) return 0;

        // check if current player can win next move
        for (int x = 0; x < GameState.GRID_WIDTH; x++) {
            if (state.isPlayable(x) && (state.play(x).getWinner() != ' ')) {
                return (GameState.GRID_WIDTH * GameState.GRID_HEIGHT + 1 - state.getNbMoves()) / 2;
            }
        }

        int max = (GameState.FLAT_LENGTH - 1 - state.getNbMoves())/2;   // upper bound of our score as we cannot win immediately
        if (beta > max) {
            beta = max;                                                 // there is no need to keep beta above our max possible score.
            if(alpha >= beta) return beta;                              // prune the exploration if the [alpha;beta] window is empty.
        }

        for(int x = 0; x < GameState.GRID_WIDTH; x++)       // compute the score of all possible next move and keep the best one
            if(state.isPlayable(x)) {
                GameState state2 = state.play(x);           // It's opponent turn in P2 position after current player plays x column.
                int score = -negamax(state2, -beta, -alpha);    // explore opponent's score within [-beta;-alpha] windows:
                                                            // no need to have good precision for score better than beta (opponent's score worse than -beta)
                                                            // no need to check for score worse than alpha (opponent's score worse better than -alpha)

                if(score >= beta) return score;     // prune the exploration if we find a possible move better than what we were looking for.
                if(score > alpha) alpha = score;    // reduce the [alpha;beta] window for next exploration, as we only
                                                    // need to search for a position that is better than the best so far.
            }
        return alpha;
    }

    public void play() {
        while (state.getWinner() == ' ') {
            state = state.play(displayController.play(state));

            displayController.render(state);

            SearchThread[] searchThreads = new SearchThread[GameState.GRID_WIDTH];

            for (int i = 0; i < searchThreads.length; i++) {
                GameState state2 = state.play(i);
                if (i == 0) {
                    searchThreads[i] = new SearchThread(state2);
                }
                searchThreads[i].start();
            }

            int best = 0;
            int bestScore = -GameState.GRID_WIDTH * GameState.GRID_HEIGHT;

            for (int i = 0; i < searchThreads.length; i++) {
                try {
                    searchThreads[i].join();
                    int score = searchThreads[i].getResult();
                    if (score > bestScore)  {
                        bestScore = score;
                        best = i;
                    }
                } catch (InterruptedException ignored) {}
            }

            state = state.play(best);
        }
    }
}
