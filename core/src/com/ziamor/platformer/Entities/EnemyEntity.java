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
import com.ziamor.platformer.Collidable;
import com.ziamor.platformer.CollisionHelper;
import com.ziamor.platformer.Entities.Player.PlayerEntity;
import com.ziamor.platformer.Platformer;

/**
 * Created by ziamor on 6/5/2017.
 */
public class EnemyEntity extends GameEntity implements Collidable {
    Vector2 target;
    float enemyWidth = 1, enemyHeight = 1, maxX = 0.1f;
    Rectangle enemyCollider;
    Rectangle[] colliders;
    StateMachine<EnemyEntity, EnemyState> stateMachine;
    EnemyAnimation enemyAnimation;
    TextureRegion currentFrame;
    Array<Rectangle> possibleCollisions;
    private boolean dirFaceing = true; // false for left, true for right

    public EnemyEntity(Texture spriteSheet, Vector2 start_pos) {
        super(start_pos);
        this.target = new Vector2();
        this.enemyCollider = new Rectangle(pos.x, pos.y, enemyWidth, enemyHeight);
        this.stateMachine = new DefaultStateMachine<EnemyEntity, EnemyState>(this, EnemyState.IDLE);
        this.enemyAnimation = new EnemyAnimation(spriteSheet);
        this.possibleCollisions = new Array<Rectangle>();
        this.colliders = new Rectangle[]{enemyCollider};
    }

    @Override
    public void update(float deltatime) {
        stateMachine.update();

        vel.x = vel.x * (1 - deltatime * 4) + target.x * (deltatime * 4);

        //Update the new position
        pos.x += vel.x;
        pos.y += vel.y;

        updateColliders();
    }

    @Override
    public void render(float deltatime, Batch batch) {
        currentFrame = enemyAnimation.getCurrentFrame(deltatime);
        float width = currentFrame.getRegionWidth() * Platformer.unitScale;
        float dir = -getDirFaceing();

        // If the player is facing to the left, scale the animation frame to be negative to flip it.
        // Also the position of the frame needs to be shifted to the right by the width of the frame
        batch.draw(currentFrame, dir < 0 ? pos.x + width : pos.x, pos.y, enemyAnimation.getScaleX() * dir, enemyAnimation.getScaleY());
    }

    public void updateColliders() {
        enemyCollider.set(pos.x, pos.y, enemyWidth, enemyHeight);
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

    @Override
    public void onEntityCollision(GameEntity obj, Rectangle collider, CollisionHelper collisionHelper) {
        if (obj instanceof PlayerEntity) {
            Vector2 penetrationVec = collisionHelper.getPenetrationVector(collider, enemyCollider);
            if (penetrationVec.y <= penetrationVec.x && ((PlayerEntity) obj).isFalling())
                this.dispose = true;
        }
    }

    @Override
    public void onWallCollision(Rectangle wall, Rectangle collider, CollisionHelper collisionHelper) {
        this.pushOutOfCollision(collider, wall, collisionHelper);
        updateColliders();
        dirFaceing = !dirFaceing;
    }

    @Override
    public void onCollisionCheckBegin() {

    }

    @Override
    public boolean collidesWithWalls(Rectangle collider) {
        return true;
    }

    @Override
    public boolean collidesWithEntities(Rectangle collider) {
        return true;
    }

    @Override
    public Rectangle[] getColliders() {
        return colliders;
    }
}
