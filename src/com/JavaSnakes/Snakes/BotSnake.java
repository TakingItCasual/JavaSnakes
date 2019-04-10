package com.JavaSnakes.Snakes;

import java.awt.Color;
import java.util.HashMap;

import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.Status;

public class BotSnake extends SnakeBase {

    public BotSnake(Direction setDirection, GridPos initPos, Color setColor) {
        super(setDirection, initPos, setColor);
    }

    public void processDirection() {
        if (status == Status.Dead) return;
    }
}
