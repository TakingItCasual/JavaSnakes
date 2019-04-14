package com.JavaSnakes.Snakes;

import java.awt.Color;

import com.JavaSnakes.util.Direction;
import com.JavaSnakes.util.GridPos;

public class BotSnake extends SnakeBase {

    public BotSnake(Direction setDirection, GridPos initPos, Color setColor) {
        super(setDirection, initPos, setColor);
    }

    public void processDirection() {
        Direction[] dirPriority = new Direction[4];

        int diffX = board.foodPos.x - coords.getFirst().x;
        int diffY = board.foodPos.y - coords.getFirst().y;
        if (!board.isWalled) {
            int altDiffX = diffX >= 0 ? diffX - board.width : diffX + board.width;
            int altDiffY = diffY >= 0 ? diffY - board.height : diffY + board.height;
            if (Math.abs(altDiffX) < Math.abs(diffX)) diffX = altDiffX;
            if (Math.abs(altDiffY) < Math.abs(diffY)) diffY = altDiffY;
        }

        if (Math.abs(diffX) < Math.abs(diffY)) {
            if (diffX != 0) {
                if (diffX > 0) {
                    dirPriority[0] = Direction.Right;
                    dirPriority[3] = Direction.Left;
                } else {
                    dirPriority[0] = Direction.Left;
                    dirPriority[3] = Direction.Right;
                }
                if (diffY > 0) {
                    dirPriority[1] = Direction.Down;
                    dirPriority[2] = Direction.Up;
                } else {
                    dirPriority[1] = Direction.Up;
                    dirPriority[2] = Direction.Down;
                }
            } else {
                if (diffY > 0) {
                    dirPriority[0] = Direction.Down;
                    dirPriority[1] = Direction.Left;
                    dirPriority[2] = Direction.Right;
                    dirPriority[3] = Direction.Up;
                } else {
                    dirPriority[0] = Direction.Up;
                    dirPriority[1] = Direction.Right;
                    dirPriority[2] = Direction.Left;
                    dirPriority[3] = Direction.Down;
                }
            }
        } else {
            if (diffY != 0) {
                if (diffY > 0) {
                    dirPriority[0] = Direction.Down;
                    dirPriority[3] = Direction.Up;
                } else {
                    dirPriority[0] = Direction.Up;
                    dirPriority[3] = Direction.Down;
                }
                if (diffX > 0) {
                    dirPriority[1] = Direction.Right;
                    dirPriority[2] = Direction.Left;
                } else {
                    dirPriority[1] = Direction.Left;
                    dirPriority[2] = Direction.Right;
                }
            } else {
                if (diffX > 0) {
                    dirPriority[0] = Direction.Right;
                    dirPriority[1] = Direction.Down;
                    dirPriority[2] = Direction.Up;
                    dirPriority[3] = Direction.Left;
                } else {
                    dirPriority[0] = Direction.Left;
                    dirPriority[1] = Direction.Up;
                    dirPriority[2] = Direction.Down;
                    dirPriority[3] = Direction.Right;
                }
            }
        }

        for (Direction dir : dirPriority) {
            if (directionsAreOpposite(direction, dir)) continue;
            if (nextTileObstructed(dir)) continue;
            direction = dir;
            break;
        }
    }

    private boolean nextTileObstructed(Direction dir) {
        GridPos headPos = coords.getFirst();
        if (dir == Direction.Up) {
            return board.tileObstructed(headPos.x, headPos.y - 1);
        } else if (dir == Direction.Down) {
            return board.tileObstructed(headPos.x, headPos.y + 1);
        } else if (dir == Direction.Left) {
            return board.tileObstructed(headPos.x - 1, headPos.y);
        } else { // Direction.Right
            return board.tileObstructed(headPos.x + 1, headPos.y);
        }
    }
}
