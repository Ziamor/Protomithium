package com.ziamor.platformer.Entities.Enemies;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.Entities.*;
import com.ziamor.platformer.Entities.Player.PlayerState;
import com.ziamor.platformer.engine.Collidable;
import com.ziamor.platformer.engine.CollisionHelper;
import com.ziamor.platformer.Entities.Player.PlayerEntity;
import com.ziamor.platformer.GameScreen;

/**
 * Created by ziamor on 6/5/2017.
 */
public class EnemyEntity extends GameEntity implements Collidable, Damageable {
    private boolean dirFaceing = true; // false for left, true for right
    final int enemyValue = 500;
    float enemyWidth = 1, enemyHeight = 0.5f, maxX = 0.1f, deathTime, maxDeathTime = 3f;
    float currentHealth, maxHealth = 1;
    boolean dead;
    Vector2 target;
    Rectangle enemyCollider;
    Rectangle[] colliders;
    StateMachine<EnemyEntity, EnemyState> stateMachine;
    EnemyAnimation enemyAnimation;
    TextureRegion currentFrame;
    Array<Rectangle> possibleCollisions;

    public EnemyEntity(Texture spriteSheet, Vector2 start_pos) {
        super(start_pos);
        this.target = new Vector2();
        this.enemyCollider = new Rectangle(pos.x, pos.y, enemyWidth, enemyHeight);
        this.stateMachine = new DefaultStateMachine<EnemyEntity, EnemyState>(this, EnemyState.IDLE, EnemyState.GLOBAL_STATE);
        this.enemyAnimation = new EnemyAnimation(spriteSheet);
        this.possibleCollisions = new Array<Rectangle>();
        this.colliders = new Rectangle[]{enemyCollider};
    }

    @Override
    public void update(float deltatime) {
        stateMachine.update();

        if (dead) {
            deathTime += deltatime;
            if (deathTime >= maxDeathTime)
                this.dispose();
        } else
            vel.x = vel.x * (1 - deltatime * 4) + target.x * (deltatime * 4);

        //Update the new position
        pos.x += vel.x;
        pos.y += vel.y;

        updateColliders();
    }

    @Override
    public void render(float deltatime, Batch batch) {
        currentFrame = enemyAnimation.getCurrentFrame(deltatime);
        float width = currentFrame.getRegionWidth() * GameScreen.unitScale;
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

    @Override
    public float getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public float getMaxHealth() {
        return maxHealth;
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public void applyDamage(float dmg_value) {
        // Anything kills this enemy in one hit
        if (dmg_value > 0)
            this.dead = true;
    }

    @Override
    public void applyHeal(float heal_value) {
        // This enemy can't be healed
    }

    @Override
    public void debugRender(float deltatime, ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(enemyCollider.x, enemyCollider.y, enemyCollider.width, enemyCollider.height);
        shapeRenderer.end();
    }

    public boolean isMoving() {
        return true;
    }

    @Override
    public void onEntityCollision(GameEntity obj, Rectangle collider, CollisionHelper collisionHelper) {
        if(isDead())
            return;

        if (obj instanceof PlayerEntity) {
            Vector2 penetrationVec = collisionHelper.getPenetrationVector(collider, enemyCollider);
            PlayerEntity player = (PlayerEntity) obj;
            if (penetrationVec.y <= penetrationVec.x && player.isFalling()) {
                player.addScore(enemyValue);
                this.dead = true;
                this.vel.x = 0;
            } else
                player.applyDamage(25f);
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
