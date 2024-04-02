package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;
import fr.dwightstudio.connect4.search.SearchResult;

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
            displayController.updateConfidence(state, result);

            System.out.println();

            if (state.isWinningState()) {
                displayController.win(false);
                return;
            }

            if (state.isDraw()) {
                displayController.draw();
                return;
            }
        }
    }
}
