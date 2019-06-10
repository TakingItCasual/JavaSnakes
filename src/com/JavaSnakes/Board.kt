package com.JavaSnakes

import com.JavaSnakes.snakes.SnakeBase
import com.JavaSnakes.util.GridPos
import com.JavaSnakes.util.Status

import java.util.ArrayList
import java.util.concurrent.ThreadLocalRandom

class Board(val width: Int, val height: Int, val isWalled: Boolean, setSnakes: List<SnakeBase>) {
    var walls = Array(width) { BooleanArray(height) }

    var snakes: MutableList<SnakeBase> = ArrayList()
    var liveSnakes: MutableList<SnakeBase> = ArrayList()

    var foodPos = GridPos()

    init {
        SnakeBase.board = this

        if (isWalled) {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    walls[x][y] = x == 0 || x == width - 1 || y == 0 || y == height - 1
                }
            }
        }

        snakes.addAll(setSnakes)
        liveSnakes.addAll(setSnakes)

        createFood()
    }

    fun checkCollisions() {
        for (snake in liveSnakes) {
            if (wallCollided(snake) || snake.selfCollided() || snakeCollided(snake)) {
                snake.status = Status.Collided
            }
        }
    }

    private fun wallCollided(snake: SnakeBase): Boolean {
        return walls[snake.headPos().x][snake.headPos().y]
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

    fun checkFood() {
        for (snake in liveSnakes) {
            if (snake.headPos() == foodPos) {
                snake.feed()
                createFood()
            }
        }
    }

    // TODO: Implement a win mechanism instead of ending in an infinite loop
    fun createFood() {
        while (true) {
            foodPos.x = ThreadLocalRandom.current().nextInt(0, width)
            foodPos.y = ThreadLocalRandom.current().nextInt(0, height)
            if (!tileObstructed(foodPos)) break
        }
    }

    fun tileObstructed(coord: GridPos): Boolean {
        coord.normalize(width, height)
        return tileHasWall(coord) || tileHasSnake(coord)
    }

    private fun tileHasWall(coord: GridPos): Boolean {
        return walls[coord.x][coord.y]
    }

    private fun tileHasSnake(coord: GridPos): Boolean {
        for (snake in liveSnakes) {
            if (snake.coords.contains(coord)) return true
        }
        return false
    }
}
