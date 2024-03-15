package fr.dwightstudio.connect4.display.awt;

import fr.dwightstudio.connect4.game.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import static fr.dwightstudio.connect4.display.awt.AWTRenderer.*;

public class BoardComponent extends JPanel {

    private static final int ANIM_DURATION = 1000;

    private final PlayLock playLock;
    private GameState currentState;
    private int lastMouseIndex;
    private char animSide;
    private long animStart;
    private Point animPos;

    public BoardComponent() {
        super();

        playLock = new PlayLock();
        animSide = ' ';
        animStart = 0;
        animPos = null;

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

        g2d.clearRect(0, 0, getWidth(), getHeight());

        int cellSize = getWidth() / GameState.GRID_WIDTH;
        int innerCellSize = (int) Math.floor(cellSize * INNER_CELL_RATIO);
        int innerCellPadding = (int) Math.floor((double) (cellSize - innerCellSize) / 2);

        // Draw animation
        if (animPos != null) {
            g2d.setColor(animSide == 'X' ? CROSS_COLOR : CIRCLE_COLOR);
            g2d.fillOval(cellSize * animPos.x + innerCellPadding,
                    (int) (cellSize * (GameState.GRID_HEIGHT - animPos.y - 1) + innerCellPadding - (System.currentTimeMillis() - animStart)),
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

        g2d.translate(cellSize - getWidth(), 0);
    }

    public void setState(GameState gameState) {
        animSide = gameState.isItCrossTurn() ? 'O' : 'X';
        animStart = System.currentTimeMillis();
        animPos = new Point(gameState.getLastMove(), gameState.getFreeHeight(gameState.getLastMove()) - 1);

        while (animStart + ANIM_DURATION > System.currentTimeMillis()) {
            repaint();
            try {
                Thread.sleep(ANIM_DURATION - (System.currentTimeMillis() - animStart) % 60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        animSide = ' ';
        animStart = 0;
        animPos = null;

        this.currentState = gameState;
    }

    public PlayLock getPlayLock() {
        return playLock;
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
