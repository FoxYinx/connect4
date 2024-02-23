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
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    @Override
    public void render(GameState gameState) {
        for (int i = GameState.GRID_HEIGHT - 1; i >= 0; i--) {
            System.out.println(" _ _ _ _ _ _ _ ");
            for (int j = 0; j < GameState.GRID_WIDTH; j++) {
                System.out.print("|" + gameState.get(new GamePosition(i, j)));
            }
            System.out.println("|");
        }
        System.out.println(" _ _ _ _ _ _ _ ");
    }

    @Override
    public int play(GameState gameState) {
        render(gameState);

        String choice;
        boolean correctChoice = false;
        do {
            choice = askForMove();
            switch (choice) {
                case "1", "2", "3", "4", "5", "6", "7" -> correctChoice = true;
                default -> {
                }
            }
        } while (!correctChoice);

        return Integer.parseInt(choice) - 1;
    }

    @Override
    public void win(boolean human) {
        if (human) {
            System.out.println();
            System.out.println("AND PROOVED THE JUSTICE");
            System.out.println("OF OUR CULTURE");
            System.out.println();
            System.out.println("NOW GO AND REST OUR");
            System.out.println("HEROES !");
        } else {
            System.out.println("HUMANITY IS DOOMED");
        }
    }

    @Override
    public void clear() {
        System.out.println("The screen has been cleared");
    }
}
