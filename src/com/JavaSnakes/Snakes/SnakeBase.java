package com.JavaSnakes.Snakes;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;

import com.JavaSnakes.Commons;
import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.Status;

public abstract class SnakeBase implements Commons {

    private static int initLength = 3;

    public Color color;

    public Status status;
    protected Direction direction;
    public LinkedList<GridPos> coords;
    protected int length;
    protected int score;

    SnakeBase(Direction setDirection, GridPos initPos, Color setColor) {
        this.status = Status.Alive;
        this.direction = setDirection;

        this.coords = new LinkedList<>();
        for (int i = 0; i < initLength; i++) {
            coords.addLast(new GridPos(initPos.x, initPos.y));
            if (direction == Direction.Up) initPos.y += 1;
            if (direction == Direction.Down) initPos.y -= 1;
            if (direction == Direction.Left) initPos.x += 1;
            if (direction == Direction.Right) initPos.x -= 1;
        }

        this.color = setColor;

        this.length = initLength;
        this.score = 0;
    }

    public void moveHead() {
        GridPos newHead = new GridPos(coords.getFirst());

        if (direction == Direction.Up) newHead.y -= 1;
        if (direction == Direction.Down) newHead.y += 1;
        if (direction == Direction.Left) newHead.x -= 1;
        if (direction == Direction.Right) newHead.x += 1;

        if (newHead.x < 0) newHead.x = MAP_W - 1;
        if (newHead.x >= MAP_W) newHead.x = 0;
        if (newHead.y < 0) newHead.y = MAP_H - 1;
        if (newHead.y >= MAP_H) newHead.y = 0;

        coords.addFirst(newHead);
    }

    public void removeTailEnd() {
        while (coords.size() > length) {
            coords.removeLast();
        }
    }

    public void feed() {
        length += 1;
        score += 1;
    }

    public boolean selfCollided() {
        if (Collections.frequency(coords, coords.getFirst()) > 1) return true;
        return false;
    }

    public abstract void processDirection();
}
