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

    public void add(long move, int score) {
        int pos = size++;
        for (; pos > 0 && entries[pos-1].getScore() > score; --pos) {
            entries[pos] = entries[pos-1];
        }
        entries[pos].setMove(move);
        entries[pos].setScore(score);
    }

    public long getNext() {
        if (size > 0) {
            return entries[--size].getMove();
        } else {
            return 0;
        }
    }

    public void reset() {
        this.size = 0;
    }

    private static class MoveScore {
        private long move;
        private int score;

        public MoveScore(long move, int score) {
            this.move = move;
            this.score = score;
        }

        public long getMove() {
            return move;
        }

        public int getScore() {
            return score;
        }

        public void setMove(long move) {
            this.move = move;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
