package com.ziamor.platformer.Entities.Player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by ziamor on 5/30/2017.
 */
public class PlayerInputProcessor implements InputProcessor {

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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
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
}
