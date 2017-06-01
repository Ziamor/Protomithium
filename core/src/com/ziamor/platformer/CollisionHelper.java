package com.ziamor.platformer;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by ziamor on 6/1/2017.
 */
public class CollisionHelper {
    TiledMap tiledMap;
    Array<Rectangle> possibleCollisions;

    public CollisionHelper(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        this.possibleCollisions = new Array<Rectangle>();
    }

    public Array<Rectangle> getPossibleCollisions(Rectangle region, String layer_name) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(layer_name);
        possibleCollisions.clear();
        for (int y = (int) region.y; y <= (int) region.y + region.height; y++) {
            for (int x = (int) region.x; x <= (int) region.x + region.width; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = new Rectangle(x, y, 1, 1);
                    possibleCollisions.add(rect);
                }
            }
        }
        return possibleCollisions;
    }
}
