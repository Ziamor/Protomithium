package com.ziamor.platformer.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.CollisionHelper;
import com.ziamor.platformer.Platformer;

/**
 * Created by ziamor on 6/5/2017.
 */
public class EnemyEntity {
    Vector2 pos, vel, target;
    float enemyWidth = 1, enemyHeight = 1, maxX = 0.1f;
    Rectangle enemyCollider;
    StateMachine<EnemyEntity, EnemyState> stateMachine;
    EnemyAnimation enemyAnimation;
    TextureRegion currentFrame;
    Array<Rectangle> possibleCollisions;
    private boolean dirFaceing = true; // false for left, true for right

    public EnemyEntity(Texture spriteSheet, Vector2 start_pos) {
        this.pos = start_pos;
        this.vel = new Vector2();
        this.target = new Vector2();
        this.enemyCollider = new Rectangle(pos.x, pos.y, enemyWidth, enemyHeight);
        this.stateMachine = new DefaultStateMachine<EnemyEntity, EnemyState>(this, EnemyState.IDLE);
        this.enemyAnimation = new EnemyAnimation(spriteSheet);
        this.possibleCollisions = new Array<Rectangle>();
    }

    public void update(CollisionHelper collisionHelper, float deltatime) {
        stateMachine.update();

        vel.x = vel.x * (1 - deltatime * 4) + target.x * (deltatime * 4);

        handleCollisions(collisionHelper);

        //Update the new position
        pos.x += vel.x;
        pos.y += vel.y;

        enemyCollider.set(pos.x, pos.y, enemyWidth, enemyHeight);
    }

    public void render(float deltatime, Batch batch) {
        currentFrame = enemyAnimation.getCurrentFrame(deltatime);
        float width = currentFrame.getRegionWidth() * Platformer.unitScale;
        float dir = -getDirFaceing();

        // If the player is facing to the left, scale the animation frame to be negative to flip it.
        // Also the position of the frame needs to be shifted to the right by the width of the frame
        batch.draw(currentFrame, dir < 0 ? pos.x + width : pos.x, pos.y, enemyAnimation.getScaleX() * dir, enemyAnimation.getScaleY());
    }

    public void handleCollisions(CollisionHelper collisionHelper) {
        float newX = pos.x + vel.x;
        float newY = pos.y + vel.y;

        enemyCollider.setX(newX);
        enemyCollider.setY(newY);

        collisionHelper.getPossibleCollisions(enemyCollider, possibleCollisions, "collision");

        for (Rectangle rect : possibleCollisions) {
            if (enemyCollider.overlaps(rect)) {
                Vector2 shallowVector = collisionHelper.getShallowAxisVector(enemyCollider, rect);
                Gdx.app.log("", shallowVector.toString() + "\t\t\t\t\t" + vel.toString());
                if (shallowVector.x != 0) {
                    vel.x = 0;
                    dirFaceing = !dirFaceing;
                    if (pos.x < rect.x)
                        pos.x = rect.x - enemyWidth;
                    else
                        pos.x = rect.x + rect.width;
                    enemyCollider.setX(pos.x);
                } else if (shallowVector.y != 0) {
                    vel.y = 0;
                    if (pos.y < rect.y)
                        pos.y = rect.y - enemyHeight;
                    else
                        pos.y = rect.y + rect.height;
                    enemyCollider.setY(pos.y);
                }
            }
        }
    }

    public float getDirFaceing() {
        if (dirFaceing)
            return -1;
        else
            return 1;
    }

    public boolean isMoving() {
        return true;
    }
}
