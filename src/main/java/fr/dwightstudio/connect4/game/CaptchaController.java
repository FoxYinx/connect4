package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

public class CaptchaController extends GameController {
    public CaptchaController(DisplayController displayController) {
        super(displayController);
    }

    @Override
    public void play() {
        displayController.render(state);

        while (true) {
            state = state.play(displayController.play(state));

            displayController.render(state);

            // On vérifie que la partie ne soit pas terminée
            if (state.isDraw()) {
                displayController.draw();
                return;
            }

            if (state.isWinningState()) {
                displayController.win(true);
                return;
            }

            SearchResult result = search(state);

            state = state.play(result.move());

            displayController.render(state);
            displayController.updateConfidence(state, result);

            if (state.isDraw()) {
                displayController.draw();
                return;
            }

            if (state.isWinningState()) {
                displayController.win(false);
                return;
            }
        }
    }
}
