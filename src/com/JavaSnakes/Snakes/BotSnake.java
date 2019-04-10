package com.JavaSnakes.Snakes;

import java.awt.Color;

import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;

public class BotSnake extends SnakeBase {

    public BotSnake(Direction setDirection, GridPos initPos, Color setColor) {
        super(setDirection, initPos, setColor);
    }

    public void processDirection() {}
}
