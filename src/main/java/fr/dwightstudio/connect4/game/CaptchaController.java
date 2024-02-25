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


            // On vérifie que l'IA ne peut pas gagner immédiatement
            for (int i = 0; i < GameState.GRID_WIDTH; i++) {
                if (state.isPlayable(i)) {
                    if (state.play(i).isWinningState()) {
                        state = state.play(i);
                        displayController.render(state);
                        displayController.win(false);
                        return;
                    }
                }
            }

            SearchResult result = search(state);

            state = state.play(result.move());

            displayController.render(state);

            displayController.updateConfidence(state, result.confidence());

            if (state.isDraw()) {
                displayController.draw();
                return;
            }
        }
    }
}
