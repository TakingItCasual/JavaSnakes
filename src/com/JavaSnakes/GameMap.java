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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameMap extends JPanel implements ActionListener {

    private final int CELL_SIZE = 10;
    private final int DELAY = 140;

    private int mapW;
    private int mapH;
    private List<List<MapCell>> gameMap;
    private List<SnakeBase> snakes;

    private GridPos food;

    private boolean inGame = true;

    private Timer timer;

    public GameMap() {
        addKeyListener(new Input());
        setBackground(Color.darkGray);
        setFocusable(true);

        this.mapW = this.mapH = 15;
        setPreferredSize(new Dimension(mapW * CELL_SIZE, mapH * CELL_SIZE));
        initGame();
    }

    private void initGame() {
        this.gameMap = new ArrayList<>();
        for (int x = 0; x < mapW; x++) {
            gameMap.add(new ArrayList<>());
            for (int y = 0; y < mapH; y++) {
                if (x == 0 || x == mapW - 1 || y == 0 || y == mapH - 1) {
                    gameMap.get(x).add(MapCell.Wall);
                } else {
                    gameMap.get(x).add(MapCell.Empty);
                }
            }
        }

        this.snakes = new ArrayList<>();
        snakes.add(new PlayerSnake(
            Direction.Right, new GridPos(3, 3), Color.blue,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
        ));
        snakes.add(new PlayerSnake(
            Direction.Left, new GridPos(mapW - 4, mapH - 4), Color.cyan,
            KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D
        ));

        this.food = new GridPos(0, 0);
        createFood();

        this.timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        if (inGame) {
            // Draw food
            g.setColor(Color.red);
            g.fillRect(food.x * CELL_SIZE + 1, food.y * CELL_SIZE + 1, 8, 8);

            // Draw snakes
            for (SnakeBase snake : snakes) {
                if (snake.status == Status.Dead) continue;

                g.setColor(snake.color);
                g.fillRect(
                    snake.coords.getFirst().x * CELL_SIZE,
                    snake.coords.getFirst().y * CELL_SIZE,
                    10, 10
                );
                for (Iterator<GridPos> iter = snake.coords.listIterator(1); iter.hasNext(); ) {
                    GridPos coord = iter.next();
                    g.fillRect(coord.x * CELL_SIZE + 1, coord.y * CELL_SIZE + 1, 8, 8);
                }
            }

            // Draw walls
            g.setColor(Color.black);
            for (int x = 0; x < mapW; x++) {
                for (int y = 0; y < mapH; y++) {
                    if (gameMap.get(x).get(y) == MapCell.Wall) {
                        g.fillRect(x * CELL_SIZE, y * CELL_SIZE, 10, 10);
                    }
                }
            }

            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }        
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (mapW *CELL_SIZE - metr.stringWidth(msg)) / 2, mapH *CELL_SIZE / 2);
    }

    private void checkCollisions() {
        for (int i = 0; i < snakes.size(); i++) {
            SnakeBase this_snake = snakes.get(i);
            if (this_snake.status == Status.Dead) continue;

            // Collision with wall
            if (gameMap.get(this_snake.coords.getFirst().x).get(this_snake.coords.getFirst().y) == MapCell.Wall)
                this_snake.status = Status.Dying;
            // Collision with own tail
            if (Collections.frequency(this_snake.coords, this_snake.coords.getFirst()) > 1)
                this_snake.status = Status.Dying;

            for (int i2 = 0; i2 < snakes.size(); i2++) {
                SnakeBase other_snake = snakes.get(i2);
                if (i == i2 || other_snake.status == Status.Dead) continue;

                // Collision with other snake
                if (other_snake.coords.contains(this_snake.coords.getFirst())) {
                    this_snake.status = Status.Dying;
                }
            }
        }

        for (SnakeBase snake : snakes) {
            if (snake.status == Status.Dying) snake.status = Status.Dead;
        }

        if (areAllDead()) {
            inGame = false;
            timer.stop();
        }
    }

    private boolean areAllDead() {
        for (SnakeBase snake : snakes) if (snake.status != Status.Dead) return false;
        return true;
    }

    private void checkFood() {
        for (SnakeBase snake : snakes) {
            if (snake.status == Status.Dead) return;
            if (snake.coords.getFirst().x == food.x && snake.coords.getFirst().y == food.y) {
                snake.feed();
                createFood();
            }
        }
    }

    private void createFood() {
        do {
            food.x = ThreadLocalRandom.current().nextInt(0, mapW);
            food.y = ThreadLocalRandom.current().nextInt(0, mapH);
        } while (gameMap.get(food.x).get(food.y) != MapCell.Empty);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            for (SnakeBase snake : snakes)
                snake.processDirection();
            for (SnakeBase snake : snakes)
                snake.moveHead();

            checkCollisions();
            checkFood();

            for (SnakeBase snake : snakes)
                snake.removeTailEnd();
        }

        paintImmediately(getBounds());
    }

    private class Input extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            for (SnakeBase snake : snakes) {
                if (!(snake instanceof PlayerSnake)) continue;
                PlayerSnake player_snake = (PlayerSnake) snake;

                if (player_snake.ctrlKeys.containsKey(key)) {
                    player_snake.directionBuffer = player_snake.ctrlKeys.get(key);
                }
            }
        }
    }
}
