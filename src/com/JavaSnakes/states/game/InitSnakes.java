package com.JavaSnakes.states.game;

import com.JavaSnakes.snakes.BotSnake;
import com.JavaSnakes.snakes.PlayerSnake;
import com.JavaSnakes.snakes.SnakeBase;
import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class InitSnakes {

    private List<SnakeBase> snakes;
    private int mapWidth;
    private int mapHeight;

    public InitSnakes(int setMapWidth, int setMapHeight) {
        snakes = new ArrayList<>();
        mapWidth = setMapWidth;
        mapHeight = setMapHeight;
    }

    public void addPlayerSnake(Color snakeColor, int ctrlUp, int ctrlDown, int ctrlLeft, int ctrlRight) {
        snakes.add(new PlayerSnake(
            getSnakeInitDir(), getSnakeInitPos(), snakeColor,
            ctrlUp, ctrlDown, ctrlLeft, ctrlRight
        ));
    }

    public void addBotSnakes(Color snakeColor, int count) {
        for (int i = 0; i < count; i++) {
            snakes.add(new BotSnake(getSnakeInitDir(), getSnakeInitPos(), snakeColor));
        }
    }

    public List<SnakeBase> getSnakes() {
        return snakes;
    }

    private Direction getSnakeInitDir() {
        if (snakes.size() % 4 == 0) {
            return Direction.Right;
        } else if (snakes.size() % 4 == 1) {
            return Direction.Left;
        } else if (snakes.size() % 4 == 2) {
            return Direction.Down;
        } else {
            return Direction.Up;
        }
    }

    private GridPos getSnakeInitPos() {
        int snakeCount = snakes.size();
        int extra = snakeCount / 4;

        int initPosX = 3 + 3 * extra;
        int initPosY = 3 + 3 * extra;
        if (snakeCount % 4 == 1 || snakeCount % 4 == 2) {
            initPosX = mapWidth - 4 - 3 * extra;
        }
        if (snakeCount % 4 == 1 || snakeCount % 4 == 3) {
            initPosY = mapHeight - 4 - 3 * extra;
        }

        return new GridPos(initPosX, initPosY);
    }
}
