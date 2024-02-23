package fr.dwightstudio.connect4.game;

import java.util.ArrayList;

public class GameState {
    public static final int GRID_HEIGHT = 4; //6
    public static final int GRID_WIDTH = 7;
    public static final int FLAT_LENGTH = GRID_HEIGHT * GRID_WIDTH;

    public static final Long[] WINNING_MASK;

    static {
        ArrayList<Long> rtn = new ArrayList<>();

        // Horizontale
        for (int x = 0; x < GRID_WIDTH - 3; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                long mask = 0L;
                for (int d = 0; d < 4; d++) {
                    mask |= (1L << new GamePosition(x + d, y).getFlatIndex());
                }
                rtn.add(mask);
            }
        }

        // Verticale
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT - 3; y++) {
                long mask = 0L;
                for (int d = 0; d < 4; d++) {
                    mask |= (1L << new GamePosition(x, y + d).getFlatIndex());
                }
                rtn.add(mask);
            }
        }

        // Diagonale vers le haut
        for (int x = 0; x < GRID_WIDTH - 3; x++) {
            for (int y = 0; y < GRID_HEIGHT - 3; y++) {
                long mask = 0L;
                for (int d = 0; d < 4; d++) {
                    mask |= (1L << new GamePosition(x + d, y + d).getFlatIndex());
                }
                rtn.add(mask);
            }
        }

        // Diagonale le bas
        for (int x = 0; x < GRID_WIDTH - 3; x++) {
            for (int y = 3; y < GRID_HEIGHT; y++) {
                long mask = 0L;
                for (int d = 0; d < 4; d++) {
                    mask |= (1L << new GamePosition(x + d, y - d).getFlatIndex());
                }
                rtn.add(mask);
            }
        }

        WINNING_MASK = rtn.toArray(rtn.toArray(new Long[0]));
    }

    private final long crossGrid;
    private final long circleGrid;
    private final int nbMoves;

    private GameState(long crossGrid, long circleGrid, int nbMoves) {
        this.crossGrid = crossGrid;
        this.circleGrid = circleGrid;
        this.nbMoves = nbMoves;
    }

    public GameState() {
        //this(5461,10922, 0);
        this(0, 0, 0);
    }

    public GameState play(int x) {
        int y = getFreeHeight(x);
        if (y == -1) throw new IllegalArgumentException("Column is full");
        if ((nbMoves & 1) == 0) {
            return placeCross(new GamePosition(x, y));
        } else {
            return placeCircle(new GamePosition(x, y));
        }
    }

    public GameState placeCross(GamePosition pos) {
        long nCrossGrid = crossGrid | (1L << pos.getFlatIndex());
        return new GameState(nCrossGrid, circleGrid, nbMoves + 1);
    }

    public GameState placeCircle(GamePosition pos) {
        long nCircleGrid = circleGrid | (1L << pos.getFlatIndex());
        return new GameState(crossGrid, nCircleGrid, nbMoves + 1);
    }

    public char get(GamePosition pos) {
        long cross = (crossGrid >>> pos.getFlatIndex()) & 1;
        long circle = (circleGrid >>> pos.getFlatIndex()) & 1;

        if (cross == 1 && circle == 0) {
            return 'X';
        } else if (cross == 0 && circle == 1) {
            return 'O';
        } else if (cross == 0) {
            return ' ';
        } else {
            throw new RuntimeException("Invalid state: two pawns at the same position");
        }
    }

    public int getFreeHeight(int x) {
        long grid = crossGrid | circleGrid;
        for (int y = 0; y < GRID_HEIGHT; y++) {
            if (((grid >>> new GamePosition(x, y).getFlatIndex()) & 1L) == 0) {
                return y;
            }
        }

        return -1;
    }

    public boolean isPlayable(int x) {
        return get(new GamePosition(x, GRID_HEIGHT-1)) == ' ';
    }

    public boolean isDraw() {
        return ~(crossGrid | circleGrid) == 0L;
    }

    public char getWinner() {
        // Cross
        for (long mask : WINNING_MASK) {
            if ((crossGrid & mask) == mask) {
                return 'X';
            }
        }

        // Circle
        for (long mask : WINNING_MASK) {
            if ((circleGrid & mask) == mask) {
                return 'O';
            }
        }

        return ' ';
    }

    public int getNbMoves() {
        return nbMoves;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GameState state) {
            return state.crossGrid == this.crossGrid && state.circleGrid == this.circleGrid;
        } else return false;
    }

    @Override
    public int hashCode() {
        return (int) (this.circleGrid + (this.crossGrid | this.circleGrid)) % ~1;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", crossGrid, circleGrid);
    }
}
