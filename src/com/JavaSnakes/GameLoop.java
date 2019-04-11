package com.JavaSnakes;

import com.JavaSnakes.Snakes.PlayerSnake;
import com.JavaSnakes.Snakes.SnakeBase;
import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.MapCell;
import com.JavaSnakes.util.Status;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JPanel;

public class GameLoop extends JPanel implements Runnable, Commons {

    private boolean[][] mapWalls = new boolean[MAP_W][MAP_H];

    private List<SnakeBase> snakes;
    private List<SnakeBase> live_snakes;

    private GridPos food;

    private boolean inGame;
    private Thread animator;

    public GameLoop() {
        addKeyListener(new Input());
        setBackground(Color.darkGray);
        setFocusable(true);

        setPreferredSize(new Dimension(MAP_W * CELL_SIZE, MAP_H * CELL_SIZE));
        initGame(false);
    }

    private void initGame(boolean includeWalls) {
        for (int x = 0; x < MAP_W; x++) {
            for (int y = 0; y < Commons.MAP_H; y++) {
                if (includeWalls) {
                    mapWalls[x][y] = (x == 0 || x == MAP_W - 1 || y == 0 || y == MAP_H - 1);
                } else {
                    mapWalls[x][y] = false;
                }
            }
        }

        this.snakes = new ArrayList<>();
        snakes.add(new PlayerSnake(
            Direction.Right, new GridPos(3, 3), Color.blue,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
        ));
        snakes.add(new PlayerSnake(
            Direction.Left, new GridPos(MAP_W - 4, MAP_H - 4), Color.cyan,
            KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D
        ));
        this.live_snakes = new ArrayList<>(snakes);

        this.food = new GridPos(0, 0);
        createFood();

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
        for (SnakeBase snake : live_snakes)
            snake.processDirection();
        for (SnakeBase snake : live_snakes)
            snake.moveHead();

        checkCollisions();
        killCollidedSnakes();
        if (live_snakes.size() == 0) inGame = false;
        checkFood();

        for (SnakeBase snake : live_snakes)
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
        g.fillRect(food.x * CELL_SIZE + 1, food.y * CELL_SIZE + 1, 8, 8);
    }

    private void drawSnakes(Graphics g) {
        for (SnakeBase snake : live_snakes) {
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
        for (int x = 0; x < MAP_W; x++) {
            for (int y = 0; y < MAP_H; y++) {
                if (mapWalls[x][y]) {
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, 10, 10);
                }
            }
        }
    }

    private void drawGameOver(Graphics g) {
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (MAP_W * CELL_SIZE - metr.stringWidth(msg)) / 2, MAP_H * CELL_SIZE / 2);
    }

    private void checkCollisions() {
        for (int i = 0; i < live_snakes.size(); i++) {
            SnakeBase this_snake = live_snakes.get(i);
            if (wallCollided(this_snake) || this_snake.selfCollided() || snakeCollided(this_snake, i)) {
                this_snake.status = Status.Collided;
            }
        }
    }

    private boolean wallCollided(SnakeBase snake) {
        if (mapWalls[snake.coords.getFirst().x][snake.coords.getFirst().y]) return true;
        return false;
    }

    private boolean snakeCollided(SnakeBase this_snake, int snake_num) {
        for (int other_snake_num = 0; other_snake_num < live_snakes.size(); other_snake_num++) {
            SnakeBase other_snake = live_snakes.get(other_snake_num);
            if (snake_num == other_snake_num) continue;

            if (other_snake.coords.contains(this_snake.coords.getFirst())) {
                return true;
            }
        }
        return false;
    }

    private void killCollidedSnakes() {
        for(Iterator<SnakeBase> iter = live_snakes.iterator(); iter.hasNext(); ) {
            SnakeBase snake = iter.next();
            if (snake.status == Status.Collided){
                snake.status = Status.Dead;
                iter.remove();
            }
        }
    }

    private void checkFood() {
        for (SnakeBase snake : live_snakes) {
            if (snake.coords.getFirst().equals(food)) {
                snake.feed();
                createFood();
            }
        }
    }

    private void createFood() {
        boolean empty_space;
        do {
            empty_space = true;
            food.x = ThreadLocalRandom.current().nextInt(0, MAP_W);
            food.y = ThreadLocalRandom.current().nextInt(0, MAP_H);

            if (mapWalls[food.x][food.y]) {
                empty_space = false;
                continue;
            }
            for (SnakeBase snake : live_snakes) {
                if (snake.coords.contains(food)){
                    empty_space = false;
                    break;
                }
            }
        } while (!empty_space);
    }

    private class Input extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            for (SnakeBase snake : live_snakes) {
                if (!(snake instanceof PlayerSnake)) continue;
                PlayerSnake player_snake = (PlayerSnake) snake;

                if (player_snake.ctrlKeys.containsKey(key)) {
                    player_snake.directionBuffer = player_snake.ctrlKeys.get(key);
                }
            }
        }
    }
}
