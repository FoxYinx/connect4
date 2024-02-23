package fr.dwightstudio.connect4.game;

public record GamePosition(int x, int y) {

    public int getFlatIndex() {
        return x + y * GameState.GRID_WIDTH;
    }
}
