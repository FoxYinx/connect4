package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

public class BoardCreatorController extends GameController {
    public BoardCreatorController(DisplayController displayController) {
        super(displayController);
    }

    @Override
    public void play() {
        while (true) {
            displayController.render(state);
            state = state.play(displayController.play(state));
            System.out.println(state.toString());
        }
    }
}
