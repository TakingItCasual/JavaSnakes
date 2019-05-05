package com.JavaSnakes;

import com.JavaSnakes.snakes.SnakeBase;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.Status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Board {

    public final int width;
    public final int height;

    public final boolean isWalled;
    public boolean[][] walls;

    public List<SnakeBase> snakes;
    public List<SnakeBase> liveSnakes;

    public GridPos foodPos;

    public Board(int setMapW, int setMapH, boolean includeWalls) {
        this.width = setMapW;
        this.height = setMapH;

        this.isWalled = includeWalls;
        this.walls = new boolean[width][height];
        if (isWalled) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    walls[x][y] = (x == 0 || x == width - 1 || y == 0 || y == height - 1);
                }
            }
        }

        this.snakes = new ArrayList<>();
        this.liveSnakes = new ArrayList<>();

        this.foodPos = new GridPos();
    }

    public void checkCollisions() {
        for (SnakeBase snake : liveSnakes) {
            if (wallCollided(snake) || snake.selfCollided() || snakeCollided(snake)) {
                snake.status = Status.Collided;
            }
        }
    }

    private boolean wallCollided(SnakeBase snake) {
        return walls[snake.headPos().x][snake.headPos().y];
    }

    private boolean snakeCollided(SnakeBase snake) {
        for (SnakeBase otherSnake : liveSnakes) {
            if (snake == otherSnake) continue;
            if (otherSnake.coords.contains(snake.headPos())) {
                return true;
            }
        }
        return false;
    }

    public void killCollidedSnakes() {
        for (Iterator<SnakeBase> iter = liveSnakes.iterator(); iter.hasNext(); ) {
            SnakeBase snake = iter.next();
            if (snake.status == Status.Collided){
                snake.status = Status.Dead;
                iter.remove();
            }
        }
    }

    public void checkFood() {
        for (SnakeBase snake : liveSnakes) {
            if (snake.headPos().equals(foodPos)) {
                snake.feed();
                createFood();
            }
        }
    }

    public void createFood() {
        while (true) {
            foodPos.x = ThreadLocalRandom.current().nextInt(0, width);
            foodPos.y = ThreadLocalRandom.current().nextInt(0, height);
            if (!tileObstructed(foodPos)) break;
        }
    }

    public boolean tileObstructed(int x, int y) {
        return tileObstructed(new GridPos(x, y));
    }

    public boolean tileObstructed(GridPos coord) {
        normalizePos(coord);
        return tileHasWall(coord) || tileHasSnake(coord);
    }

    private void normalizePos(GridPos coord) {
        coord.x = Math.floorMod(coord.x, width);
        coord.y = Math.floorMod(coord.y, height);
    }

    private boolean tileHasWall(GridPos coord) {
        return walls[coord.x][coord.y];
    }

    private boolean tileHasSnake(GridPos coord) {
        for (SnakeBase snake : liveSnakes) {
            if (snake.coords.contains(coord)) return true;
        }
        return false;
    }
}
