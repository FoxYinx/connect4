package fr.dwightstudio.connect4.search;

import fr.dwightstudio.connect4.game.GameState;

public abstract class SearchThread extends Thread {

    protected long nb;
    protected final GameState initialState;
    protected int result;

    protected SearchThread(GameState initialState) {
        this.initialState = initialState;
        this.nb = 0;
    }

    public long getNb() {
        return nb;
    }

    public int getResult() {
        return result;
    }
}
