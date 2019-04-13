package com.JavaSnakes.Snakes;

import com.JavaSnakes.MapData;
import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.Status;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SnakeBase {

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    public final int id;

    private static int initLength = 3;
    public static MapData mapData;

    public Status status;
    protected Direction direction;
    public LinkedList<GridPos> coords;
    protected int length;
    public int score;

    public Color color;

    SnakeBase(Direction setDirection, GridPos initPos, Color setColor) {
        this.id = NEXT_ID.getAndIncrement();

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

        if (newHead.x < 0) newHead.x = mapData.width - 1;
        if (newHead.x >= mapData.width) newHead.x = 0;
        if (newHead.y < 0) newHead.y = mapData.height - 1;
        if (newHead.y >= mapData.height) newHead.y = 0;

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
        return Collections.frequency(coords, coords.getFirst()) > 1;
    }

    public abstract void processDirection();
}
