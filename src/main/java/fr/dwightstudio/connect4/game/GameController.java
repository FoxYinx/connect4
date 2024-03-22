package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;

abstract public class GameController {

    protected final DisplayController displayController;
    protected GameState state;

    public GameController(DisplayController displayController) {
        this.displayController = displayController;
        state = new GameState();
    }

    abstract public void play();

    public SearchResult search(GameState state) {
        SearchThread[] searchThreads = new SearchThread[GameState.GRID_WIDTH];

        // On vérifie que l'IA ne peut pas gagner immédiatement
        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            if (state.isPlayable(i)) {
                if (state.play(i).isWinningState()) {
                    return new SearchResult(i, (GameState.FLAT_LENGTH + 1 - state.getNbMoves()) / 2);
                }
            }
        }

        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            if (state.isPlayable(i)) {
                GameState state2 = state.play(i);
                searchThreads[i] = new SearchThread(state2);
                searchThreads[i].start();
            }
        }

        final int WAITING_MILLIS = 100;

        boolean done = false;
        long lastTotal;
        long total = 0;

        long start = System.currentTimeMillis();

        while (!done) {
            done = true;
            lastTotal = total;
            total = 0;

            System.out.print("\rSearching...  Threads: ");
            for (SearchThread searchThread : searchThreads) {
                if (searchThread == null) continue;
                long nb = searchThread.getNb() / 1000;
                total += nb;
                if (!searchThread.isAlive()) {
                    System.out.printf(" [%9s]", "Done");
                } else {
                    done = false;
                    System.out.printf(" [%,9dk]", nb);
                }
            }

            System.out.printf("  Total: %,12dk at", total);

            System.out.printf("  %,9dk states/s", (total - lastTotal) * 1000 / WAITING_MILLIS);

            System.out.printf("  %4.2fs elapsed", (float) (System.currentTimeMillis() - start) / 1000f);

            try {
                Thread.sleep(WAITING_MILLIS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println();

        int best = -1;
        int bestScore = -(GameState.FLAT_LENGTH - state.getNbMoves()) / 2;
        int meanScore = 0;

        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            if (searchThreads[i] == null) continue;
            int score = searchThreads[i].getResult();
            meanScore += score;
            if (score > bestScore) {
                bestScore = score;
                best = i;
            }
        }

        if (best == -1) {
            for (int i = 0; i < GameState.GRID_WIDTH; i++) {
                if (state.isPlayable(SearchThread.COLUMN_ORDER[i])) {
                    best = SearchThread.COLUMN_ORDER[i];
                    break;
                }
            }
        }
        return new SearchResult(best, bestScore, (float) meanScore / (float) GameState.GRID_WIDTH);
    }
}
