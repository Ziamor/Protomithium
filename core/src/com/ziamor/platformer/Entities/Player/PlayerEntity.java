package com.ziamor.platformer.Entities.Player;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.CollisionHelper;
import com.ziamor.platformer.Platformer;

/**
 * Created by ziamor on 5/29/2017.
 */
public class PlayerEntity {
    final float maxX = Platformer.unitScale * 20f, colBumpOut = Platformer.unitScale * 1f;
    final float player_width = Platformer.unitScale * 128, player_height = Platformer.unitScale * 180;
    float jumpForce = Platformer.unitScale * 20f;
    float gravity = Platformer.unitScale * -18f;
    PlayerAnimation playerAnimation;
    Vector2 pos, vel;
    StateMachine<PlayerEntity, PlayerState> playerStateMachine;
    Rectangle AABB, collRegion;
    Array<Rectangle> possibleCollisions;
    private boolean moveLeft, moveRight, jump, crouch;
    private float lastDirFacing;
    private TextureRegion currentFrame;

    public PlayerEntity(Texture spriteSheet, Vector2 start_pos) {
        this.pos = start_pos;
        this.vel = Vector2.Zero;
        this.AABB = new Rectangle(pos.x, pos.y, player_width, player_height);
        this.playerAnimation = new PlayerAnimation(spriteSheet);
        this.playerStateMachine = new DefaultStateMachine<PlayerEntity, PlayerState>(this, PlayerState.IDLE, PlayerState.GLOBAL_STATE);
        this.lastDirFacing = 1;
        this.collRegion = new Rectangle();
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

        if (vel.x < 0.001 && vel.x > -0.001)
            vel.x = 0;

        // Handle movement along the y-axis
        if (pos.y > 5)
            vel.y += gravity * deltatime;
        else if (pos.y < 5) {
            vel.y = 0;
            pos.y = 5;
        }

        // Check to see if we need to jump
        if (jump) {
            jump = false;
            vel.y += jumpForce;
        }

        // Check for collisions
        //Gdx.app.log("", "" + pos.x + "\t" + vel.x + "\t" + (pos.x + vel.x));
        float newX = pos.x + vel.x;
        collRegion.x = Math.min(pos.x, newX);
        collRegion.y = Math.max(pos.y, pos.y + vel.y);
        collRegion.width = Math.abs(vel.x) + player_width;
        collRegion.height = Math.abs(vel.y) + player_height;
        possibleCollisions = collisionHelper.getPossibleCollisions(collRegion, "collision");

        if (possibleCollisions != null) {
            for (Rectangle rect : possibleCollisions) {
                if (AABB.overlaps(rect)) {
                    if (vel.x > 0 && pos.x < rect.x) {
                        if (newX + player_width > rect.x) {
                            //Push the player out of the collision
                            vel.x = -colBumpOut;
                            break;
                        }
                    } else if (vel.x < 0 && pos.x > rect.x) {
                        if (newX < rect.x + rect.width) {
                            //Push the player out of the collision
                            vel.x = colBumpOut;
                            break;
                        }
                    } else {
                        //TODO add case for when vel.x == 0
                    }
                }
            }
        }
        //Update the new position
        pos.x += vel.x;
        pos.y += vel.y;

        AABB.set(pos.x, pos.y, player_width, player_height);
    }

    public void tryToJump() {
        if (pos.y == 5)
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
        shapeRenderer.rect(AABB.x, AABB.y, AABB.width, AABB.height);
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
        return vel.y == 0;
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
}
