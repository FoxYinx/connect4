package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

public class PVPController extends GameController {
    public PVPController(DisplayController displayController) {
        super(displayController);
    }

    @Override
    public void play() {
        displayController.render(state);

        while (true) {
            state = state.play(displayController.play(state));

            displayController.render(state);

            // On vérifie que la partie ne soit pas terminée
            if (state.isWinningState()) {
                displayController.win(true);
                return;
            }

            if (state.isDraw()) {
                displayController.draw();
                return;
            }
        }
    }
}
