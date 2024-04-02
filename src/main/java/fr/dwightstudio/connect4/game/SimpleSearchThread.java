package fr.dwightstudio.connect4.game;

public class SimpleSearchThread extends SearchThread {

    private static final TranspositionTable TRANSPOSITION_TABLE = new TranspositionTable();

    public SimpleSearchThread(GameState initialState) {
        super(initialState);
    }

    @Override
    public void run() {
        result = eval(initialState, 10);
    }

    private int eval(GameState state, int maxRecursivity) {
        nb++;

        Integer saved = TRANSPOSITION_TABLE.get(state);

        if (saved != null) {
            return saved;
        }

        if (state.isWinningState()) return GameState.GRID_WIDTH;

        if (maxRecursivity == 0) return 0;

        int score = Long.bitCount(state.computeWinningPositions());

        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            if (state.isPlayable(i)) {
                score -= eval(state.play(i), maxRecursivity - 1)/2;
            }
        }

        TRANSPOSITION_TABLE.put(state, score);

        return score;
    }

    public int getResult() {
        return result;
    }
}
