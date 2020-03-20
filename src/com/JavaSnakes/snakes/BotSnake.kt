package com.JavaSnakes.snakes

import java.awt.Color

import com.JavaSnakes.util.Direction
import com.JavaSnakes.util.GridPos
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

class BotSnake(initDir: Direction, initPos: GridPos, setColor: Color) : SnakeBase(initDir, initPos, setColor) {
    companion object {
        private val NEXT_ID = AtomicInteger(1)
    }

    override val groupName = "Bot"
    override val idInGroup = NEXT_ID.getAndIncrement()

    override fun processDirection() {
        val dirByPriority = Array(4) { Direction.Up }

        val (diffX, diffY) = closestFoodDiff()
        if (abs(diffX) < abs(diffY)) {
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

        val potentialDirs = dirByPriority.filterNot { it === direction.opposite()} // Can't move backwards
        for (potentialDir in potentialDirs) {
            if (nextTileObstructed(potentialDir)) continue // Obstructed by wall or snake body
            if (nextTileClaimed(potentialDir)) continue // Higher priority snake moving to tile
            direction = potentialDir
            break
        }
    }

    private fun closestFoodDiff(): Pair<Int, Int> {
        var returnX = board!!.width * 2
        var returnY = board!!.height * 2
        for (foodPos in board!!.foodsPos) {
            val (diffX, diffY) = foodDiff(foodPos)
            if (abs(diffX) + abs(diffY) < abs(returnX) + abs(returnY)) {
                returnX = diffX
                returnY = diffY
            }
        }
        return Pair(returnX, returnY)
    }

    private fun foodDiff(foodPos: GridPos): Pair<Int, Int> {
        var diffX = foodPos.x - headPos().x
        var diffY = foodPos.y - headPos().y
        if (!board!!.isWalled) {
            val altDiffX = if (diffX >= 0) diffX - board!!.width else diffX + board!!.width
            val altDiffY = if (diffY >= 0) diffY - board!!.height else diffY + board!!.height
            if (abs(altDiffX) < abs(diffX)) diffX = altDiffX
            if (abs(altDiffY) < abs(diffY)) diffY = altDiffY
        }
        return Pair(diffX, diffY)
    }

    private fun nextTileObstructed(potentialDir: Direction): Boolean {
        return board!!.tileObstructed(headPos().nextPos(potentialDir))
    }

    // Snakes must be ordered by ID in board.liveSnakes, and have their directions processed in the same order
    private fun nextTileClaimed(potentialDir: Direction): Boolean {
        val thisNextPos = headPos().nextPos(potentialDir)
        for (snake in board!!.liveSnakes) {
            if (snake !is BotSnake) continue
            if (idInGroup <= snake.idInGroup) break

            val otherNextPos = snake.headPos().nextPos(snake.direction)
            if (thisNextPos == otherNextPos) return true
        }
        return false
    }
}
