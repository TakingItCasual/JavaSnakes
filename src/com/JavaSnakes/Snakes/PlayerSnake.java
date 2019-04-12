package com.JavaSnakes.Snakes;

import java.awt.Color;
import java.util.HashMap;

import com.JavaSnakes.MapData;
import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;

public class PlayerSnake extends SnakeBase {

    public Direction directionBuffer;
    public HashMap<Integer, Direction> ctrlKeys;

    public PlayerSnake(
            MapData setMapData,
            Direction setDirection,
            GridPos initPos,
            Color setColor,
            int ctrlUp,
            int ctrlDown,
            int ctrlLeft,
            int ctrlRight
    ) {
        super(setMapData, setDirection, initPos, setColor);

        this.directionBuffer = setDirection;

        this.ctrlKeys = new HashMap<>();
        ctrlKeys.put(ctrlUp, Direction.Up);
        ctrlKeys.put(ctrlDown, Direction.Down);
        ctrlKeys.put(ctrlLeft, Direction.Left);
        ctrlKeys.put(ctrlRight, Direction.Right);

        if (ctrlKeys.size() != 4) System.exit(1); // TODO: Properly handle duplicate control keys
    }

    public void processDirection() {
        if ((direction == Direction.Up && directionBuffer != Direction.Down) ||
            (direction == Direction.Down && directionBuffer != Direction.Up) ||
            (direction == Direction.Left && directionBuffer != Direction.Right) ||
            (direction == Direction.Right && directionBuffer != Direction.Left))
        {
            direction = directionBuffer;
        }
        directionBuffer = direction;
    }
}
