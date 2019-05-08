package com.JavaSnakes.states.game;

import com.JavaSnakes.Board;
import com.JavaSnakes.Main;
import com.JavaSnakes.snakes.PlayerSnake;
import com.JavaSnakes.snakes.SnakeBase;
import com.JavaSnakes.states.menu.MenuPanel;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.MenuCard;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GamePanel implements Runnable {

    private final Main owner;
    public JPanel mainPanel;

    private CardLayout cardLayout;

    private MainGamePanel mainGamePanel;

    private JButton continueButton;
    private JButton quitToMenuButton;

    private JButton backToMainButton;

    private final int delay;

    private final int cellSize;
    private final int miniCell;
    private final int miniOffset;

    private Board board;

    private boolean inGame;
    private Thread animator;

    public GamePanel(
            Main setOwner,
            int setDelay,
            int setCellSize,
            int setMapW,
            int setMapH,
            boolean includeWalls,
            List<SnakeBase> setSnakes
    ) {
        owner = setOwner;

        mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());

        mainGamePanel = new MainGamePanel();
        mainPanel.add(mainGamePanel, "game card");
        createEscapeCard();
        createEndCard();

        cardLayout = (CardLayout) mainPanel.getLayout();

        delay = setDelay;

        cellSize = setCellSize;
        miniOffset = 1;
        miniCell = cellSize - 2 * miniOffset;

        board = new Board(setMapW, setMapH, includeWalls);
        mainPanel.setPreferredSize(new Dimension(board.width * cellSize, board.height * cellSize));
        SnakeBase.board = board;

        board.snakes.addAll(setSnakes);
        board.liveSnakes.addAll(setSnakes);

        board.createFood();

        inGame = true;
        animator = new Thread(this);
        animator.start();
    }

    private void createEscapeCard() {
        continueButton = new JButton("Continue");
        continueButton.addActionListener(e -> toGameCard());
        quitToMenuButton = new JButton("Quit to main menu");
        quitToMenuButton.addActionListener(e -> toMainMenu());

        MenuCard gridBag = new MenuCard();
        gridBag.addInGrid(continueButton, 0, 0);
        gridBag.addInGrid(quitToMenuButton, 1, 0);

        mainPanel.add(gridBag, "escape card");
    }

    private void createEndCard() {
        backToMainButton = new JButton("Back to main menu");
        backToMainButton.addActionListener(e -> toMainMenu());
        JLabel gameOverLabel = new JLabel("Game Over");

        MenuCard gridBag = new MenuCard();
        gridBag.addInGrid(backToMainButton, 0, 0);
        gridBag.addInGrid(gameOverLabel, 1, 0);

        mainPanel.add(gridBag, "end card");
    }

    private void toMainMenu() {
        owner.changePanel(new MenuPanel(owner).mainPanel);
    }

    private void toGameCard() {
        cardLayout.show(mainPanel, "game card");
    }

    private void toEscapeCard() {
        cardLayout.show(mainPanel, "escape card");
        continueButton.requestFocus();
    }

    @Override
    public void run() {
        long beforeTime = System.currentTimeMillis();
        frameDelay(beforeTime);

        while (inGame) {
            beforeTime = System.currentTimeMillis();

            gameLogic();
            mainPanel.paintImmediately(mainPanel.getBounds());

            frameDelay(beforeTime);
        }
    }

    private void gameLogic() {
        for (SnakeBase snake : board.liveSnakes)
            snake.processDirection();
        for (SnakeBase snake : board.liveSnakes)
            snake.moveHead();

        board.checkCollisions();
        board.killCollidedSnakes();
        if (board.liveSnakes.size() == 0) inGame = false;

        board.checkFood();
        for (SnakeBase snake : board.liveSnakes)
            snake.removeTailEnd();
    }

    private void frameDelay(long beforeTime) {
        long sleep = delay - (System.currentTimeMillis() - beforeTime);
        if (sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
            }
        }
    }

    private class MainGamePanel extends JPanel {

        private MainGamePanel() {
            addKeyListener(new Input());
            setBackground(Color.darkGray);
            setFocusable(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (inGame) {
                drawFood(g);
                drawSnakes(g);
                drawWalls(g);
            } else {
                drawGameOver(g);
            }

            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        }

        private void drawFood(Graphics g) {
            g.setColor(Color.red);
            drawMiniSquare(g, board.foodPos);
        }

        private void drawSnakes(Graphics g) {
            for (SnakeBase snake : board.liveSnakes) {
                g.setColor(snake.color);

                drawFullSquare(g, snake.headPos());
                for (Iterator<GridPos> iter = snake.coords.listIterator(1); iter.hasNext(); ) {
                    drawMiniSquare(g, iter.next());
                }
            }
        }

        private void drawWalls(Graphics g) {
            g.setColor(Color.black);
            for (int x = 0; x < board.width; x++) {
                for (int y = 0; y < board.height; y++) {
                    if (board.walls[x][y]) {
                        drawFullSquare(g, x, y);
                    }
                }
            }
        }

        private void drawFullSquare(Graphics g, GridPos coord) {
            drawFullSquare(g, coord.x, coord.y);
        }

        private void drawFullSquare(Graphics g, int x, int y) {
            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
        }

        private void drawMiniSquare(Graphics g, GridPos coord) {
            g.fillRect(coord.x * cellSize + miniOffset, coord.y * cellSize + miniOffset, miniCell, miniCell);
        }

        private void drawGameOver(Graphics g) {
            Font small = new Font("Helvetica", Font.BOLD, 14);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.white);
            g.setFont(small);
            int fontHeight = g.getFontMetrics().getHeight();

            String[] msgs = new String[board.snakes.size() + 1];
            msgs[0] = "Game Over";
            for (int i = 0; i < board.snakes.size(); i++) {
                msgs[i + 1] = "Snake " + board.snakes.get(i).id + ": " + board.snakes.get(i).score;
            }

            int yOffset = fontHeight * (board.snakes.size() / 2);
            for (int i = 0; i < msgs.length; i++) {
                if (i > 0) g.setColor(board.snakes.get(i-1).color);
                g.drawString(
                        msgs[i],
                        (getWidth() - metr.stringWidth(msgs[i])) / 2,
                        getHeight() / 2 - yOffset + i * fontHeight
                );
            }
        }

        private class Input extends KeyAdapter {

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_ESCAPE)
                    System.exit(0);

                for (SnakeBase snake : board.liveSnakes) {
                    if (!(snake instanceof PlayerSnake)) continue;
                    PlayerSnake playerSnake = (PlayerSnake) snake;
                    playerSnake.bufferInput(key);
                }
            }
        }
    }
}
