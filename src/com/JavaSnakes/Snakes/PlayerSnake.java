package com.JavaSnakes.Snakes;

import java.awt.Color;
import java.util.HashMap;

import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.Status;

public class PlayerSnake extends SnakeBase {

    public Direction direction_buffer;
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

        this.direction_buffer = setDirection;

        this.ctrlKeys = new HashMap<>();
        ctrlKeys.put(ctrlUp, Direction.Up);
        ctrlKeys.put(ctrlDown, Direction.Down);
        ctrlKeys.put(ctrlLeft, Direction.Left);
        ctrlKeys.put(ctrlRight, Direction.Right);

        if (ctrlKeys.size() != 4) System.exit(1); // TODO: Properly handle duplicate control keys
    }

    public void process_input() {
        if (status == Status.Dead) return;

        if (direction == Direction.Up && direction_buffer != Direction.Down) {
            direction = direction_buffer;
        } else if (direction == Direction.Down && direction_buffer != Direction.Up) {
            direction = direction_buffer;
        } else if (direction == Direction.Left && direction_buffer != Direction.Right) {
            direction = direction_buffer;
        } else if (direction == Direction.Right && direction_buffer != Direction.Left) {
            direction = direction_buffer;
        }
        direction_buffer = direction;
    }
}
