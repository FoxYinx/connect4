package fr.dwightstudio.connect4.game;

public class MoveSorter {

    private int size;
    private final MoveScore[] entries;

    public MoveSorter() {
        this.size = 0;
        this.entries = new MoveScore[GameState.GRID_WIDTH];
        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            entries[i] = new MoveScore(0, 0);
        }
    }

    public void add(MoveScore move) {
        int pos = size++;
        for (; pos != 0 && entries[pos-1].score() > move.score(); --pos) {
            entries[pos] = entries[pos-1];
        }
        entries[pos] = new MoveScore(move.move(), move.score());
    }

    public long getNext() {
        if (size > 0) {
            return entries[--size].move();
        } else {
            return 0;
        }
    }

    public void reset() {
        this.size = 0;
    }
}
