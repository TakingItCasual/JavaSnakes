package com.JavaSnakes;

import com.JavaSnakes.Snakes.PlayerSnake;
import com.JavaSnakes.Snakes.SnakeBase;
import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.MapCell;
import com.JavaSnakes.util.Status;

import javax.swing.JPanel;
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

public class GameLoop extends JPanel implements Runnable {

    int CELL_SIZE = 10;
    int DELAY = 150;

    private MapData mapData;

    private List<SnakeBase> snakes;
    private List<SnakeBase> liveSnakes;

    private boolean inGame;
    private Thread animator;

    public GameLoop() {
        addKeyListener(new Input());
        setBackground(Color.darkGray);
        setFocusable(true);

        this.mapData = new MapData(15, 15);

        setPreferredSize(new Dimension(mapData.width * CELL_SIZE, mapData.height * CELL_SIZE));
        initGame(false);
    }

    private void initGame(boolean includeWalls) {
        if (includeWalls) {
            for (int x = 0; x < mapData.width; x++) {
                for (int y = 0; y < mapData.height; y++) {
                    if (x == 0 || x == mapData.width - 1 || y == 0 || y == mapData.height - 1) {
                        mapData.cells[x][y] = MapCell.Wall;
                    }
                }
            }
        }

        this.snakes = new ArrayList<>();
        snakes.add(new PlayerSnake(
            mapData, Direction.Right, new GridPos(3, 3), Color.blue,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
        ));
        snakes.add(new PlayerSnake(
            mapData, Direction.Left, new GridPos(mapData.width - 4, mapData.height - 4), Color.cyan,
            KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D
        ));
        this.liveSnakes = new ArrayList<>(snakes);

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
        for (SnakeBase snake : liveSnakes)
            snake.processDirection();
        for (SnakeBase snake : liveSnakes)
            snake.moveHead();

        checkCollisions();
        killCollidedSnakes();
        if (liveSnakes.size() == 0) inGame = false;
        checkFood();

        for (SnakeBase snake : liveSnakes)
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
        g.fillRect(mapData.food.x * CELL_SIZE + 1, mapData.food.y * CELL_SIZE + 1, 8, 8);
    }

    private void drawSnakes(Graphics g) {
        for (SnakeBase snake : liveSnakes) {
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
        for (int x = 0; x < mapData.width; x++) {
            for (int y = 0; y < mapData.height; y++) {
                if (mapData.cells[x][y] == MapCell.Wall) {
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

        String[] msgs = new String[snakes.size() + 1];
        msgs[0] = "Game Over";
        for (int i = 0; i < snakes.size(); i++) {
            msgs[i + 1] = "Snake " + snakes.get(i).id + ": " + snakes.get(i).score;
        }

        int yOffset = g.getFontMetrics().getHeight() * (snakes.size() / 2);
        for (int i = 0; i < msgs.length; i++) {
            g.drawString(
                msgs[i],
                (getWidth() - metr.stringWidth(msgs[i])) / 2,
                getHeight() / 2 - yOffset + i * g.getFontMetrics().getHeight()
            );
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < liveSnakes.size(); i++) {
            SnakeBase thisSnake = liveSnakes.get(i);
            if (wallCollided(thisSnake) || thisSnake.selfCollided() || snakeCollided(thisSnake, i)) {
                thisSnake.status = Status.Collided;
            }
        }
    }

    private boolean wallCollided(SnakeBase snake) {
        if (mapData.getCell(snake.coords.getFirst()) == MapCell.Wall) return true;
        return false;
    }

    private boolean snakeCollided(SnakeBase thisSnake, int snakeNum) {
        for (int otherSnakeNum = 0; otherSnakeNum < liveSnakes.size(); otherSnakeNum++) {
            SnakeBase otherSnake = liveSnakes.get(otherSnakeNum);
            if (snakeNum == otherSnakeNum) continue;

            if (otherSnake.coords.contains(thisSnake.coords.getFirst())) {
                return true;
            }
        }
        return false;
    }

    private void killCollidedSnakes() {
        for(Iterator<SnakeBase> iter = liveSnakes.iterator(); iter.hasNext(); ) {
            SnakeBase snake = iter.next();
            if (snake.status == Status.Collided){
                snake.status = Status.Dead;
                iter.remove();
            }
        }
    }

    private void checkFood() {
        for (SnakeBase snake : liveSnakes) {
            if (snake.coords.getFirst().equals(mapData.food)) {
                snake.feed();
                createFood();
            }
        }
    }

    private void createFood() {
        boolean emptySpace;
        do {
            emptySpace = true;
            mapData.food.x = ThreadLocalRandom.current().nextInt(0, mapData.width);
            mapData.food.y = ThreadLocalRandom.current().nextInt(0, mapData.height);

            if (mapData.getCell(mapData.food) == MapCell.Wall) {
                emptySpace = false;
                continue;
            }
            for (SnakeBase snake : liveSnakes) {
                if (snake.coords.contains(mapData.food)){
                    emptySpace = false;
                    break;
                }
            }
        } while (!emptySpace);
    }

    private class Input extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_ESCAPE)
                System.exit(0);

            for (SnakeBase snake : liveSnakes) {
                if (!(snake instanceof PlayerSnake)) continue;
                PlayerSnake playerSnake = (PlayerSnake) snake;

                if (playerSnake.ctrlKeys.containsKey(key)) {
                    playerSnake.directionBuffer = playerSnake.ctrlKeys.get(key);
                }
            }
        }
    }
}
