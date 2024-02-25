package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

public class FightController extends GameController {
    public FightController(DisplayController displayController) {
        super(displayController);
    }

    @Override
    public void play() {
        displayController.render(state);

        while (true) {
            SearchResult result = search(state);

            state = state.play(result.move());

            displayController.render(state);
            displayController.updateConfidence(state, result.confidence());

            System.out.println();

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
