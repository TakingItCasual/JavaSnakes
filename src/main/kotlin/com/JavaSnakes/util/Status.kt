package com.javasnakes.util

enum class Status {
    Alive,
    Collided, // After colliding, before collision detection for the game loop is completed
    Dead
}
