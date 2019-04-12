package com.JavaSnakes.Snakes;

import java.awt.Color;

import com.JavaSnakes.MapData;
import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;

public class BotSnake extends SnakeBase {

    public BotSnake(MapData setMapData, Direction setDirection, GridPos initPos, Color setColor) {
        super(setMapData, setDirection, initPos, setColor);
    }

    public void processDirection() {}
}
