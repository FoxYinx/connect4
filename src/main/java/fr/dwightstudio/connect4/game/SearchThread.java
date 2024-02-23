package fr.dwightstudio.connect4.game;

public class SearchThread extends Thread {

    private final GameState initialState;
    private int result;

    public SearchThread(GameState initialState) {
        this.initialState = initialState;
    }

    @Override
    public void run() {
        result = GameController.negamax(initialState);
    }


    public int getResult() {
        return result;
    }
}
