package com.JavaSnakes.snakes;

import java.awt.Color;
import java.util.HashMap;

import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;

public class PlayerSnake extends SnakeBase {

    private Direction directionBuffer;
    private HashMap<Integer, Direction> ctrlKeys;

    public PlayerSnake(
            Direction initDirection,
            GridPos initPos,
            Color setColor,
            int ctrlUp,
            int ctrlDown,
            int ctrlLeft,
            int ctrlRight
    ) {
        super(initDirection, initPos, setColor);

        directionBuffer = initDirection;

        ctrlKeys = new HashMap<>();
        ctrlKeys.put(ctrlUp, Direction.Up);
        ctrlKeys.put(ctrlDown, Direction.Down);
        ctrlKeys.put(ctrlLeft, Direction.Left);
        ctrlKeys.put(ctrlRight, Direction.Right);

        if (ctrlKeys.size() != 4) System.exit(1); // TODO: Properly handle duplicate control keys
    }

    @Override
    public void processDirection() {
        if (!directionsAreOpposite(direction, directionBuffer)) {
            direction = directionBuffer;
        }
        directionBuffer = direction;
    }

    public void bufferInput(int inputKey) {
        if (ctrlKeys.containsKey(inputKey)) {
            directionBuffer = ctrlKeys.get(inputKey);
        }
    }
}
