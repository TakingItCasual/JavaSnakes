package com.JavaSnakes.util

class GridPos constructor(var x: Int = 0, var y: Int = 0) {
    constructor(other: GridPos): this(other.x, other.y)

    // TODO: Combined nextPos and normalized method
    fun nextPos(dir: Direction): GridPos {
        return when (dir) {
            Direction.Up -> GridPos(x, y - 1)
            Direction.Down -> GridPos(x, y + 1)
            Direction.Left -> GridPos(x - 1, y)
            Direction.Right -> GridPos(x + 1, y)
        }
    }

    fun normalized(boardWidth: Int, boardHeight: Int): GridPos {
        val newX = Math.floorMod(x, boardWidth)
        val newY = Math.floorMod(y, boardHeight)
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
