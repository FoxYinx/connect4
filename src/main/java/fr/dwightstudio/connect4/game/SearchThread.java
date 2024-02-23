package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

public class SearchThread extends Thread {

    private final GameState initialState;
    private int result;

    public SearchThread(GameState initialState) {
        this.initialState = initialState;
    }

    public SearchThread(GameState initialState, DisplayController displayController) {
        this(initialState);

    }

    @Override
    public void run() {
        result = GameController.negamax(initialState, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }


    public int getResult() {
        return result;
    }
}
