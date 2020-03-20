package com.JavaSnakes

import com.JavaSnakes.snakes.SnakeBase
import com.JavaSnakes.util.GridPos
import com.JavaSnakes.util.Status

import java.util.ArrayList
import java.util.concurrent.ThreadLocalRandom

class Board(
        val width: Int,
        val height: Int,
        val isWalled: Boolean,
        foodCount: Int,
        setSnakes: List<SnakeBase>
) {
    var snakes: MutableList<SnakeBase> = ArrayList()
    var liveSnakes: MutableList<SnakeBase> = ArrayList()

    var foodsPos: MutableList<GridPos> = ArrayList()

    init {
        SnakeBase.board = this

        snakes.addAll(setSnakes)
        liveSnakes.addAll(setSnakes)

        val freeArea = freeMapArea()
        repeat(foodCount) {
            addFood(freeArea)
        }
    }

    fun checkCollisions() {
        for (snake in liveSnakes) {
            if (tileHasWall(snake.headPos()) || snake.selfCollided() || snakeCollided(snake)) {
                snake.status = Status.Collided
            }
        }
    }

    private fun snakeCollided(snake: SnakeBase): Boolean {
        for (otherSnake in liveSnakes) {
            if (snake === otherSnake) continue
            if (otherSnake.coords.contains(snake.headPos())) {
                return true
            }
        }
        return false
    }

    fun killCollidedSnakes() {
        val iter = liveSnakes.iterator()
        while (iter.hasNext()) {
            val snake = iter.next()
            if (snake.status === Status.Collided) {
                snake.status = Status.Dead
                iter.remove()
            }
        }
    }

    // TODO: Test with almost-full-map saved game
    fun checkFood(): Boolean {
        for (snake in liveSnakes) {
            if (snake.headPos() in foodsPos) {
                foodsPos.remove(snake.headPos())
                snake.feed()

                val freeArea = freeMapArea()
                if (freeArea <= 0) return false // Map full, game over
                addFood(freeArea)
            }
        }
        return true
    }

    private fun addFood(freeArea: Int) {
        if (freeArea - foodsPos.size <= 0) return

        val newPos = GridPos()
        while (true) {
            newPos.x = ThreadLocalRandom.current().nextInt(0, width)
            newPos.y = ThreadLocalRandom.current().nextInt(0, height)
            if (!tileObstructed(newPos) && !foodsPos.contains(newPos)) break
        }
        foodsPos.add(newPos)
    }

    // Returns number of tiles not taken by snakes or walls
    private fun freeMapArea(): Int {
        var snakeLengthsTotal = 0
        for (snake in liveSnakes) {
            snakeLengthsTotal += snake.coords.size + 1 // Include space behind snake
        }
        val totalArea = if (!isWalled) (width * height) else ((width-2) * (height-2))
        return totalArea - snakeLengthsTotal
    }

    fun tileObstructed(coord: GridPos): Boolean {
        return tileHasWall(coord) || tileHasSnake(coord)
    }

    private fun tileHasWall(coord: GridPos): Boolean {
        return isWalled && (coord.x == 0 || coord.x == width-1 || coord.y == 0 || coord.y == height-1)
    }

    private fun tileHasSnake(coord: GridPos): Boolean {
        for (snake in liveSnakes) {
            if (snake.coords.contains(coord)) return true
        }
        return false
    }
}
