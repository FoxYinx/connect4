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
        result = -GameController.negamax(initialState, -GameState.FLAT_LENGTH/2, GameState.FLAT_LENGTH/2, nbUpdater);
    }

    public int getResult() {
        return result;
    }

    public int getNb() {
        return nb;
    }
}
