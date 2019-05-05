package com.JavaSnakes.panels;

import com.JavaSnakes.Board;
import com.JavaSnakes.snakes.BotSnake;
import com.JavaSnakes.snakes.PlayerSnake;
import com.JavaSnakes.snakes.SnakeBase;
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

public class GamePanel extends JPanel implements Runnable {

    private final int delay;

    private final int cellSize;
    private final int miniCell;
    private final int miniOffset;

    private final Board board;

    private boolean inGame;
    private Thread animator;

    public GamePanel(int setDelay, int setCellSize) {
        this.delay = setDelay;

        this.cellSize = setCellSize;
        this.miniOffset = 1;
        this.miniCell = cellSize - 2 * miniOffset;

        addKeyListener(new Input());
        setBackground(Color.darkGray);
        setFocusable(true);

        this.board = new Board(30, 20, false);
        SnakeBase.board = board;

        setPreferredSize(new Dimension(board.width * cellSize, board.height * cellSize));

        board.snakes.add(new PlayerSnake(
            Direction.Right, new GridPos(3, 3), Color.blue,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
        ));
        board.snakes.add(new BotSnake(
            Direction.Left, new GridPos(board.width - 4, board.height - 4), Color.yellow
        ));
        board.liveSnakes.addAll(board.snakes);

        board.createFood();

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
        long sleep = delay - (System.currentTimeMillis() - beforeTime);
        if (sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
            }
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
