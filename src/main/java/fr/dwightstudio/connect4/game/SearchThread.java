package fr.dwightstudio.connect4.game;

import java.util.Arrays;

public class SearchThread extends Thread {

    public static final int[] COLUMN_ORDER;

    static {
        COLUMN_ORDER = new int[GameState.GRID_WIDTH];

        int center = Math.floorDiv(GameState.GRID_WIDTH, 2);
        int offset = 0;
        boolean negative = false;

        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            if (negative) {
                COLUMN_ORDER[i] = center - offset;
            } else {
                COLUMN_ORDER[i] = center + offset;
                offset++;
            }

            if (offset == 0) {
                offset++;
            }

            negative = !negative;
        }

        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            int finalI = i;
            if (Arrays.stream(COLUMN_ORDER).noneMatch(n -> finalI == n)) throw new RuntimeException("Column order array is incomplete");
        }
    }

    private static final TranspositionTable TRANSPOSITION_TABLE = new TranspositionTable();

    private long nb;
    private final GameState initialState;
    private int result;
    private int hintScore;

    public SearchThread(GameState initialState) {
        this.initialState = initialState;
        hintScore = Integer.MIN_VALUE;
    }

    @Override
    public void run() {
        int min = -(GameState.FLAT_LENGTH - initialState.getNbMoves())/2;
        int max = (GameState.FLAT_LENGTH + 1 - initialState.getNbMoves())/2;

        // iteratively narrow the min-max exploration window
        while(min < max) {
            int med = min + (max - min)/2;
            if(med <= 0 && min/2 < med) med = min/2;
            else if(med >= 0 && max/2 > med) med = max/2;
            int r = negamax(initialState, med, med + 1, 0);   // use a null depth window to know if the actual score is greater or smaller than med
            if(r <= med) max = r;
            else min = r;
        }

        result = -min;

        //result = -negamax(initialState, -GameState.FLAT_LENGTH/2, GameState.FLAT_LENGTH/2, 0);
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
    private int negamax(GameState state, int alpha, int beta, int depth) {
        nb++;

        Integer rtn = TRANSPOSITION_TABLE.get(state);
        if (rtn != null) return rtn;

        // check for draw game
        if (state.isDraw()) return 0;

        // check if current player can win next move
        for (int x = 0; x < GameState.GRID_WIDTH; x++) {
            if (state.isPlayable(x) && (state.play(x).isWinningState())) {
                return (GameState.FLAT_LENGTH + 1 - state.getNbMoves()) / 2;
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
                int score = -negamax(state2, -beta, -alpha, depth + 1);
                // no need to have good precision for score better than beta (opponent's score worse than -beta)
                // no need to check for score worse than alpha (opponent's score worse better than -alpha)

                // prune the exploration if we find a possible move better than what we were looking for.
                if (score >= beta) return score;

                // reduce the [alpha;beta] window for next exploration, as we only
                // need to search for a position that is better than the best so far.
                if (score > alpha) alpha = score;
            }
        }

        TRANSPOSITION_TABLE.put(state, alpha);

        if (depth == 1) {
            if (-alpha >= hintScore) {
                hintScore = -alpha;
            }
        }

        return alpha;
    }

    public int getResult() {
        return result;
    }

    public long getNb() {
        return nb;
    }
}
