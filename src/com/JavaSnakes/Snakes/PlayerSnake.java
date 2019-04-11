package com.JavaSnakes.Snakes;

import java.awt.Color;
import java.util.HashMap;

import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;

public class PlayerSnake extends SnakeBase {

    public Direction directionBuffer;
    public HashMap<Integer, Direction> ctrlKeys;

    public PlayerSnake(
            Direction setDirection,
            GridPos initPos,
            Color setColor,
            int ctrlUp,
            int ctrlDown,
            int ctrlLeft,
            int ctrlRight
    ) {
        super(setDirection, initPos, setColor);

        this.directionBuffer = setDirection;

        this.ctrlKeys = new HashMap<>();
        ctrlKeys.put(ctrlUp, Direction.Up);
        ctrlKeys.put(ctrlDown, Direction.Down);
        ctrlKeys.put(ctrlLeft, Direction.Left);
        ctrlKeys.put(ctrlRight, Direction.Right);

        if (ctrlKeys.size() != 4) System.exit(1); // TODO: Properly handle duplicate control keys
    }

    public void processDirection() {
        if (direction == Direction.Up && directionBuffer != Direction.Down) {
            direction = directionBuffer;
        } else if (direction == Direction.Down && directionBuffer != Direction.Up) {
            direction = directionBuffer;
        } else if (direction == Direction.Left && directionBuffer != Direction.Right) {
            direction = directionBuffer;
        } else if (direction == Direction.Right && directionBuffer != Direction.Left) {
            direction = directionBuffer;
        }
        directionBuffer = direction;
    }
}