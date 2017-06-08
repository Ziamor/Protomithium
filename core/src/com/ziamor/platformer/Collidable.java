package com.ziamor.platformer;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.Entities.GameEntity;

/**
 * Created by ziamor on 6/7/2017.
 */
public interface Collidable {
    public void onEntityCollision(GameEntity obj, Rectangle collider, CollisionHelper collisionHelper);

    public void onWallCollision(Rectangle wall, Rectangle collider, CollisionHelper collisionHelper);

    public void onCollisionCheckBegin(); // Called when collision checking begins

    public boolean collidesWithWalls(Rectangle collider);

    public boolean collidesWithEntities(Rectangle collider);

    public Rectangle[] getColliders();
}
