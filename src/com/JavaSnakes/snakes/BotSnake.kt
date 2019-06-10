package com.JavaSnakes.snakes

import java.awt.Color

import com.JavaSnakes.util.Direction
import com.JavaSnakes.util.GridPos
import java.util.concurrent.atomic.AtomicInteger

class BotSnake(initDir: Direction, initPos: GridPos, setColor: Color) : SnakeBase(initDir, initPos, setColor) {
    companion object {
        private val NEXT_ID = AtomicInteger(1)
    }

    override val groupName = "Bot"
    override val idInGroup = NEXT_ID.getAndIncrement()

    override fun processDirection() {
        val dirPriority = Array(4) { Direction.Up }

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

        for (potentialDir in dirPriority) {
            if (direction.isOppositeOf(potentialDir)) continue
            if (nextTileObstructed(potentialDir)) continue
            direction = potentialDir
            break
        }
    }

    private fun nextTileObstructed(dir: Direction): Boolean {
        return board!!.tileObstructed(headPos().nextPos(dir))
    }
}
