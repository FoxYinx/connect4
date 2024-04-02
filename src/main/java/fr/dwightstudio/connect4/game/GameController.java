package fr.dwightstudio.connect4.game;

import fr.dwightstudio.connect4.display.DisplayController;
import fr.dwightstudio.connect4.search.SearchResult;
import fr.dwightstudio.connect4.search.SearchThread;
import fr.dwightstudio.connect4.search.SimpleSearchThread;
import fr.dwightstudio.connect4.search.SolverSearchThread;

import static fr.dwightstudio.connect4.search.SolverSearchThread.COLUMN_ORDER;

abstract public class GameController {

    public static final int MASTERMIND_THRESHOLD = 8;

    protected final DisplayController displayController;
    protected GameState state;

    public GameController(DisplayController displayController) {
        this.displayController = displayController;
        state = new GameState();
    }

    abstract public void play();

    public SearchResult search(GameState state) {
        // On vérifie que l'IA ne peut pas gagner immédiatement
        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            if (state.isPlayable(i)) {
                if (state.play(i).isWinningState()) {
                    return new SearchResult(i, (GameState.FLAT_LENGTH + 1 - state.getNbMoves()) / 2);
                }
            }
        }

        if (state.getNbMoves() < MASTERMIND_THRESHOLD) {
            return simpleSearch(state);
        } else {
            if (state.getNbMoves() == MASTERMIND_THRESHOLD) SolverSearchThread.TRANSPOSITION_TABLE.clear();
            return thoroughSearch(state);
        }
    }

    private SearchResult simpleSearch(GameState state) {
        if (state.getNbMoves() == 0) return new SearchResult(GameState.GRID_WIDTH / 2, (GameState.FLAT_LENGTH + 1) / 2);

        SimpleSearchThread[] searchThreads = new SimpleSearchThread[GameState.GRID_WIDTH];

        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            if (state.isPlayable(i)) {
                GameState state2 = state.play(i);
                searchThreads[i] = new SimpleSearchThread(state2);
                searchThreads[i].start();
            }
        }

        return doSearch(searchThreads);
    }

    private SearchResult thoroughSearch(GameState state) {
        SolverSearchThread[] searchThreads = new SolverSearchThread[GameState.GRID_WIDTH];

        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            if (state.isPlayable(i)) {
                GameState state2 = state.play(i);
                searchThreads[i] = new SolverSearchThread(state2);
                searchThreads[i].start();
            }
        }

        return doSearch(searchThreads);
    }

    private SearchResult doSearch(SearchThread[] searchThreads) {
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
                    System.out.printf(" [ %9s]", "Done");
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
                if (state.isPlayable(COLUMN_ORDER[i])) {
                    best = COLUMN_ORDER[i];
                    break;
                }
            }
        }
        return new SearchResult(best, bestScore, (float) meanScore / (float) GameState.GRID_WIDTH);
    }
}
