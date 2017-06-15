package com.ziamor.platformer.engine;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class GameLevel {
    private int level_width, level_height;
    private TiledMap tiledMap;

    public GameLevel(int level_width, int level_height, TiledMap tiledMap) {
        this.level_width = level_width;
        this.level_height = level_height;
        this.tiledMap = tiledMap;
    }

    public boolean[][] getBlockingMatrix() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("walls");

        if (layer != null) {
            boolean[][] blockers = new boolean[level_width][level_height];

            for (int y = 0; y < level_height; y++)
                for (int x = 0; x < level_width; x++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null)
                        blockers[x][y] = true;
                }
            return blockers;
        }
        return null;
    }

    public int getWidth() {
        return level_width;
    }

    public int getHeight() {
        return level_height;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
