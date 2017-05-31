package com.ziamor.platformer.Entities.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by ziamor on 5/30/2017.
 */
public class PlayerAnimation {
    final int frameCols = 8, frameRows = 8;
    float stateTime, scaleX = 1, scaleY = 2;
    Animation<TextureRegion> walkAnimation, idleAnimation, jumpAnimation, fallAnimation, crouchAnimation;
    Texture spriteSheet;
    TextureRegion currentFrame;
    Animation<TextureRegion> currentAnimation;

    public PlayerAnimation(Texture spriteSheet) {
        this.spriteSheet = spriteSheet;

        TextureRegion[][] allFrames = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / frameCols, spriteSheet.getHeight() / frameRows);
        TextureRegion[] walkFrames = {allFrames[0][0], allFrames[1][0]};
        walkAnimation = new Animation<TextureRegion>(0.2f, walkFrames);
        idleAnimation = new Animation<TextureRegion>(0.2f, allFrames[4][0]);
        jumpAnimation = new Animation<TextureRegion>(0.2f, allFrames[2][0]);
        fallAnimation = new Animation<TextureRegion>(0.2f, allFrames[6][0]);
        crouchAnimation = new Animation<TextureRegion>(0.2f, allFrames[0][1]);

        stateTime = 0;
        currentAnimation = idleAnimation;
    }

    public TextureRegion getCurrentFrame(float deltaTime) {
        stateTime += deltaTime;

        currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        return currentFrame;
    }

    public void setCurrentAnimation(String state) {
        if (state.equals("idle")) {
            currentAnimation = idleAnimation;
        } else if (state.equals("walk")) {
            currentAnimation = walkAnimation;
        } else if (state.equals("jump")) {
            currentAnimation = jumpAnimation;
        } else if (state.equals("fall")) {
            currentAnimation = fallAnimation;
        } else if (state.equals("crouch")) {
            currentAnimation = crouchAnimation;
        } else
            Gdx.app.debug("Invalid Animation name", state);
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }
}
