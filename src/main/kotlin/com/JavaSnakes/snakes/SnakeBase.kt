package com.javasnakes.snakes

import com.javasnakes.Board
import com.javasnakes.util.Direction
import com.javasnakes.util.GridPos
import com.javasnakes.util.Status

import java.awt.Color
import java.util.Collections
import java.util.LinkedList

abstract class SnakeBase protected constructor(
        protected var direction: Direction,
        var initPos: GridPos,
        var color: Color
) {
    companion object {
        private const val initLength = 3
        var board: Board? = null
    }

    abstract val groupName: String // TODO: Abstract static properties?
    abstract val idInGroup: Int

    var status = Status.Alive
    val coords: LinkedList<GridPos> = LinkedList()
    private var length = initLength
    var score = 0
        private set

    init {
        for (i in 0 until initLength) {
            coords.addLast(GridPos(initPos))
            initPos = initPos.nextPos(direction.opposite())
        }
    }

    fun moveHead() {
        coords.addFirst(headPos().nextPos(direction))
    }

    fun removeTailEnd() {
        while (coords.size > length) {
            coords.removeLast()
        }
    }

    fun feed() {
        length += 1
        score += 1
    }

    fun selfCollided(): Boolean {
        return Collections.frequency(coords, coords.first) > 1
    }

    fun headPos(): GridPos {
        return coords.first
    }

    abstract fun processDirection()
}
