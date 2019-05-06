package com.JavaSnakes.util;

public class GridPos {

    public int x;
    public int y;

    public GridPos() {
        this(0, 0);
    }

    public GridPos(int setX, int setY) {
        x = setX;
        y = setY;
    }

    public GridPos(GridPos other) {
        this.x = other.x;
        this.y = other.y;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if(obj instanceof GridPos){
            GridPos other = (GridPos) obj;
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return x + y<<16; // Hashes are unique as long as x and y can be represented as shorts
    }
}
