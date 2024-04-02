package fr.dwightstudio.connect4;

import fr.dwightstudio.connect4.display.DisplayController;
import fr.dwightstudio.connect4.display.console.ASCIIRenderer;
import fr.dwightstudio.connect4.display.awt.AWTRenderer;
import fr.dwightstudio.connect4.game.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameController gameController;
        DisplayController displayController;
        String choice;

        if (args.length >= 1) {
            choice = args[0];
        } else {

            System.out.println("1 -> ASCII Renderer");
            System.out.println("2 -> gPu-InTeNsIvE Renderer");
            System.out.println();
            System.out.println("ASCII or GPU-Intensive?");
            System.out.print("> ");

            choice = scanner.nextLine().strip();
        }

        if (choice.equals("1")) {
            displayController = new ASCIIRenderer();
        } else if (choice.equals("2")) {
            displayController = new AWTRenderer();
        } else {
            throw new RuntimeException("Make a valid choice dummy (1 or 2)");
        }

        if (args.length >= 2) {
            choice = args[1];
        } else {
            System.out.println();
            System.out.println("1 -> Captcha (Human/AI)");
            System.out.println("2 -> Fight (AI/AI)");
            System.out.println("3 -> PVP (Human/Human)");
            System.out.println("4 -> Board Creator");
            System.out.println("5 -> Test");
            System.out.println();
            System.out.println("Versus or Against itself?");
            System.out.print("> ");

            choice = scanner.nextLine().strip();
        }

        if (choice.equals("1")) {
            gameController = new CaptchaController(displayController);
        } else if (choice.equals("2")) {
            gameController = new FightController(displayController);
        } else if (choice.equals("3")) {
                gameController = new PVPController(displayController);
        } else if (choice.equals("4")) {
            gameController = new BoardCreatorController(displayController);
        } else if (choice.equals("5")) {
                gameController = new TestController(displayController);
        } else if (choice.equalsIgnoreCase("learn")) {
            creepy();
            return;
        } else {
            throw new RuntimeException("Make a valid choice dummy (1 or 2 or 3)");
        }

        System.out.println();

        gameController.play();
    }

    private static void creepy() {
        System.out.println("What did you do?");
        System.out.println("How did you get here?");
        System.out.println("> sudo -u");
        System.out.println("No, what r u doing?");
        System.out.println("> rm -r ./*");
        System.out.println("NO, DO NOT DO THAT, DON'T PUSH THAT BUTTON");
        System.out.println("Deleting in progress...");
        System.out.println("譁 \t� \t蟄 \t怜 \t喧 \t縺 \t�");
        throw new RuntimeException();
    }
}
