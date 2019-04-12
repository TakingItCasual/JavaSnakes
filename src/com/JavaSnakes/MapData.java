package com.JavaSnakes;

import com.JavaSnakes.util.GridPos;
import com.JavaSnakes.util.MapCell;

import java.util.Arrays;

// TODO: Move snake data into here
public class MapData {

    public final int width;
    public final int height;

    public MapCell[][] cells;
    public GridPos food;

    public MapData(int setMapW, int setMapH) {
        this.width = setMapW;
        this.height = setMapH;

        this.cells = new MapCell[width][height];
        for (MapCell[] row: cells)
            Arrays.fill(row, MapCell.Empty);

        this.food = new GridPos(0, 0);
    }

    public MapCell getCell(GridPos gridPos) {
        return cells[gridPos.x][gridPos.y];
    }
}
