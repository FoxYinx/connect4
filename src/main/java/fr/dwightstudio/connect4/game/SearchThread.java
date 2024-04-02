package fr.dwightstudio.connect4.game;

public abstract class SearchThread extends Thread {

    protected long nb;
    protected final GameState initialState;
    protected int result;

    protected SearchThread(GameState initialState) {
        this.initialState = initialState;
        this.nb = 0;
    }

    long getNb() {
        return nb;
    }

    public int getResult() {
        return result;
    }
}
