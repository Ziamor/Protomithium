package com.ziamor.platformer.engine;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.ziamor.platformer.Entities.GameEntity;

public interface Collidable {
    void onEntityCollision(GameEntity obj, Rectangle collider, CollisionHelper collisionHelper);

    void onWallCollision(Rectangle wall, Rectangle collider, TiledMapTileLayer.Cell cell, CollisionHelper collisionHelper);

    void onCollisionCheckBegin(); // Called when collision checking begins

    boolean collidesWithWalls(Rectangle collider);

    boolean collidesWithEntities(Rectangle collider);

    Rectangle[] getColliders();
}
