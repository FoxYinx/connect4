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
    public static int negamax(GameState state) {
        // check for draw game
        if (state.isDraw()) return 0;

        // check if current player can win next move
        for (int x = 0; x < GameState.GRID_WIDTH; x++) {
            if (state.isPlayable(x) && (state.play(x).getWinner() != ' ')) {
                return (GameState.GRID_WIDTH * GameState.GRID_HEIGHT + 1 - state.getNbMoves()) / 2;
            }
        }

        // init the best possible score with a lower bound of score
        int bestScore = -GameState.GRID_WIDTH * GameState.GRID_HEIGHT;

        // compute the score of all possible next move and keep the best one
        for (int x = 0; x < GameState.GRID_WIDTH; x++) {
            if (state.isPlayable(x)) {
                GameState state2 = state.play(x); // It's opponent turn in P2 position after current player plays x column.
                int score = -negamax(state2); // If current player plays col x, his score will be the opposite of opponent's score after playing col x
                if (score > bestScore) bestScore = score; // keep track of the best possible score so far.
            }
        }

        return bestScore;
    }

    public void play() {
        while (state.getWinner() == ' ') {
            state = state.play(displayController.play(state));

            displayController.render(state);

            SearchThread[] searchThreads = new SearchThread[GameState.GRID_WIDTH];

            for (int i = 0; i < searchThreads.length; i++) {
                GameState state2 = state.play(i);
                System.out.println("\n");
                displayController.render(state2);
                System.out.println("\n");
                searchThreads[i] = new SearchThread(state2);
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
