package com.JavaSnakes.snakes

import java.awt.Color

import com.JavaSnakes.util.Direction
import com.JavaSnakes.util.GridPos

class BotSnake(initDir: Direction, initPos: GridPos, setColor: Color) : SnakeBase(initDir, initPos, setColor) {
    override fun processDirection() {
        val dirPriority: Array<Direction> = Array(4) { Direction.Up }

        var diffX = board!!.foodPos.x - headPos().x
        var diffY = board!!.foodPos.y - headPos().y
        if (!board!!.isWalled) {
            val altDiffX = if (diffX >= 0) diffX - board!!.width else diffX + board!!.width
            val altDiffY = if (diffY >= 0) diffY - board!!.height else diffY + board!!.height
            if (Math.abs(altDiffX) < Math.abs(diffX)) diffX = altDiffX
            if (Math.abs(altDiffY) < Math.abs(diffY)) diffY = altDiffY
        }

        if (Math.abs(diffX) < Math.abs(diffY)) {
            if (diffX != 0) {
                if (diffX > 0) {
                    dirPriority[0] = Direction.Right
                    dirPriority[3] = Direction.Left
                } else {
                    dirPriority[0] = Direction.Left
                    dirPriority[3] = Direction.Right
                }
                if (diffY > 0) {
                    dirPriority[1] = Direction.Down
                    dirPriority[2] = Direction.Up
                } else {
                    dirPriority[1] = Direction.Up
                    dirPriority[2] = Direction.Down
                }
            } else {
                if (diffY > 0) {
                    dirPriority[0] = Direction.Down
                    dirPriority[1] = Direction.Left
                    dirPriority[2] = Direction.Right
                    dirPriority[3] = Direction.Up
                } else {
                    dirPriority[0] = Direction.Up
                    dirPriority[1] = Direction.Right
                    dirPriority[2] = Direction.Left
                    dirPriority[3] = Direction.Down
                }
            }
        } else {
            if (diffY != 0) {
                if (diffY > 0) {
                    dirPriority[0] = Direction.Down
                    dirPriority[3] = Direction.Up
                } else {
                    dirPriority[0] = Direction.Up
                    dirPriority[3] = Direction.Down
                }
                if (diffX > 0) {
                    dirPriority[1] = Direction.Right
                    dirPriority[2] = Direction.Left
                } else {
                    dirPriority[1] = Direction.Left
                    dirPriority[2] = Direction.Right
                }
            } else {
                if (diffX > 0) {
                    dirPriority[0] = Direction.Right
                    dirPriority[1] = Direction.Down
                    dirPriority[2] = Direction.Up
                    dirPriority[3] = Direction.Left
                } else {
                    dirPriority[0] = Direction.Left
                    dirPriority[1] = Direction.Up
                    dirPriority[2] = Direction.Down
                    dirPriority[3] = Direction.Right
                }
            }
        }

        for (tempDir in dirPriority) {
            if (directionsAreOpposite(direction, tempDir)) continue
            if (nextTileObstructed(tempDir)) continue
            direction = tempDir
            break
        }
    }

    private fun nextTileObstructed(dir: Direction): Boolean {
        return when (dir) {
            Direction.Up -> board!!.tileObstructed(headPos().x, headPos().y - 1)
            Direction.Down -> board!!.tileObstructed(headPos().x, headPos().y + 1)
            Direction.Left -> board!!.tileObstructed(headPos().x - 1, headPos().y)
            Direction.Right -> board!!.tileObstructed(headPos().x + 1, headPos().y)
        }
    }
}
