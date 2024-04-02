package fr.dwightstudio.connect4.search;

import fr.dwightstudio.connect4.game.GameState;

public class SimpleSearchThread extends ThoroughSearchThread {

    private static final int MAX_RECURSIVITY = 22;
    private static final TranspositionTable TRANSPOSITION_TABLE = new TranspositionTable();

    public SimpleSearchThread(GameState initialState) {
        super(initialState);
    }

    @Override
    protected int negamax(GameState state, int alpha, int beta, int depth) {

        if (depth > MAX_RECURSIVITY) {
            if (state.isWinningState() || state.computeWinningPositions() != 0) {
                return ((GameState.FLAT_LENGTH + 1 - state.getNbMoves()) / 2) - 1;
            } else {
                return 0;
            }
        }

        return super.negamax(state, alpha, beta, depth);
    }
}
