package com.ziamor.platformer.Entities.Enemies;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.ziamor.platformer.Entities.GameEntity;
import com.ziamor.platformer.engine.Pathfinding.WaypointNode;

/**
 * Created by ziamor on 6/6/2017.
 */
public enum EnemyState implements State<EnemyEntity> {
    IDLE() {
        @Override
        public void enter(EnemyEntity enemyEntity) {
            enemyEntity.enemyAnimation.setCurrentAnimation("idle");
        }

        @Override
        public void update(EnemyEntity enemyEntity) {
            if (enemyEntity.isMoving()) {
                enemyEntity.stateMachine.changeState(WALKING);
            }
        }
    },
    WALKING() {
        @Override
        public void enter(EnemyEntity enemyEntity) {
            enemyEntity.enemyAnimation.setCurrentAnimation("walk");
        }

        @Override
        public void update(EnemyEntity enemyEntity) {
            if (enemyEntity.isFollowing()) {
                WaypointNode targetNode = enemyEntity.getCurrentNode();
                if (targetNode != null) {
                    if (targetNode.getX() < enemyEntity.getPos().x)
                        enemyEntity.setDirection(GameEntity.Direction.LEFT);
                    else
                        enemyEntity.setDirection(GameEntity.Direction.RIGHT);
                }
                enemyEntity.target.x = enemyEntity.maxX * enemyEntity.getDirectionFacingScale();
            }
            else
                enemyEntity.target.x = 0;
        }
    }, DEAD() {
        @Override
        public void enter(EnemyEntity enemyEntity) {
            enemyEntity.enemyAnimation.setCurrentAnimation("dead");
        }
    }, GLOBAL_STATE() {
        @Override
        public void update(EnemyEntity enemyEntity) {
            if (enemyEntity.isDead())
                enemyEntity.stateMachine.changeState(DEAD);
        }
    };

    @Override
    public void enter(EnemyEntity entity) {

    }

    @Override
    public void update(EnemyEntity entity) {

    }

    @Override
    public void exit(EnemyEntity entity) {

    }

    @Override
    public boolean onMessage(EnemyEntity entity, Telegram telegram) {
        return false;
    }
}
