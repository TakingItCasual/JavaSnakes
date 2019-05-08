package com.JavaSnakes.util

class GridPos constructor(var x: Int = 0, var y: Int = 0) {
    constructor(other: GridPos): this(other.x, other.y)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GridPos) return false
        return this.x == other.x && this.y == other.y
    }

    override fun hashCode(): Int {
        return x + y shl 16 // Hashes are unique as long as x and y can be represented as shorts
    }
}
