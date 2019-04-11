package com.JavaSnakes.util;

// TODO: Move snake data into here
public class MapData {

    public int width;
    public int height;

    public boolean[][] walls;
    public GridPos food;

    public MapData(int setMapW, int setMapH) {
        this.width = setMapW;
        this.height = setMapH;

        this.walls = new boolean[width][height];
        this.food = new GridPos(0, 0);
    }
}
