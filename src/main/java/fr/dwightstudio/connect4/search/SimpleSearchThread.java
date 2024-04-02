package fr.dwightstudio.connect4.search;

import fr.dwightstudio.connect4.game.GameState;

public class SimpleSearchThread extends SolverSearchThread {

    private static final int MAX_RECURSIVITY = 22;

    public SimpleSearchThread(GameState initialState) {
        super(initialState);
    }

    @Override
    protected int negamax(GameState state, int alpha, int beta, int depth) {

        if (depth > MAX_RECURSIVITY) {
            if (state.isWinningState()) {
                return ((GameState.FLAT_LENGTH + 1 - state.getNbMoves()) / 2);
            } else if (state.computeWinningPositions() != 0) {
                return ((GameState.FLAT_LENGTH + 1 - state.getNbMoves()) / 2) - 2;
            } else {
                return 0;
            }
        }

        return super.negamax(state, alpha, beta, depth);
    }
}
