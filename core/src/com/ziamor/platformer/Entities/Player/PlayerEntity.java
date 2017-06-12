package com.ziamor.platformer.Entities.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ziamor.platformer.Entities.Damageable;
import com.ziamor.platformer.GameScreen;
import com.ziamor.platformer.engine.Collidable;
import com.ziamor.platformer.engine.CollisionHelper;
import com.ziamor.platformer.Entities.GameEntity;

public class PlayerEntity extends GameEntity implements Collidable, Damageable {
    final float maxX = GameScreen.unitScale * 20f, colBumpOut = GameScreen.unitScale * 2f;
    final float player_width = GameScreen.unitScale * 100, player_height = GameScreen.unitScale * 160;
    final float groundColliderWidth = player_width, groundColliderHeight = GameScreen.unitScale * 2f;

    private float jumpForce = GameScreen.unitScale * 20f;
    private float gravity = GameScreen.unitScale * -18f;

    private float currentHealth, maxHealth = 100, timeSinceLastDamage, damageImmunityTime = 1.5f;
    private boolean dead, damageImmune;

    PlayerAnimation playerAnimation;
    StateMachine<PlayerEntity, PlayerState> playerStateMachine;

    private Rectangle playerCollider, groundCollider, collRegion;
    private Rectangle[] colliders;

    private boolean moveLeft, moveRight, jump, crouch, touchingGround;
    private float lastDirFacing;

    private TextureRegion currentFrame;

    private int score;

    public PlayerEntity(Texture spriteSheet, Vector2 start_pos) {
        super(start_pos);
        this.playerCollider = new Rectangle(pos.x, pos.y, player_width, player_height);
        this.groundCollider = new Rectangle(pos.x, pos.y - groundColliderHeight / 2, groundColliderWidth, groundColliderHeight);
        this.colliders = new Rectangle[]{playerCollider, groundCollider};
        this.playerAnimation = new PlayerAnimation(spriteSheet);
        this.playerStateMachine = new DefaultStateMachine<PlayerEntity, PlayerState>(this, PlayerState.IDLE, PlayerState.GLOBAL_STATE);
        this.lastDirFacing = 1;
        this.collRegion = new Rectangle();
        this.touchingGround = true;
        this.currentHealth = maxHealth;
    }

    @Override
    public void update(float deltatime) {
        playerStateMachine.update();

        if (damageImmune) {
            timeSinceLastDamage += deltatime;
            if (timeSinceLastDamage >= damageImmunityTime) {
                damageImmune = false;
                timeSinceLastDamage = 0;
            }
        }

        // Handle movement allong the x-axis
        if (isMoving() && !isCrouching()) {
            // Get the x target value
            float xTarget = getDirFaceing() * maxX;
            vel.x = vel.x * (1 - deltatime * 4) + xTarget * (deltatime * 4);
        } else
            vel.x = vel.x * (1 - deltatime * 8);

        // Handle gravity
        if (!isOnGround())
            vel.y += gravity * deltatime;

        // Check to see if we need to jump
        if (jump) {
            jump = false;
            touchingGround = false;
            vel.y += jumpForce;
        }

        //Update the new position
        pos.x += vel.x;
        pos.y += vel.y;

        updateColliders();
    }

    @Override
    public void render(float deltatime, Batch batch) {
        currentFrame = playerAnimation.getCurrentFrame(deltatime);
        float width = currentFrame.getRegionWidth() * GameScreen.unitScale;
        float dir = getDirFaceing();

        // If the player is facing to the left, scale the animation frame to be negative to flip it.
        // Also the position of the frame needs to be shifted to the right by the width of the frame
        batch.draw(currentFrame, dir < 0 ? pos.x + width : pos.x, pos.y, playerAnimation.getScaleX() * dir, playerAnimation.getScaleY());
    }

    @Override
    public void debugRender(float deltatime, ShapeRenderer shapeRenderer) {
        /*-shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(playerCollider.x, playerCollider.y, playerCollider.width, playerCollider.height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(groundCollider.x, groundCollider.y, groundCollider.width, groundCollider.height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.rect(collRegion.x, collRegion.y, collRegion.width, collRegion.height);
        shapeRenderer.end();

        if (possibleCollisions != null) {
            for (Rectangle rect : possibleCollisions) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
                shapeRenderer.end();
            }
        }*/
    }

    public void addScore(int value) {
        score += value;
    }

    public int getScore() {
        return score;
    }

    public void updateColliders() {
        playerCollider.set(pos.x, pos.y, player_width, player_height);
        groundCollider.set(pos.x, pos.y - groundColliderHeight / 2, groundColliderWidth, groundColliderHeight);
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public float getDirFaceing() {
        if (this.isMoving())
            return lastDirFacing = moveLeft ? -1 : 1;
        else
            return lastDirFacing;
    }

    public void tryToJump() {
        if (isOnGround())
            jump = true;
    }

    public void crouch() {
        if (isOnGround())
            crouch = true;
    }

    public void stopCrouch() {
        crouch = false;
    }

    public boolean isJumping() {

        return vel.y > 0;
    }

    public boolean isFalling() {
        return vel.y < 0;
    }

    public boolean isOnGround() {
        return touchingGround;
    }

    public boolean isMoving() {
        return moveLeft || moveRight;
    }

    public boolean isCrouching() {
        return crouch;
    }

    @Override
    public void onEntityCollision(GameEntity obj, Rectangle collider, CollisionHelper collisionHelper) {
    }

    @Override
    public void onWallCollision(Rectangle wall, Rectangle collider, CollisionHelper collisionHelper) {
        if (collider == playerCollider) {
            this.pushOutOfCollision(collider, wall, collisionHelper);
            updateColliders();
        } else if (collider == groundCollider) {
            touchingGround = true;
        }
    }

    @Override
    public void onCollisionCheckBegin() {
        touchingGround = false;
    }

    @Override
    public boolean collidesWithWalls(Rectangle collider) {
        return true;
    }

    @Override
    public boolean collidesWithEntities(Rectangle collider) {
        if (collider == playerCollider)
            return true;
        return false;
    }

    @Override
    public Rectangle[] getColliders() {
        return colliders;
    }

    @Override
    public float getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public float getMaxHealth() {
        return maxHealth;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public void applyDamage(float dmg_value) {
        // The player can't be hurt is they are immune to damage
        if (damageImmune)
            return;
        // If dmg_value is less then zero it would heal instead of damage
        if (dmg_value < 0)
            return;
        currentHealth -= dmg_value;
        if (currentHealth < 0) {
            currentHealth = 0;
            dead = true;
        }

        damageImmune = true;
    }

    @Override
    public void applyHeal(float heal_value) {
        //If heal_value < 0 it would damage instead of heal
        if (heal_value < 0)
            return;

        currentHealth += heal_value;

        if (currentHealth > maxHealth)
            currentHealth = maxHealth;
    }
}
