package com.JavaSnakes;

import com.JavaSnakes.Snakes.BotSnake;
import com.JavaSnakes.Snakes.PlayerSnake;
import com.JavaSnakes.Snakes.SnakeBase;
import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;

public class GameLoop extends JPanel implements Runnable {

    private static final int CELL_SIZE = 10;
    private static final int DELAY = 150;

    private Board board;

    private boolean inGame;
    private Thread animator;

    public GameLoop() {
        addKeyListener(new Input());
        setBackground(Color.darkGray);
        setFocusable(true);

        this.board = new Board(30, 30);
        SnakeBase.board = board;

        setPreferredSize(new Dimension(board.width * CELL_SIZE, board.height * CELL_SIZE));
        initGame(true);
    }

    private void initGame(boolean includeWalls) {
        if (includeWalls) {
            for (int x = 0; x < board.width; x++) {
                for (int y = 0; y < board.height; y++) {
                    board.walls[x][y] = (x == 0 || x == board.width - 1 || y == 0 || y == board.height - 1);
                }
            }
        }

        board.createFood();

        board.snakes.add(new PlayerSnake(
            Direction.Right, new GridPos(3, 3), Color.blue,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
        ));
        board.snakes.add(new PlayerSnake(
            Direction.Left, new GridPos(board.width - 4, board.height - 4), Color.cyan,
            KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D
        ));
        board.snakes.add(new BotSnake(Direction.Down, new GridPos(board.width - 4, 3), Color.yellow));
        board.snakes.add(new BotSnake(Direction.Up, new GridPos(3, board.height - 4), Color.green));
        board.liveSnakes.addAll(board.snakes);

        this.inGame = true;
        this.animator = new Thread(this);
        animator.start();
    }

    @Override
    public void run() {
        while (inGame) {
            long beforeTime = System.currentTimeMillis();

            gameLogic();
            paintImmediately(getBounds());

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
        long sleep = DELAY - (System.currentTimeMillis() - beforeTime);
        if (sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {}
        }
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
        g.fillRect(board.foodPos.x * CELL_SIZE + 1, board.foodPos.y * CELL_SIZE + 1, 8, 8);
    }

    private void drawSnakes(Graphics g) {
        for (SnakeBase snake : board.liveSnakes) {
            g.setColor(snake.color);

            GridPos coord = snake.coords.getFirst();
            g.fillRect(coord.x * CELL_SIZE, coord.y * CELL_SIZE, 10, 10);
            for (Iterator<GridPos> iter = snake.coords.listIterator(1); iter.hasNext(); ) {
                coord = iter.next();
                g.fillRect(coord.x * CELL_SIZE + 1, coord.y * CELL_SIZE + 1, 8, 8);
            }
        }
    }

    private void drawWalls(Graphics g) {
        g.setColor(Color.black);
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                if (board.walls[x][y]) {
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, 10, 10);
                }
            }
        }
    }

    private void drawGameOver(Graphics g) {
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);

        String[] msgs = new String[board.snakes.size() + 1];
        msgs[0] = "Game Over";
        for (int i = 0; i < board.snakes.size(); i++) {
            msgs[i + 1] = "Snake " + board.snakes.get(i).id + ": " + board.snakes.get(i).score;
        }

        int yOffset = g.getFontMetrics().getHeight() * (board.snakes.size() / 2);
        for (int i = 0; i < msgs.length; i++) {
            if (i > 0) g.setColor(board.snakes.get(i-1).color);
            g.drawString(
                msgs[i],
                (getWidth() - metr.stringWidth(msgs[i])) / 2,
                getHeight() / 2 - yOffset + i * g.getFontMetrics().getHeight()
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

                if (playerSnake.ctrlKeys.containsKey(key)) {
                    playerSnake.directionBuffer = playerSnake.ctrlKeys.get(key);
                }
            }
        }
    }
}
