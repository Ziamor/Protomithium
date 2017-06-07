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
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.Collidable;
import com.ziamor.platformer.CollisionHelper;
import com.ziamor.platformer.Entities.GameEntity;
import com.ziamor.platformer.Platformer;

/**
 * Created by ziamor on 5/29/2017.
 */
public class PlayerEntity extends GameEntity implements Collidable {
    final float maxX = Platformer.unitScale * 20f, colBumpOut = Platformer.unitScale * 2f;
    final float player_width = Platformer.unitScale * 100, player_height = Platformer.unitScale * 160;
    final float groundColliderWidth = player_width, groundColliderHeight = Platformer.unitScale * 2f;
    float jumpForce = Platformer.unitScale * 20f;
    float gravity = Platformer.unitScale * -18f;
    PlayerAnimation playerAnimation;
    StateMachine<PlayerEntity, PlayerState> playerStateMachine;
    Rectangle playerCollider, groundCollider, collRegion;
    Rectangle[] colliders;
    Array<Rectangle> possibleCollisions, possibleGroundCollisions;
    private boolean moveLeft, moveRight, jump, crouch, touchingGround;
    private float lastDirFacing;
    private TextureRegion currentFrame;

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
        this.possibleCollisions = new Array<Rectangle>();
        this.possibleGroundCollisions = new Array<Rectangle>();
    }

    public void update(CollisionHelper collisionHelper, float deltatime) {
        playerStateMachine.update();

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

        //Gdx.app.log("", "" + pos.x + "\t" + vel.x + "\t" + (pos.x + vel.x));
        //Gdx.app.log("", "" + pos.y + "\t" + vel.y + "\t" + (pos.y + vel.y));
        // Check for collisions
        handleCollisions(collisionHelper);
        if (vel.x < 0.001 && vel.x > -0.001)
            vel.x = 0;

        if (vel.y < 0.001 && vel.y > -0.001)
            vel.y = 0;

        //Update the new position
        pos.x += vel.x;
        pos.y += vel.y;

        playerCollider.set(pos.x, pos.y, player_width, player_height);
        groundCollider.set(pos.x, pos.y - groundColliderHeight / 2, groundColliderWidth, groundColliderHeight);
    }

    public void handleCollisions(CollisionHelper collisionHelper) {
        float newX = pos.x + vel.x;
        float newY = pos.y + vel.y;

        playerCollider.setX(newX);
        playerCollider.setY(newY);

        collisionHelper.getPossibleCollisions(playerCollider, possibleCollisions, "collision");

        for (Rectangle rect : possibleCollisions) {
            if (playerCollider.overlaps(rect)) {
                Vector2 shallowVector = collisionHelper.getShallowAxisVector(playerCollider, rect);
                if (shallowVector.x != 0) {
                    vel.x = 0;
                    if (pos.x < rect.x)
                        pos.x = rect.x - player_width;
                    else
                        pos.x = rect.x + rect.width;
                    playerCollider.setX(pos.x);
                } else if (shallowVector.y != 0) {
                    vel.y = 0;
                    if (pos.y < rect.y)
                        pos.y = rect.y - player_height;
                    else
                        pos.y = rect.y + rect.height;
                    playerCollider.setY(pos.y);
                }
            }
        }

        collisionHelper.getPossibleGroundCollisions(groundCollider, possibleGroundCollisions, "collision");

        touchingGround = false;
        for (Rectangle rect : possibleGroundCollisions) {
            if (groundCollider.overlaps(rect)) {
                touchingGround = true;
                break;
            }
        }
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

    public void render(float deltatime, Batch batch) {
        currentFrame = playerAnimation.getCurrentFrame(deltatime);
        float width = currentFrame.getRegionWidth() * Platformer.unitScale;
        float dir = getDirFaceing();

        // If the player is facing to the left, scale the animation frame to be negative to flip it.
        // Also the position of the frame needs to be shifted to the right by the width of the frame
        batch.draw(currentFrame, dir < 0 ? pos.x + width : pos.x, pos.y, playerAnimation.getScaleX() * dir, playerAnimation.getScaleY());
    }

    public void debugRender(float deltatime, ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
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
        }
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

    public boolean wantsToJump() {
        return jump;
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

    public boolean isMoveLeft() {
        return moveLeft;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    public boolean isInAir() {
        return pos.y > 0;
    }

    public boolean isCrouching() {
        return crouch;
    }

    @Override
    public void onEntityCollision(GameEntity obj, Rectangle collider) {
        Gdx.app.log("Collision", "Hit an entity");
    }

    @Override
    public void onWallCollision(Rectangle wall, Rectangle collider) {
        if(collider != groundCollider)
        Gdx.app.log("Collision", "Hit a wall");
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
