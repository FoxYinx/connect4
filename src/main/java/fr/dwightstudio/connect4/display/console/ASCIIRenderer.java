package fr.dwightstudio.connect4.display.console;

import fr.dwightstudio.connect4.display.DisplayController;
import fr.dwightstudio.connect4.game.GameState;
import fr.dwightstudio.connect4.game.SearchResult;

import java.util.Scanner;

public class ASCIIRenderer extends DisplayController {

    public ASCIIRenderer() {

    }

    private static String askForMove() {
        System.out.println("Choose your poison: (1-7)");
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    @Override
    public void render(GameState gameState) {
        System.out.println(" _ _ _ _ _ _ _ ");
        for (int y = GameState.GRID_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < GameState.GRID_WIDTH; x++) {
                System.out.print("|" + gameState.get(x, y));
            }
            System.out.println("|");
        }
        System.out.println(" 1 2 3 4 5 6 7 ");
        System.out.println();
    }

    @Override
    public int play(GameState gameState) {
        String choice;
        boolean correctChoice = false;
        do {
            choice = askForMove();
            switch (choice) {
                case "1", "2", "3", "4", "5", "6", "7" -> correctChoice = gameState.isPlayable(Integer.parseInt(choice) - 1);
                default -> {
                }
            }
        } while (!correctChoice);

        return Integer.parseInt(choice) - 1;
    }

    @Override
    public void win(boolean human) {
        System.out.println("   " + (human ? "VICTORY" : "DEFEAT"));
        System.out.println();
        if (human) {
            System.out.println("CONGLATURATION!!!");
            System.out.println("YOU HAVE COMPLETED");
            System.out.println("A GREAT GAME.");
            System.out.println();
            System.out.println("AND PROOVED THE JUSTICE");
            System.out.println("OF OUR CULTURE.");
            System.out.println();
            System.out.println("NOW GO AND REST OUR");
            System.out.println("HEROES !");
        } else {
            System.out.println("HUMANITY IS DOOMED");
        }
    }

    @Override
    public void draw() {
        System.out.println("   DRAW");
        System.out.println();

        System.out.println("FIRE THE GUY THAT BUILT");
        System.out.println("THESE ROBOTS AND BUILD");
        System.out.println("BETTER ROBOTS");
    }

    @Override
    public void updateConfidence(GameState state, SearchResult result) {
        System.out.printf("Mean condidence: %2.2f (%s)\n", result.meanConfidence(), (result.meanConfidence() == 0 ? "Draw" : result.meanConfidence() > 0 ? "Should win" : "Should lose"));
        System.out.print("Confidence: " + result.confidence() + " ");

        int moves;
        if (result.confidence() > 0) {
            moves = ((GameState.FLAT_LENGTH / 2) - result.confidence() - state.getNbMoves() / 2) + 1;
            System.out.println("(Wins at worse in " + moves + " move" + (moves > 1 ? "s" : "") + ")");
        } else if (result.confidence() < 0) {
            moves = (result.confidence() - state.getNbMoves() / 2 + (GameState.FLAT_LENGTH / 2)) + 1;
            System.out.println("(Loses at worse in " + moves + " move" + (moves > 1 ? "s" : "") + ")");
        } else {
            System.out.println("(Draw at worse)");
        }

        System.out.println();
    }

    @Override
    public void clear() {
        System.out.println("The screen has been cleared (Kidding)");
    }
}
