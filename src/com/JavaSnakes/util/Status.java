package com.JavaSnakes.util;

public enum Status {
    Alive,
    Dying, // After colliding, but before collision detection for the game loop is completed
    Dead
}
