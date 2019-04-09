package com.JavaSnakes;

import com.JavaSnakes.Snakes.PlayerSnake;
import com.JavaSnakes.Snakes.SnakeBase;
import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.MapCell;
import com.JavaSnakes.util.GridPos;

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
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameMap extends JPanel implements ActionListener {

    private final int CELL_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;

    private int map_w;
    private int map_h;
    private List<List<MapCell>> game_map;
    private List<PlayerSnake> player_snakes;

    private GridPos food;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;

    public GameMap() {
        addKeyListener(new Input());
        setBackground(Color.darkGray);
        setFocusable(true);

        this.map_w = this.map_h = 30;
        setPreferredSize(new Dimension(map_w*CELL_SIZE, map_h*CELL_SIZE));
        initGame();
    }

    private void initGame() {

        this.game_map = new ArrayList<>();
        for (int _x = 0; _x < map_w; _x++) {
            this.game_map.add(new ArrayList<>());
            for (int _y = 0; _y < map_h; _y++) {
                if (_x == 0 || _x == map_w-1 || _y == 0 || _y == map_h-1) {
                    this.game_map.get(_x).add(MapCell.Wall);
                } else {
                    this.game_map.get(_x).add(MapCell.Empty);
                }
            }
        }

        this.player_snakes = new ArrayList<>();
        this.player_snakes.add(new PlayerSnake(
            Direction.Right,
            new GridPos(7, 7),
            Color.blue,
            KeyEvent.VK_UP,
            KeyEvent.VK_DOWN,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_RIGHT
        ));

        dots = 3;

        for (int i = 0; i < dots; i++) {
            x[i] = 5 - i;
            y[i] = 5;
        }

        this.food = new GridPos(0, 0);
        createFood();

        timer = new Timer(DELAY, this);
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

            // Draw player snakes
            for (PlayerSnake player_snake : player_snakes) {
                if (!player_snake.alive) continue;

                g.setColor(player_snake.color);
                g.fillRect(
                    player_snake.coords.getFirst().x * CELL_SIZE,
                    player_snake.coords.getFirst().y * CELL_SIZE,
                    10, 10
                );
                for (Iterator<GridPos> i = player_snake.coords.listIterator(1); i.hasNext(); ) {
                    GridPos coord = i.next();
                    g.fillRect(coord.x * CELL_SIZE + 1, coord.y * CELL_SIZE + 1, 8, 8);
                }
            }

            for (int i = 0; i < dots; i++) {
                if (i == 0) {
                    g.fillRect(x[i]*CELL_SIZE, y[i]*CELL_SIZE, 10, 10);
                } else {
                    g.fillRect(x[i]*CELL_SIZE+1, y[i]*CELL_SIZE+1, 8, 8);
                }
            }

            // Draw walls
            g.setColor(Color.black);
            for (int _x = 0; _x < map_w; _x++) {
                for (int _y = 0; _y < map_h; _y++) {
                    if (game_map.get(_x).get(_y) == MapCell.Wall) {
                        g.fillRect(_x * CELL_SIZE, _y * CELL_SIZE, 10, 10);
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
        g.drawString(msg, (map_w*CELL_SIZE - metr.stringWidth(msg)) / 2, map_h*CELL_SIZE / 2);
    }

    private void checkApple() {
        if ((x[0] == food.x) && (y[0] == food.y)) {
            dots++;
            createFood();
        }
    }

    private void check_collisions(SnakeBase snake_character) {
        if (!snake_character.alive) return;
    }

    private void check_food(SnakeBase snake_character) {
        if (!snake_character.alive) return;

        if (snake_character.coords.getFirst().x == food.x && snake_character.coords.getFirst().y == food.y) {
            snake_character.feed();
            createFood();
        }
    }

    private void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[(i - 1)];
            y[i] = y[(i - 1)];
        }

        if (leftDirection) {
            x[0] -= 1;
        }

        if (rightDirection) {
            x[0] += 1;
        }

        if (upDirection) {
            y[0] -= 1;
        }

        if (downDirection) {
            y[0] += 1;
        }
    }

    private void checkCollision() {
        for (int i = dots; i > 0; i--) {

            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false;
            }
        }

        if (x[0] < 0 || x[0] >= map_w) {
            inGame = false;
        }

        if (y[0] < 0 || y[0] >= map_h) {
            inGame = false;
        }
        
        if (!inGame) {
            timer.stop();
        }
    }

    private void createFood() {
        do {
            this.food.x = (int) (Math.random() * RAND_POS);
            this.food.y = (int) (Math.random() * RAND_POS);
        } while (game_map.get(food.x).get(food.y) != MapCell.Empty);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();

            for (PlayerSnake player_snake : player_snakes)
                player_snake.process_input();
            for (PlayerSnake player_snake : player_snakes)
                player_snake.move_head();
            for (PlayerSnake player_snake : player_snakes)
                check_collisions(player_snake);
            for (PlayerSnake player_snake : player_snakes)
                check_food(player_snake);
            for (PlayerSnake player_snake : player_snakes)
                player_snake.remove_tail_end();
        }

        paintImmediately(getBounds());
    }

    private class Input extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            for (PlayerSnake player_snake : player_snakes) {
                if (player_snake.ctrlKeys.containsKey(key)) {
                    player_snake.direction_buffer = player_snake.ctrlKeys.get(key);
                }
            }

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
