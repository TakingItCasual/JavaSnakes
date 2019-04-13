package com.JavaSnakes.Snakes;

import java.awt.Color;

import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;

public class BotSnake extends SnakeBase {

    public BotSnake(Direction setDirection, GridPos initPos, Color setColor) {
        super(setDirection, initPos, setColor);
    }

    public void processDirection() {
        int diffX = mapData.foodPos.x - coords.getFirst().x;
        int diffY = mapData.foodPos.y - coords.getFirst().y;

        if (Math.abs(diffX) < Math.abs(diffY)) {
            if (diffX > 0) {
                if (direction != Direction.Left) {
                    direction = Direction.Right;
                } else if (diffY > 0) {
                    direction = Direction.Down;
                } else if (diffY < 0) {
                    direction = Direction.Up;
                }
            } else if (diffX < 0) {
                if (direction != Direction.Right) {
                    direction = Direction.Left;
                } else if (diffY > 0) {
                    direction = Direction.Down;
                } else if (diffY < 0) {
                    direction = Direction.Up;
                }
            } else {
                if (diffY > 0) {
                    if (direction != Direction.Up) {
                        direction = Direction.Down;
                    } else {
                        direction = Direction.Left;
                    }
                } else if (diffY < 0) {
                    if (direction != Direction.Down) {
                        direction = Direction.Up;
                    } else {
                        direction = Direction.Right;
                    }
                }
            }
        } else if (Math.abs(diffY) < Math.abs(diffX)) {
            if (diffY > 0) {
                if (direction != Direction.Up) {
                    direction = Direction.Down;
                } else if (diffX > 0) {
                    direction = Direction.Right;
                } else if (diffX < 0) {
                    direction = Direction.Left;
                }
            } else if (diffY < 0) {
                if (direction != Direction.Down) {
                    direction = Direction.Up;
                } else if (diffX > 0) {
                    direction = Direction.Right;
                } else if (diffX < 0) {
                    direction = Direction.Left;
                }
            } else {
                if (diffX > 0) {
                    if (direction != Direction.Left) {
                        direction = Direction.Right;
                    } else {
                        direction = Direction.Down;
                    }
                } else if (diffX < 0) {
                    if (direction != Direction.Right) {
                        direction = Direction.Left;
                    } else {
                        direction = Direction.Up;
                    }
                }
            }
        }
    }
}
