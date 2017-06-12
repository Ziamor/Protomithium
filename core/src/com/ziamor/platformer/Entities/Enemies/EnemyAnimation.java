package com.ziamor.platformer.Entities.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by ziamor on 6/7/2017.
 */
public class EnemyAnimation {
    float stateTime, scaleX = 1, scaleY = 1;
    Animation<TextureRegion> walkAnimation, idleAnimation, jumpAnimation, fallAnimation;
    Texture spriteSheet;
    TextureRegion currentFrame;
    Animation<TextureRegion> currentAnimation;

    public EnemyAnimation(Texture spriteSheet) {

        this.spriteSheet = spriteSheet;

        TextureRegion[][] allFrames = TextureRegion.split(spriteSheet, 128, 128);
        TextureRegion[] walkFrames = {allFrames[4][1], allFrames[1][1], allFrames[2][1], allFrames[1][1]};
        walkAnimation = new Animation<TextureRegion>(0.2f, walkFrames);
        idleAnimation = new Animation<TextureRegion>(0.2f, allFrames[4][1]);
        jumpAnimation = new Animation<TextureRegion>(0.2f, allFrames[1][1]);
        fallAnimation = new Animation<TextureRegion>(0.2f, allFrames[2][1]);

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
