package fr.dwightstudio.connect4.display.console;

import fr.dwightstudio.connect4.display.DisplayController;
import fr.dwightstudio.connect4.game.GamePosition;
import fr.dwightstudio.connect4.game.GameState;

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
                System.out.print("|" + gameState.get(new GamePosition(x, y)));
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
        System.out.println();
        System.out.println("   DRAW");
        System.out.println();

        System.out.println("FIRE THE GUY THAT BUILT");
        System.out.println("THESE ROBOTS AND BUILD");
        System.out.println("BETTER ROBOTS");
    }

    @Override
    public void updateConfidence(int confidence) {
        System.out.println("Confidence: " + confidence);
    }

    @Override
    public void clear() {
        System.out.println("The screen has been cleared");
    }
}
