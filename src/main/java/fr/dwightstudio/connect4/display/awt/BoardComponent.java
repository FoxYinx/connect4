package fr.dwightstudio.connect4.display.awt;

import fr.dwightstudio.connect4.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;

import static fr.dwightstudio.connect4.display.awt.AWTRenderer.*;

public class BoardComponent extends JPanel {

    private static final int ANIM_DURATION = 350;

    private final PlayLock playLock;
    private GameState currentState;
    private boolean won;
    private int lastMouseIndex;
    private char animSide;
    private long animStart;
    private Point animPos;
    private String confidenceString;

    public BoardComponent() {
        super();

        playLock = new PlayLock();
        animSide = ' ';
        animStart = 0;
        animPos = null;
        confidenceString = "";

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playLock.setPlayed();
                repaint();
                synchronized (playLock) {
                    playLock.notify();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lastMouseIndex = -1;
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int cellSize = getWidth() / GameState.GRID_WIDTH;
                int index = Math.floorDiv(e.getPoint().x, cellSize);

                if (index < 0 || index >= GameState.GRID_WIDTH) index = -1;

                if (index != -1 && lastMouseIndex != index) {
                    lastMouseIndex = index;
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;

        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHints(hints);

        g2d.clearRect(0, 0, getWidth(), getHeight());

        int cellSize = getWidth() / GameState.GRID_WIDTH;
        int innerCellSize = (int) Math.floor(cellSize * INNER_CELL_RATIO);
        int innerCellPadding = (int) Math.floor((double) (cellSize - innerCellSize) / 2);

        // Draw animation
        if (animPos != null) {
            g2d.setColor(animSide == 'X' ? CROSS_COLOR : CIRCLE_COLOR);
            g2d.fillOval(cellSize * animPos.x + innerCellPadding,
                    cellSize * (GameState.GRID_HEIGHT - animPos.y - 1) + innerCellPadding - (int) Math.round(getHeight() * (1d - ((double) (System.currentTimeMillis() - animStart) / (double) ANIM_DURATION))),
                    innerCellSize,
                    innerCellSize);
        }

        // Draw moves
        if (currentState != null) {
            for (int x = 0; x < GameState.GRID_WIDTH; x++) {
                for (int y = 0; y < GameState.GRID_WIDTH; y++) {
                    int i = currentState.get(x, y);

                    if (i == ' ') continue;
                    g2d.setColor(i == 'X' ? CROSS_COLOR : CIRCLE_COLOR);

                    g2d.fillOval(cellSize * x + innerCellPadding,
                            cellSize * (GameState.GRID_HEIGHT - y - 1) + innerCellPadding,
                            innerCellSize,
                            innerCellSize);
                }
            }
        }

        // Draw winning moves
        if (won) {
            final GameState winningState = currentState.getWinningState();

            if (winningState != null) {
                g2d.setColor(WINNING_COLOR);

                for (int x = 0; x < GameState.GRID_WIDTH; x++) {
                    for (int y = 0; y < GameState.GRID_WIDTH; y++) {
                        int i = winningState.get(x, y);

                        if (i == ' ') continue;

                        System.out.println(" ");
                        g2d.fillRoundRect(cellSize * x + cellSize / 3, cellSize * (GameState.GRID_HEIGHT - y - 1) + cellSize / 3, cellSize / 3, cellSize / 3, innerCellPadding, innerCellPadding );
                    }
                }
            }
        }

        // Draw board
        Area boardSection = new Area(new Rectangle(0, 0, getWidth() / GameState.GRID_WIDTH, getHeight()));
        for (int j = 0; j < GameState.GRID_HEIGHT; j++) {
            boardSection.subtract(new Area(new Ellipse2D.Double(innerCellPadding, cellSize * j + innerCellPadding, innerCellSize, innerCellSize)));
        }

        for (int i = 0; i < GameState.GRID_WIDTH; i++) {
            g2d.setColor(i == lastMouseIndex ? SELECTED_BACKGROUND_COLOR : BACKGROUND_COLOR);
            g2d.fill(boardSection);
            g2d.translate(cellSize, 0);
        }

        g2d.fillRect(0, 0, cellSize, getHeight());

        g2d.translate(- getWidth(), 0);

        // Draw information
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, cellSize/3));
        g2d.drawString(confidenceString, cellSize/2, cellSize * GameState.GRID_HEIGHT + cellSize/2);
    }

    public void setState(GameState gameState) {
        animSide = gameState.isItCrossTurn() ? 'O' : 'X';
        animStart = System.currentTimeMillis();
        animPos = new Point(gameState.getLastMove(), gameState.getFreeHeight(gameState.getLastMove()) - 1);

        while (animStart + ANIM_DURATION > System.currentTimeMillis()) {
            repaint();
        }

        animSide = ' ';
        animStart = 0;
        animPos = null;

        this.currentState = gameState;
    }

    public PlayLock getPlayLock() {
        return playLock;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public void setConfidenceString(String confidenceString) {
        this.confidenceString = confidenceString;
    }

    public class PlayLock {
        private int move;

        private void setPlayed() {
            this.move = lastMouseIndex;
        }

        public int getMove() {
            return move;
        }
    }
}
