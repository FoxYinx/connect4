package fr.dwightstudio.connect4;

import fr.dwightstudio.connect4.display.DisplayController;
import fr.dwightstudio.connect4.display.console.ASCIIRenderer;
import fr.dwightstudio.connect4.display.awt.AWTRenderer;
import fr.dwightstudio.connect4.game.FightController;
import fr.dwightstudio.connect4.game.GameController;
import fr.dwightstudio.connect4.game.GameState;
import fr.dwightstudio.connect4.game.CaptchaController;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameController gameController;
        DisplayController displayController = null;
        String choice;

        System.out.println("1 -> ASCII Renderer");
        System.out.println("2 -> gPu-InTeNsIvE Renderer");
        System.out.println();
        System.out.println("ASCII or GPU-Intensive?");
        System.out.print("> ");

        choice = scanner.nextLine().strip();
        if (choice.equals("1")) {
            displayController = new ASCIIRenderer();
        } else if (choice.equals("2")) {
            displayController = new AWTRenderer();
        } else {
            throw new RuntimeException("Make a valid choice dummy (1 or 2)");
        }

        System.out.println();
        System.out.println("1 -> Captcha (Human/AI)");
        System.out.println("2 -> Fight (AI/AI)");
        System.out.println();
        System.out.println("Versus or Against itself?");
        System.out.print("> ");

        choice = scanner.nextLine().strip();
        if (choice.equals("1")) {
            gameController = new CaptchaController(displayController);
        } else if (choice.equals("2")) {
            gameController = new FightController(displayController);
        } else if (choice.equalsIgnoreCase("learn")) {
            crippy();
            return;
        } else {
            throw new RuntimeException("Make a valid choice dummy (1 or 2 or 3)");
        }

        System.out.println();

        gameController.play();
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
        throw new RuntimeException();
    }

    private static void create(DisplayController controller) {
        GameState state = new GameState();

        while (true) {
            int x = controller.play(state);
            state = state.play(x);
            System.out.println(state);
        }
    }
}
