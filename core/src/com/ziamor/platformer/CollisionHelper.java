package com.ziamor.platformer;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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

    public Vector2 getShallowAxisVector(Rectangle collider, Rectangle blocker) {
        Vector2 vec = new Vector2();
        Rectangle intersect = new Rectangle();
        Intersector.intersectRectangles(collider, blocker, intersect);
        if (intersect != null) {
            if (intersect.width < intersect.height || intersect.height == 0)
                vec.x = intersect.width;
            else
                vec.y = intersect.height;
        }
        return vec;
    }

    public Array<Rectangle> getPossibleCollisions(Rectangle collider, String layer_name) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(layer_name);
        possibleCollisions.clear();
        int startX = (int) Math.max(0, collider.x - 1);
        int startY = (int) Math.max(0, collider.y);
        int endX = startX + 2;
        int endY = startY + 2;
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

    public Array<Rectangle> getPossibleGroundCollisions(Rectangle collider, String layer_name) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(layer_name);
        possibleCollisions.clear();
        int startX = (int) Math.max(0, collider.x - 1);
        int startY = (int) Math.max(0, collider.y);
        int endX = startX + 2;
        int endY = startY;
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
