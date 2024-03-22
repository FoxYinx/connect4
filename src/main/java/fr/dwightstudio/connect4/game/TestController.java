package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

public class TestController extends GameController {
    public TestController(DisplayController displayController) {
        super(displayController);
    }

    @Override
    public void play() {
        for (int i = 0; i < GameState.COLUMN_MASK.length; i++) {
            displayController.render(new GameState(GameState.COLUMN_MASK[i], 0L, 0, 0));
        }
    }

    /*
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

            // TEST
            displayController.play(state);
            GameState tmp = new GameState(state.possibleNonLosingMoves(), 0L, 0, 0);
            displayController.render(tmp);
            displayController.play(tmp);
            tmp = new GameState(state.computeOpponentWinningPositions(), 0L, 0, 0);
            displayController.render(tmp);
            displayController.play(tmp);
            tmp = new GameState(state.computePossibleMoves(), 0L, 0, 0);
            displayController.render(tmp);
            displayController.play(tmp);
            displayController.render(state);

            SearchResult result = search(state);

            state = state.play(result.move());

            displayController.render(state);
            displayController.updateConfidence(state, result);

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
     */
}
