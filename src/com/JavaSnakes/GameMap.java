package com.JavaSnakes;

import com.JavaSnakes.Snakes.PlayerSnake;
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

    private int map_w;
    private int map_h;
    private List<List<MapCell>> game_map;
    private List<PlayerSnake> player_snakes;

    private GridPos food;

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
            game_map.add(new ArrayList<>());
            for (int _y = 0; _y < map_h; _y++) {
                if (_x == 0 || _x == map_w-1 || _y == 0 || _y == map_h-1) {
                    game_map.get(_x).add(MapCell.Wall);
                } else {
                    game_map.get(_x).add(MapCell.Empty);
                }
            }
        }

        this.player_snakes = new ArrayList<>();
        player_snakes.add(new PlayerSnake(
            Direction.Right, new GridPos(5, 5), Color.blue,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
        ));
        player_snakes.add(new PlayerSnake(
            Direction.Left, new GridPos(20, 20), Color.cyan,
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

            // Draw player snakes
            for (PlayerSnake player_snake : player_snakes) {
                if (player_snake.status == Status.Dead) continue;

                g.setColor(player_snake.color);
                g.fillRect(
                    player_snake.coords.getFirst().x * CELL_SIZE,
                    player_snake.coords.getFirst().y * CELL_SIZE,
                    10, 10
                );
                for (Iterator<GridPos> iter = player_snake.coords.listIterator(1); iter.hasNext(); ) {
                    GridPos coord = iter.next();
                    g.fillRect(coord.x * CELL_SIZE + 1, coord.y * CELL_SIZE + 1, 8, 8);
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

    private void check_collisions() {
        for (int i = 0; i < player_snakes.size(); i++) {
            PlayerSnake player_snake = player_snakes.get(i);
            if (player_snake.status == Status.Dead) continue;

            // Collision with wall
            if (game_map.get(player_snake.coords.getFirst().x).get(player_snake.coords.getFirst().y) == MapCell.Wall)
                player_snake.status = Status.Dying;
            // Collision with own tail
            if (Collections.frequency(player_snake.coords, player_snake.coords.getFirst()) > 1)
                player_snake.status = Status.Dying;

            for (int i2 = 0; i2 < player_snakes.size(); i2++) {
                PlayerSnake other_snake = player_snakes.get(i2);
                if (i == i2 || other_snake.status == Status.Dead) continue;

                // Collision with other snake
                if (other_snake.coords.contains(player_snake.coords.getFirst())) {
                    player_snake.status = Status.Dying;
                }
            }
        }

        for (PlayerSnake player_snake : player_snakes) {
            if (player_snake.status == Status.Dying) player_snake.status = Status.Dead;
        }

        if (isAllDead()) {
            inGame = false;
            timer.stop();
        }
    }

    private boolean isAllDead() {
        for (PlayerSnake player_snake : player_snakes) if (player_snake.status != Status.Dead) return false;
        return true;
    }

    private void check_food() {
        for (PlayerSnake player_snake : player_snakes) {
            if (player_snake.status == Status.Dead) return;
            if (player_snake.coords.getFirst().x == food.x && player_snake.coords.getFirst().y == food.y) {
                player_snake.feed();
                createFood();
            }
        }
    }

    private void createFood() {
        do {
            food.x = ThreadLocalRandom.current().nextInt(0, map_w);
            food.y = ThreadLocalRandom.current().nextInt(0, map_h);
        } while (game_map.get(food.x).get(food.y) != MapCell.Empty);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            for (PlayerSnake player_snake : player_snakes)
                player_snake.process_input();
            for (PlayerSnake player_snake : player_snakes)
                player_snake.move_head();

            check_collisions();
            check_food();

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
        }
    }
}
