package com.ziamor.platformer.Entities.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * Created by ziamor on 5/31/2017.
 */
public enum PlayerState implements State<PlayerEntity> {
    IDLE() {
        @Override
        public void enter(PlayerEntity playerEntity) {
            playerEntity.playerAnimation.setCurrentAnimation("idle");
        }

        @Override
        public void update(PlayerEntity playerEntity) {
            if (playerEntity.isMoving()) {
                playerEntity.playerStateMachine.changeState(WALKING);
            } else if (playerEntity.isCrouching()) {
                playerEntity.playerStateMachine.changeState(CROUCHING);
            }
        }
    },

    WALKING() {
        @Override
        public void enter(PlayerEntity playerEntity) {
            playerEntity.playerAnimation.setCurrentAnimation("walk");
        }

        @Override
        public void update(PlayerEntity playerEntity) {
            if (!playerEntity.isMoving()) {
                playerEntity.playerStateMachine.changeState(IDLE);
            } else if (playerEntity.isCrouching()) {
                playerEntity.playerStateMachine.changeState(CROUCHING);
            }
        }
    },
    CROUCHING() {
        @Override
        public void enter(PlayerEntity playerEntity) {
            playerEntity.playerAnimation.setCurrentAnimation("crouch");
        }

        @Override
        public void update(PlayerEntity playerEntity) {
            if (!playerEntity.isCrouching()) {
                playerEntity.playerStateMachine.changeState(IDLE);
            }
        }
    },
    JUMP() {
        @Override
        public void enter(PlayerEntity playerEntity) {
            playerEntity.playerAnimation.setCurrentAnimation("jump");
        }

        @Override
        public void update(PlayerEntity playerEntity) {
            if (playerEntity.isOnGround()) {
                playerEntity.playerStateMachine.changeState(IDLE);
            }
        }
    },
    FALLING() {
        @Override
        public void enter(PlayerEntity playerEntity) {
            playerEntity.playerAnimation.setCurrentAnimation("fall");
        }

        @Override
        public void update(PlayerEntity playerEntity) {
            if (playerEntity.isOnGround()) {
                playerEntity.playerStateMachine.changeState(IDLE);
            }
        }
    },
    GLOBAL_STATE() {
        @Override
        public void update(PlayerEntity playerEntity) {
            if (playerEntity.isJumping()) {
                playerEntity.playerStateMachine.changeState(JUMP);
            } else if (playerEntity.isFalling()) {
                playerEntity.playerStateMachine.changeState(FALLING);
            }
        }
    };

    @Override
    public void enter(PlayerEntity entity) {

    }

    @Override
    public void update(PlayerEntity entity) {
    }

    @Override
    public void exit(PlayerEntity entity) {
    }

    @Override
    public boolean onMessage(PlayerEntity entity, Telegram telegram) {
        return true;
    }
}
