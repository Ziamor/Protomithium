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
        int startX = (int) region.x;
        int startY = (int) region.y;
        int endX = startX + (int) (Math.ceil(region.width));
        int endY = startY + (int) (Math.ceil(region.height));
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
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
