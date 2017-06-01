package com.ziamor.platformer.Entities.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by ziamor on 5/30/2017.
 */
public class PlayerInputProcessor implements InputProcessor, GestureDetector.GestureListener {

    PlayerEntity playerEntity;

    public PlayerInputProcessor(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                playerEntity.setMoveLeft(true);
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                playerEntity.setMoveRight(true);
                break;
            case Input.Keys.SPACE:
            case Input.Keys.W:
            case Input.Keys.UP:
                playerEntity.tryToJump();
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                playerEntity.crouch();
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                playerEntity.setMoveLeft(false);
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                playerEntity.setMoveRight(false);
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                playerEntity.stopCrouch();
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (screenX >= 700) {
            playerEntity.setMoveRight(true);
        } else if (screenX <= 100) {
            playerEntity.setMoveLeft(true);
        } else {
            if (screenY >= 240)
                playerEntity.tryToJump();
            else
                playerEntity.crouch();
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        playerEntity.setMoveRight(false);
        playerEntity.setMoveLeft(false);
        playerEntity.stopCrouch();
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
