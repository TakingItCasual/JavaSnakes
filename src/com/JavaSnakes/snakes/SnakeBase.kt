package com.JavaSnakes.snakes

import com.JavaSnakes.Board
import com.JavaSnakes.util.Direction
import com.JavaSnakes.util.GridPos
import com.JavaSnakes.util.Status

import java.awt.Color
import java.util.Collections
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicInteger

abstract class SnakeBase internal constructor(protected var direction: Direction, initPos: GridPos, var color: Color) {
    companion object {
        private val NEXT_ID = AtomicInteger(1)

        private const val initLength = 3
        var board: Board? = null
    }

    val id: Int

    var status: Status
    var coords: LinkedList<GridPos>
    private var length: Int = 0
    var score: Int = 0

    init {
        id = NEXT_ID.getAndIncrement()

        status = Status.Alive

        coords = LinkedList()
        for (i in 0 until initLength) {
            coords.addLast(GridPos(initPos))
            if (direction === Direction.Up) initPos.y += 1
            if (direction === Direction.Down) initPos.y -= 1
            if (direction === Direction.Left) initPos.x += 1
            if (direction === Direction.Right) initPos.x -= 1
        }

        length = initLength
        score = 0
    }

    fun moveHead() {
        val newHead = GridPos(headPos())

        if (direction === Direction.Up) newHead.y -= 1
        if (direction === Direction.Down) newHead.y += 1
        if (direction === Direction.Left) newHead.x -= 1
        if (direction === Direction.Right) newHead.x += 1

        if (newHead.x < 0) newHead.x = board!!.width - 1
        if (newHead.x >= board!!.width) newHead.x = 0
        if (newHead.y < 0) newHead.y = board!!.height - 1
        if (newHead.y >= board!!.height) newHead.y = 0

        coords.addFirst(newHead)
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

    protected fun directionsAreOpposite(dir1: Direction, dir2: Direction) = dir1 == oppositeDirection(dir2)

    private fun oppositeDirection(dir: Direction): Direction {
        return when (dir) {
            Direction.Up -> Direction.Down
            Direction.Down -> Direction.Up
            Direction.Left -> Direction.Right
            Direction.Right -> Direction.Left
        }
    }

    abstract fun processDirection()
}
