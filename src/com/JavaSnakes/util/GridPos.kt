package com.JavaSnakes.util

import java.lang.RuntimeException

class GridPos(var x: Int = 0, var y: Int = 0) {
    constructor(other: GridPos): this(other.x, other.y)

    companion object {
        var boardW: Int? = null
        var boardH: Int? = null
    }

    fun nextPos(dir: Direction): GridPos {
        return when (dir) {
            Direction.Up -> normalized(x, y - 1)
            Direction.Down -> normalized(x, y + 1)
            Direction.Left -> normalized(x - 1, y)
            Direction.Right -> normalized(x + 1, y)
        }
    }

    // Used by rendering to decide how to draw snake sections
    fun otherDir(other: GridPos): Direction {
        val xOffset = this.x - other.x
        val yOffset = this.y - other.y
        return when {
            xOffset == 1 || xOffset < -1 -> Direction.Left
            xOffset == -1 || xOffset > 1 -> Direction.Right
            yOffset == 1 || yOffset < -1 -> Direction.Up
            yOffset == -1 || yOffset > 1 -> Direction.Down
            else -> throw RuntimeException()
        }
    }

    private fun normalized(nextX: Int, nextY: Int): GridPos {
        val newX = Math.floorMod(nextX, boardW!!)
        val newY = Math.floorMod(nextY, boardH!!)
        return GridPos(newX, newY)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GridPos) return false
        return this.x == other.x && this.y == other.y
    }

    override fun hashCode(): Int {
        return x + y shl 16 // Hashes are unique as long as x and y can be represented as shorts
    }
}
