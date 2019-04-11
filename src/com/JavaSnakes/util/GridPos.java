package com.JavaSnakes.util;

public class GridPos {

    public int x;
    public int y;

    public GridPos(int setX, int setY) {
        this.x = setX;
        this.y = setY;
    }

    public GridPos(GridPos other) {
        this.x = other.x;
        this.y = other.y;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(o instanceof GridPos){
            GridPos other = (GridPos) o;
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return x + y<<16;
    }
}
