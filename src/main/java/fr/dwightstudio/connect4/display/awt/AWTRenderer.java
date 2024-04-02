package fr.dwightstudio.connect4.display.awt;

import fr.dwightstudio.connect4.display.DisplayController;
import fr.dwightstudio.connect4.game.GameController;
import fr.dwightstudio.connect4.game.GameState;
import fr.dwightstudio.connect4.search.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AWTRenderer extends DisplayController {

    static final int DEFAULT_CELL_SIZE = 100;
    static final int DEFAULT_WIDTH = GameState.GRID_WIDTH * DEFAULT_CELL_SIZE;
    static final int DEFAULT_HEIGHT = GameState.GRID_HEIGHT * DEFAULT_CELL_SIZE;
    static final double INNER_CELL_RATIO = 0.8;

    static final Color BACKGROUND_COLOR = new Color(23, 112, 255);
    static final Color SELECTED_BACKGROUND_COLOR = new Color(79, 147, 255);
    static final Color CIRCLE_COLOR = new Color(255, 224, 51);
    static final Color CROSS_COLOR = new Color(255, 79, 48);
    static final Color WINNING_COLOR = new Color(80, 255, 37);

    private final JFrame frame;
    private final BoardComponent boardComponent;

    public AWTRenderer() {
        frame = new JFrame("Connect4");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((screenSize.width - DEFAULT_WIDTH) / 2, (screenSize.height - DEFAULT_HEIGHT) / 2, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        boardComponent = new BoardComponent();
        frame.getContentPane().add(boardComponent);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boardComponent.setBounds(getBoardBounds());
            }
        });

        frame.setVisible(true);
    }

    private Rectangle getBoardBounds() {
        int candidateWidth = frame.getWidth();
        int candidateHeight = (int) Math.floor(((double) candidateWidth / (double) GameState.GRID_WIDTH) * (GameState.GRID_HEIGHT + 1));

        if (candidateHeight <= frame.getHeight()) {
            return new Rectangle(0, (frame.getHeight() - candidateHeight) / 2, candidateWidth, candidateHeight);
        } else {
            candidateHeight = frame.getHeight();
            candidateWidth = (int) Math.floor(((double) candidateHeight / (double) (GameState.GRID_HEIGHT + 1)) * GameState.GRID_WIDTH);

            return new Rectangle((frame.getWidth() - candidateWidth) / 2, 0, candidateWidth, candidateHeight);
        }
    }

    @Override
    public void render(GameState gameState) {
        boardComponent.setState(gameState);
    }

    @Override
    public int play(GameState gameState) {
        BoardComponent.PlayLock playLock = boardComponent.getPlayLock();

        try {
            synchronized (playLock) {
                playLock.wait();
            }

            return playLock.getMove();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void win(boolean human) {
        boardComponent.setWon(true);
    }

    @Override
    public void draw() {

    }

    @Override
    public void updateConfidence(GameState state, SearchResult result) {
        StringBuilder confidenceString = new StringBuilder();
        confidenceString.append("Confidence: ").append(result.confidence()).append(" ");

        int moves;
        int confidence = result.confidence();

        if (state.getNbMoves() > GameController.MASTERMIND_THRESHOLD) {
            if (result.confidence() > 0) {
                moves = ((GameState.FLAT_LENGTH / 2) - confidence - state.getNbMoves() / 2) + 1;
                confidenceString.append("(Wins at worse in ").append(moves).append(" move").append(moves > 1 ? "s" : "").append(")");
            } else if (confidence < 0) {
                moves = (confidence - state.getNbMoves() / 2 + (GameState.FLAT_LENGTH / 2)) + 1;
                confidenceString.append("(Loses at worse in ").append(moves).append(" move").append(moves > 1 ? "s" : "").append(")");
            } else {
                confidenceString.append("(Draw at worse)");
            }
        } else {
            confidenceString.append("(Setting up the strategy...)");
        }

        boardComponent.setConfidenceString(confidenceString.toString());
    }

    @Override
    public void clear() {
        boardComponent.setWon(false);
        render(null);
    }
}
