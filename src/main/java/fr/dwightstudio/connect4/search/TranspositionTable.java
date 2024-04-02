package fr.dwightstudio.connect4.search;

import fr.dwightstudio.connect4.game.GameState;

public class TranspositionTable {

    public static final int SIZE = 8388593;
    private final Node[] TABLE = new Node[SIZE];

    private int getIndex(GameState state) {
        int i = (int) (state.longHashCode() % ((long) SIZE));
        return i >= 0 ? i : SIZE + i;
    }

    public Integer get(GameState state) {
        int index = getIndex(state);

        Node node = TABLE[index];

        if (node != null && node.state.equals(state)) return node.score;
        else return null;
    }

    public void put(GameState state, int score) {
        int index = getIndex(state);

        TABLE[index] = new Node(state, score);
    }

    private record Node(GameState state, int score) {}
}
