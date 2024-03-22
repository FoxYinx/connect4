package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

public class TestController extends GameController {
    public TestController(DisplayController displayController) {
        super(displayController);
    }

    @Override
    public void play() {
        for (int i = 0; i < GameState.WINNING_FILLED_POSITION_MASK.length; i++) {
            displayController.render(new GameState(GameState.WINNING_FILLED_POSITION_MASK[i], GameState.WINNING_EMPTY_POSITION_MASK[i], 0, 0));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
