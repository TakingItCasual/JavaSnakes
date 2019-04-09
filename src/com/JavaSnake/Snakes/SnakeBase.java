package com.JavaSnake.Snakes;

import java.awt.Color;
import java.util.LinkedList;
import java.util.stream.IntStream;

import com.JavaSnake.util.Direction;
import com.JavaSnake.util.GridPos;

public abstract class SnakeBase {

    private static int initLength = 3;

    public Color color;

    public boolean alive;
    protected Direction direction;
    public Direction direction_buffer;
    public LinkedList<GridPos> coords;
    protected int length;
    protected int score;

    SnakeBase(Direction setDirection, GridPos initPos, Color setColor) {
        this.alive = true;
        this.direction = this.direction_buffer = setDirection;

        this.coords = new LinkedList<>();
        if (direction == Direction.Up) {
            IntStream.range(0, initLength).forEachOrdered(n -> {
                this.coords.addLast(new GridPos(initPos.x, initPos.y + n));
            });
        } else if (direction == Direction.Down) {
            IntStream.range(0, initLength).forEachOrdered(n -> {
                this.coords.addLast(new GridPos(initPos.x, initPos.y - n));
            });
        } else if (direction == Direction.Left) {
            IntStream.range(0, initLength).forEachOrdered(n -> {
                this.coords.addLast(new GridPos(initPos.x + n, initPos.y));
            });
        } else if (direction == Direction.Right) {
            IntStream.range(0, initLength).forEachOrdered(n -> {
                this.coords.addLast(new GridPos(initPos.x - n, initPos.y));
            });
        }

        this.color = setColor;

        this.length = initLength;
        this.score = 0;
    }

    public void move_head() {
        if (!alive) return;

        GridPos prev_head = coords.getFirst();

        if (direction == Direction.Up) {
            this.coords.addFirst(new GridPos(prev_head.x, prev_head.y - 1));
        } else if (direction == Direction.Down) {
            this.coords.addFirst(new GridPos(prev_head.x, prev_head.y + 1));
        } else if (direction == Direction.Left) {
            this.coords.addFirst(new GridPos(prev_head.x - 1, prev_head.y));
        } else if (direction == Direction.Right) {
            this.coords.addFirst(new GridPos(prev_head.x + 1, prev_head.y));
        }
    }

    public void remove_tail_end() {
        if (!alive) return;

        while (coords.size() > length) {
            this.coords.removeLast();
        }
    }

    public void feed() {
        if (!alive) return;

        this.length += 1;
        this.score += 1;
    }
}
