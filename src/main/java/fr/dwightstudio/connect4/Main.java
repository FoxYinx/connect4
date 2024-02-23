package fr.dwightstudio.connect4;

import fr.dwightstudio.connect4.display.DisplayController;
import fr.dwightstudio.connect4.display.console.ASCIIRenderer;
import fr.dwightstudio.connect4.display.java2d.Java2DRenderer;
import fr.dwightstudio.connect4.game.GameController;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameController gameController;
        DisplayController displayController;
        String choice;

        System.out.println("ASCII or GPU-Intensive?");
        System.out.println("1 -> ascii");
        System.out.println("2 -> gPu-InTeNsIvE");
        choice = scanner.nextLine().strip();
        if (choice.equals("1")) {
            displayController = new ASCIIRenderer();
        } else if (choice.equals("2")) {
            displayController = new Java2DRenderer();
        } else {
            throw new RuntimeException("Make a valid choice dummy (1 or 2)");
        }
        gameController = new GameController(displayController);

        System.out.println("Howdy buddy, what ya wanna do? (learning is deprecated)");
        System.out.println("Play? (y/n)");
        choice = scanner.nextLine().strip();
        if (choice.equalsIgnoreCase("y")) gameController.play();
        if (choice.equalsIgnoreCase("learn")) crippy();
        throw new RuntimeException("You HAVE to play!");
    }

    private static void crippy() {
        System.out.println("What did you do?");
        System.out.println("How did you get here?");
        System.out.println("sudo -u");
        System.out.println("No, what r u doing?");
        System.out.println("rm -r ./");
        System.out.println("NO, DO NOT DO THAT, DON'T PUSH THAT BUTTON");
        System.out.println("Deleting in progress...");
        System.out.println("譁 \t� \t蟄 \t怜 \t喧 \t縺 \t�");
    }
}
