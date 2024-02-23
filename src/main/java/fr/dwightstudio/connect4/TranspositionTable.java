package fr.dwightstudio.connect4;

import fr.dwightstudio.connect4.game.GameState;

public class TranspositionTable {

    public static final int SIZE = 100000000;
    public static final Node[] TABLE = new Node[SIZE];

    public Integer get(GameState state) {
        int index = state.hashCode() % SIZE;
        if (index < 0) return null;

        Node node = TABLE[index];

        if (node != null && node.state.equals(state)) return node.score;
        else return null;
    }

    public void put(GameState state, int score) {
        int index = state.hashCode() % SIZE;
        if (index < 0) return;

        TABLE[state.hashCode() % SIZE] = new Node(state, score);
    }

    private record Node(GameState state, int score) {}
}
