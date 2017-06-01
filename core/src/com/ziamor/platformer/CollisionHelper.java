package com.ziamor.platformer;

import com.badlogic.gdx.maps.tiled.TiledMap;
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

    public Array<Rectangle> getPossibleCollisions(Rectangle region, String layer) {
        possibleCollisions.clear();

        return possibleCollisions;
    }
}
