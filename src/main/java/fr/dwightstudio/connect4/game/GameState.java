package fr.dwightstudio.connect4.game;

import java.util.ArrayList;

public class GameState {
    public static final int GRID_HEIGHT = 5;
    public static final int GRID_WIDTH = 7;
    public static final int FLAT_LENGTH = GRID_HEIGHT * GRID_WIDTH;
    public static final int MIN_SCORE = -FLAT_LENGTH / 2 + 3;
    public static final int MAX_SCORE = (FLAT_LENGTH + 1) / 2 - 3;

    public static final Long[] WINNING_MASKS;

    static {
        ArrayList<Long> rtn = new ArrayList<>();

        // Horizontale
        for (int x = 0; x < GRID_WIDTH - 3; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                long mask = 0L;
                for (int d = 0; d < 4; d++) {
                    mask |= (1L << getFlatIndex(x + d, y));
                }
                rtn.add(mask);
            }
        }

        // Verticale
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT - 3; y++) {
                long mask = 0L;
                for (int d = 0; d < 4; d++) {
                    mask |= (1L << getFlatIndex(x, y + d));
                }
                rtn.add(mask);
            }
        }

        // Diagonale vers le haut
        for (int x = 0; x < GRID_WIDTH - 3; x++) {
            for (int y = 0; y < GRID_HEIGHT - 3; y++) {
                long mask = 0L;
                for (int d = 0; d < 4; d++) {
                    mask |= (1L << getFlatIndex(x + d, y + d));
                }
                rtn.add(mask);
            }
        }

        // Diagonale le bas
        for (int x = 0; x < GRID_WIDTH - 3; x++) {
            for (int y = 3; y < GRID_HEIGHT; y++) {
                long mask = 0L;
                for (int d = 0; d < 4; d++) {
                    mask |= (1L << getFlatIndex(x + d, y - d));
                }
                rtn.add(mask);
            }
        }

        WINNING_MASKS = rtn.toArray(new Long[0]);
    }

    public static final Long[] WINNING_FILLED_POSITION_MASK;
    public static final Long[] WINNING_EMPTY_POSITION_MASK;

    static {
        ArrayList<Long> filled_rtn = new ArrayList<>();
        ArrayList<Long> empty_rtn = new ArrayList<>();

        for (int w = 0; w < 4; w++) {
            // Horizontale
            for (int x = 0; x < GRID_WIDTH - 3; x++) {
                for (int y = 0; y < GRID_HEIGHT; y++) {
                    long filled_mask = 0L;
                    long empty_mask = 0L;
                    for (int d = 0; d < 4; d++) {
                        if (d != w) filled_mask |= (1L << getFlatIndex(x + d, y));
                        else empty_mask |= (1L << getFlatIndex(x + d, y));
                    }
                    filled_rtn.add(filled_mask);
                    empty_rtn.add(empty_mask);
                }
            }

            // Verticale
            for (int x = 0; x < GRID_WIDTH; x++) {
                for (int y = 0; y < GRID_HEIGHT - 3; y++) {
                    long filled_mask = 0L;
                    long empty_mask = 0L;
                    for (int d = 0; d < 4; d++) {
                        if (d != w) filled_mask |= (1L << getFlatIndex(x, y + d));
                        else empty_mask |= (1L << getFlatIndex(x, y + d));
                    }
                    filled_rtn.add(filled_mask);
                    empty_rtn.add(empty_mask);
                }
            }

            // Diagonale vers le haut
            for (int x = 0; x < GRID_WIDTH - 3; x++) {
                for (int y = 0; y < GRID_HEIGHT - 3; y++) {
                    long filled_mask = 0L;
                    long empty_mask = 0L;
                    for (int d = 0; d < 4; d++) {
                        if (d != w) filled_mask |= (1L << getFlatIndex(x + d, y + d));
                        else empty_mask |= (1L << getFlatIndex(x + d, y + d));
                    }
                    filled_rtn.add(filled_mask);
                    empty_rtn.add(empty_mask);
                }
            }

            // Diagonale le bas
            for (int x = 0; x < GRID_WIDTH - 3; x++) {
                for (int y = 3; y < GRID_HEIGHT; y++) {
                    long filled_mask = 0L;
                    long empty_mask = 0L;
                    for (int d = 0; d < 4; d++) {
                        if (d != w) filled_mask |= (1L << getFlatIndex(x + d, y - d));
                        else empty_mask |= (1L << getFlatIndex(x + d, y - d));
                    }
                    filled_rtn.add(filled_mask);
                    empty_rtn.add(empty_mask);
                }
            }
        }

        WINNING_FILLED_POSITION_MASK = filled_rtn.toArray(new Long[0]);
        WINNING_EMPTY_POSITION_MASK = empty_rtn.toArray(new Long[0]);
    }

    public static final Long[] COLUMN_MASK;

    static {
        COLUMN_MASK = new Long[GRID_WIDTH];

        for (int x = 0; x < GRID_WIDTH; x++) {
            long mask = 0L;
            for (int y = 0; y < GRID_HEIGHT; y++) {
                mask |= 1L << getFlatIndex(x, y);
            }
            COLUMN_MASK[x] = mask;
        }
    }

    private final long crossGrid;
    private final long circleGrid;
    private final int nbMoves;
    private final int lastMove;

    public GameState(long crossGrid, long circleGrid, int nbMoves, int lastMove) {
        this.crossGrid = crossGrid;
        this.circleGrid = circleGrid;
        this.nbMoves = nbMoves;
        this.lastMove = lastMove;
    }

    public GameState() {
        this(0, 0, 0, -1);
    }

    public boolean isItCrossTurn() {
        return (nbMoves & 1) == 0;
    }

    public GameState play(int x) {
        int y = getFreeHeight(x);
        if (y == GRID_HEIGHT) throw new IllegalArgumentException("Column is full");
        if (isItCrossTurn()) {
            return placeCross(x, y);
        } else {
            return placeCircle(x, y);
        }
    }

    public GameState playMask(long mask) {
        for (int x = 0; x < GRID_WIDTH; x++) {
            if ((mask & COLUMN_MASK[x]) != 0) {
                return play(x);
            }
        }

        throw new IllegalArgumentException("Empty mask");
    }

    public GameState placeCross(int x, int y) {
        long nCrossGrid = crossGrid | (1L << getFlatIndex(x, y));
        return new GameState(nCrossGrid, circleGrid, nbMoves + 1, x);
    }

    public GameState placeCircle(int x, int y) {
        long nCircleGrid = circleGrid | (1L << getFlatIndex(x, y));
        return new GameState(crossGrid, nCircleGrid, nbMoves + 1, x);
    }

    public char get(int x, int y) {
        long cross = (crossGrid >>> getFlatIndex(x, y)) & 1;
        long circle = (circleGrid >>> getFlatIndex(x, y)) & 1;

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
            if (((grid >>> getFlatIndex(x, y)) & 1L) == 0) {
                return y;
            }
        }

        return GameState.GRID_HEIGHT;
    }

    public boolean isPlayable(int x) {
        return get(x, GRID_HEIGHT-1) == ' ';
    }

    public long possibleNonLosingMoves() {
        long possibleMask = computePossibleMoves();
        long opponentWinningPos = computeOpponentWinningPositions();
        long forcedMoves = possibleMask & opponentWinningPos;

        if (forcedMoves != 0) {
            if ((forcedMoves & (forcedMoves - 1)) != 0) return 0;
            else possibleMask = forcedMoves;
        }

        return possibleMask & ~(opponentWinningPos >> getFlatIndex(0, 1));
    }

    public long computePossibleMoves() {
        long grid = crossGrid | circleGrid;
        long mask = 0L;

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (((grid >>> getFlatIndex(x, y)) & 1L) == 0) {
                    mask |= (1L << getFlatIndex(x, y));
                    break;
                }
            }
        }

        return mask;
    }

    public long computeOpponentWinningPositions() {
        if (isItCrossTurn()) {
            return computeWinningPositions(circleGrid, crossGrid);
        } else {
            return computeWinningPositions(crossGrid, circleGrid);
        }
    }

    public long computeWinningPositions(long playerMask, long opponentMask) {
        long mask = 0L;

        for (int i = 0; i < WINNING_FILLED_POSITION_MASK.length; i++) {
            if ((playerMask & WINNING_FILLED_POSITION_MASK[i]) == WINNING_FILLED_POSITION_MASK[i]) {
                mask |= ~(playerMask | opponentMask) & WINNING_EMPTY_POSITION_MASK[i];
            }
        }

        return mask;
    }

    public boolean isDraw() {
        return nbMoves == FLAT_LENGTH;
    }

    public boolean isWinningState() {
        long playerMask = isItCrossTurn() ? circleGrid : crossGrid;

        for (long mask : WINNING_MASKS) {
            if ((playerMask & mask) == mask) {
                return true;
            }
        }

        return false;
    }

    public GameState getWinningState() {
        if (isItCrossTurn()) {
            for (long mask : WINNING_MASKS) {
                if ((circleGrid & mask) == mask) {
                    return new GameState(0, mask, 0, 0);
                }
            }
        } else {
            for (long mask : WINNING_MASKS) {
                if ((crossGrid & mask) == mask) {
                    return new GameState(mask, 0, 0, 0);
                }
            }
        }

        return null;
    }

    public char getWinner() {
        // Cross
        for (long mask : WINNING_MASKS) {
            if ((crossGrid & mask) == mask) {
                return 'X';
            }
        }

        // Circle
        for (long mask : WINNING_MASKS) {
            if ((circleGrid & mask) == mask) {
                return 'O';
            }
        }

        return ' ';
    }

    public int getNbMoves() {
        return nbMoves;
    }

    private static int getFlatIndex(int x, int y) {
        return x + y * GameState.GRID_WIDTH;
    }

    public int moveScore(long move) {
        if (isItCrossTurn()) {
            return Long.bitCount(computeWinningPositions(crossGrid | move, circleGrid));
        } else {
            return Long.bitCount(computeWinningPositions(circleGrid | move, crossGrid));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GameState state) {
            return state.crossGrid == this.crossGrid && state.circleGrid == this.circleGrid;
        } else return false;
    }

    public long longHashCode() {
        if (isItCrossTurn()) {
            return this.circleGrid + (this.crossGrid | this.circleGrid);
        } else {
            return this.crossGrid + (this.crossGrid | this.circleGrid);
        }
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", crossGrid, circleGrid);
    }

    public int getLastMove() {
        return lastMove;
    }
}
