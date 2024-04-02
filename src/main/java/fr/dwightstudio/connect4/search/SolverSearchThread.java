package fr.dwightstudio.connect4.search;

import fr.dwightstudio.connect4.game.GameState;
import fr.dwightstudio.connect4.game.MoveScore;
import fr.dwightstudio.connect4.game.MoveSorter;

import java.util.Arrays;

public class SolverSearchThread extends SearchThread {

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
            if (Arrays.stream(COLUMN_ORDER).noneMatch(n -> finalI == n))
                throw new RuntimeException("Column order array is incomplete");
        }
    }

    public static final TranspositionTable TRANSPOSITION_TABLE = new TranspositionTable();

    public SolverSearchThread(GameState initialState) {
        super(initialState);
    }

    @Override
    public void run() {
        int min = -(GameState.FLAT_LENGTH - initialState.getNbMoves()) / 2;
        int max = (GameState.FLAT_LENGTH + 1 - initialState.getNbMoves()) / 2;

        // iteratively narrow the min-max exploration window
        while (min < max) {
            int med = min + (max - min) / 2;
            if (med <= 0 && min / 2 < med) med = min / 2;
            else if (med >= 0 && max / 2 > med) med = max / 2;
            int r = negamax(initialState, med, med + 1, 0);   // use a null depth window to know if the actual score is greater or smaller than med
            if (r <= med) max = r;
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
    protected int negamax(GameState state, int alpha, int beta, int depth) {
        nb++;

        // check for draw game
        if (state.isDraw()) return 0;

        long next = state.possibleNonLosingMoves();

        // if no possible non-losing move, opponent wins next move
        if (next == 0) return -(GameState.FLAT_LENGTH - state.getNbMoves()) / 2;

        // check if current player can win next move
        for (int x = 0; x < GameState.GRID_WIDTH; x++) {
            if (state.isPlayable(x) && (state.play(x).isWinningState())) {
                return (GameState.FLAT_LENGTH + 1 - state.getNbMoves()) / 2;
            }
        }

        int min = -(GameState.FLAT_LENGTH - 2 - state.getNbMoves()) / 2;
        if (alpha < min) {
            alpha = min;
            if (alpha >= beta) return alpha;
        }

        int max = (GameState.FLAT_LENGTH - 1 - state.getNbMoves()) / 2;
        if (beta > max) {
            // there is no need to keep beta above our max possible score.
            beta = max;
            // prune the exploration if the [alpha;beta] window is empty.
            if (alpha >= beta) return beta;
        }

        // upper bound of our score as we cannot win immediately
        Integer val = TRANSPOSITION_TABLE.get(state);

        if (val != null) {
            if (val > GameState.MAX_SCORE - GameState.MIN_SCORE + 1) {
                // we have a lower bound
                min = val + 2 * GameState.MIN_SCORE - GameState.MAX_SCORE - 2;
                if (alpha < min) {
                    // there is no need to keep beta above our max possible score.
                    alpha = min;
                    // prune the exploration if the [alpha;beta] window is empty.
                    if (alpha >= beta) return alpha;
                }
            } else {
                // we have an upper bound
                max = val + GameState.MIN_SCORE - 1;
                if (beta > max) {
                    // there is no need to keep beta above our max possible score.
                    beta = max;
                    // prune the exploration if the [alpha;beta] window is empty.
                    if (alpha >= beta) return beta;
                }
            }
        }

        MoveSorter moves = new MoveSorter();
        for (int i = GameState.GRID_WIDTH - 1; i >= 0; i--) {
            long move = next & GameState.COLUMN_MASK[COLUMN_ORDER[i]];
            if (move != 0) {
                moves.add(new MoveScore(move, state.moveScore(move)));
            }
        }

        long newMove;
        while ((newMove = moves.getNext()) != 0) {
            GameState state2 = state.playMask(newMove);

            // explore opponent's score within [-beta;-alpha] windows:
            int score = -negamax(state2, -beta, -alpha, depth + 1);
            // no need to have good precision for score better than beta (opponent's score worse than -beta)
            // no need to check for score worse than alpha (opponent's score worse better than -alpha)

            // prune the exploration if we find a possible move better than what we were looking for.
            if (score >= beta) {
                // save the lower bound of the position
                TRANSPOSITION_TABLE.put(state, score + GameState.MAX_SCORE - 2 * GameState.MIN_SCORE + 2);
                return score;
            }

            // reduce the [alpha;beta] window for next exploration, as we only
            // need to search for a position that is better than the best so far.
            if (score > alpha) alpha = score;
        }

        TRANSPOSITION_TABLE.put(state, alpha - GameState.MIN_SCORE + 1);
        return alpha;
    }

    public int getResult() {
        return result;
    }

    public long getNb() {
        return nb;
    }
}
