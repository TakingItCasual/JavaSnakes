package com.JavaSnakes;

import com.JavaSnakes.Snakes.SnakeBase;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.Status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// TODO: Move snake data into here
public class MapData {

    public final int width;
    public final int height;

    public boolean[][] walls;

    public GridPos foodPos;

    public List<SnakeBase> snakes;
    public List<SnakeBase> liveSnakes;

    public MapData(int setMapW, int setMapH) {
        this.width = setMapW;
        this.height = setMapH;

        this.walls = new boolean[width][height];

        this.foodPos = new GridPos();

        this.snakes = new ArrayList<>();
        this.liveSnakes = new ArrayList<>();
    }

    public void checkCollisions() {
        for (int i = 0; i < liveSnakes.size(); i++) {
            SnakeBase thisSnake = liveSnakes.get(i);
            if (wallCollided(thisSnake) || thisSnake.selfCollided() || snakeCollided(thisSnake, i)) {
                thisSnake.status = Status.Collided;
            }
        }
    }

    private boolean wallCollided(SnakeBase snake) {
        return walls[snake.coords.getFirst().x][snake.coords.getFirst().y];
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
            if (snake.coords.getFirst().equals(foodPos)) {
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

    public boolean tileObstructed(GridPos coord) {
        return tileHasWall(coord) || tileHasSnake(coord);
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
