package com.ziamor.platformer.Entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ziamor.platformer.engine.CollisionHelper;


/**
 * Created by ziamor on 6/7/2017.
 */
public abstract class GameEntity {
    public enum Direction {
        LEFT, RIGHT
    }

    protected Vector2 pos, vel;
    private boolean dispose;
    //protected float width,height;

    public Vector2 getPos() {
        return pos;
    }

    public Vector2 getVel() {
        return vel;
    }

    public GameEntity(Vector2 start_pos) {
        this.pos = start_pos;
        this.vel = new Vector2();
        this.dispose = false;
    }

    public abstract void update(float deltatime);

    public abstract void render(float deltatime, Batch batch);

    public void debugRender(float deltatime, ShapeRenderer shapeRenderer) {
    }

    public void dispose() {
        this.dispose = true;
    }

    //TODO find a better place for this
    protected void pushOutOfCollision(Rectangle collider, Rectangle blocker, CollisionHelper collisionHelper) {
        if (collider.overlaps(blocker)) {
            Vector2 shallowVector = collisionHelper.getShallowAxisVector(collider, blocker);
            //Gdx.app.log("", collisionHelper.getPenetrationVector(collider, blocker).toString() + "\t\t" + vel.toString());
            if (shallowVector.x != 0) {
                //if (Math.abs(shallowVector.x) > 0.02)
                    vel.x = 0;
                if (pos.x < blocker.x)
                    pos.x = blocker.x - collider.width;
                else
                    pos.x = blocker.x + blocker.width;
                collider.setX(pos.x);
            } else if (shallowVector.y != 0) {
                //TODO entity gets stuck in walls sometimes, this check stops that, find a better solution
                if (Math.abs(shallowVector.y) > 0.01)
                    vel.y = 0;
                if (pos.y < blocker.y)
                    pos.y = blocker.y - collider.height;
                else
                    pos.y = blocker.y + blocker.height;
                collider.setY(pos.y);
            }
        }
    }

    public boolean isReadyToDispose() {
        return dispose;
    }

    public void setDispose(boolean dispose) {
        this.dispose = dispose;
    }
}
