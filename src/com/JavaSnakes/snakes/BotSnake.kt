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
        val dirByPriority = Array(4) { Direction.Up }

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
                    dirByPriority[0] = Direction.Right
                    dirByPriority[3] = Direction.Left
                } else {
                    dirByPriority[0] = Direction.Left
                    dirByPriority[3] = Direction.Right
                }
                if (diffY > 0) {
                    dirByPriority[1] = Direction.Down
                    dirByPriority[2] = Direction.Up
                } else {
                    dirByPriority[1] = Direction.Up
                    dirByPriority[2] = Direction.Down
                }
            } else {
                if (diffY > 0) {
                    dirByPriority[0] = Direction.Down
                    dirByPriority[1] = Direction.Left
                    dirByPriority[2] = Direction.Right
                    dirByPriority[3] = Direction.Up
                } else {
                    dirByPriority[0] = Direction.Up
                    dirByPriority[1] = Direction.Right
                    dirByPriority[2] = Direction.Left
                    dirByPriority[3] = Direction.Down
                }
            }
        } else {
            if (diffY != 0) {
                if (diffY > 0) {
                    dirByPriority[0] = Direction.Down
                    dirByPriority[3] = Direction.Up
                } else {
                    dirByPriority[0] = Direction.Up
                    dirByPriority[3] = Direction.Down
                }
                if (diffX > 0) {
                    dirByPriority[1] = Direction.Right
                    dirByPriority[2] = Direction.Left
                } else {
                    dirByPriority[1] = Direction.Left
                    dirByPriority[2] = Direction.Right
                }
            } else {
                if (diffX > 0) {
                    dirByPriority[0] = Direction.Right
                    dirByPriority[1] = Direction.Down
                    dirByPriority[2] = Direction.Up
                    dirByPriority[3] = Direction.Left
                } else {
                    dirByPriority[0] = Direction.Left
                    dirByPriority[1] = Direction.Up
                    dirByPriority[2] = Direction.Down
                    dirByPriority[3] = Direction.Right
                }
            }
        }

        val potentialDirs = dirByPriority.filterNot { it === direction.opposite()}
        for (potentialDir in potentialDirs) {
            if (nextTileObstructed(potentialDir)) continue
            if (nextTileClaimed(potentialDir)) continue
            direction = potentialDir
            break
        }
    }

    private fun nextTileObstructed(potentialDir: Direction): Boolean {
        return board!!.tileObstructed(headPos().nextPos(potentialDir).normalized(board!!.width, board!!.height))
    }

    // Snakes must be ordered by ID in board.liveSnakes, and have their directions processed in the same order
    private fun nextTileClaimed(potentialDir: Direction): Boolean {
        val thisNextPos = headPos().nextPos(potentialDir).normalized(board!!.width, board!!.height)
        for (snake in board!!.liveSnakes) {
            if (snake !is BotSnake) continue
            if (idInGroup <= snake.idInGroup) break

            val otherNextPos = snake.headPos().nextPos(snake.direction).normalized(board!!.width, board!!.height)
            if (thisNextPos == otherNextPos) return true
        }
        return false
    }
}
