package com.JavaSnakes.Snakes;

import java.awt.Color;
import java.util.LinkedList;
import java.util.stream.IntStream;

import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.Status;

public abstract class SnakeBase {

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
        if (direction == Direction.Up) {
            IntStream.range(0, initLength).forEachOrdered(n -> {
                coords.addLast(new GridPos(initPos.x, initPos.y + n));
            });
        } else if (direction == Direction.Down) {
            IntStream.range(0, initLength).forEachOrdered(n -> {
                coords.addLast(new GridPos(initPos.x, initPos.y - n));
            });
        } else if (direction == Direction.Left) {
            IntStream.range(0, initLength).forEachOrdered(n -> {
                coords.addLast(new GridPos(initPos.x + n, initPos.y));
            });
        } else if (direction == Direction.Right) {
            IntStream.range(0, initLength).forEachOrdered(n -> {
                coords.addLast(new GridPos(initPos.x - n, initPos.y));
            });
        }

        this.color = setColor;

        this.length = initLength;
        this.score = 0;
    }

    public void move_head() {
        if (status == status.Dead) return;

        GridPos prev_head = coords.getFirst();

        if (direction == Direction.Up) {
            coords.addFirst(new GridPos(prev_head.x, prev_head.y - 1));
        } else if (direction == Direction.Down) {
            coords.addFirst(new GridPos(prev_head.x, prev_head.y + 1));
        } else if (direction == Direction.Left) {
            coords.addFirst(new GridPos(prev_head.x - 1, prev_head.y));
        } else if (direction == Direction.Right) {
            coords.addFirst(new GridPos(prev_head.x + 1, prev_head.y));
        }
    }

    public void remove_tail_end() {
        if (status == Status.Dead) return;

        while (coords.size() > length) {
            coords.removeLast();
        }
    }

    public void feed() {
        if (status == Status.Dead) return;

        length += 1;
        score += 1;
    }
}
