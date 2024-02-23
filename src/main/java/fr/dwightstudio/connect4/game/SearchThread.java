package fr.dwightstudio.connect4.game;

public class SearchThread extends Thread {

    private int nb;
    private final Runnable nbUpdater = () -> nb++;
    private final GameState initialState;
    private int result;

    public SearchThread(GameState initialState) {
        this.initialState = initialState;
    }

    @Override
    public void run() {
        result = GameController.negamax(initialState, Integer.MIN_VALUE, Integer.MAX_VALUE, nbUpdater);
    }


    public int getResult() {
        return result;
    }

    public int getNb() {
        return nb;
    }
}
