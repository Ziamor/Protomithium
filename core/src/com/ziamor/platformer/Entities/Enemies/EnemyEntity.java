package com.ziamor.platformer.Entities.Enemies;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.Entities.Damageable;
import com.ziamor.platformer.Entities.GameEntity;
import com.ziamor.platformer.Entities.Player.PlayerEntity;
import com.ziamor.platformer.GameScreen;
import com.ziamor.platformer.engine.Collidable;
import com.ziamor.platformer.engine.CollisionHelper;
import com.ziamor.platformer.engine.Pathfinding.WaypointGraph;

/**
 * Created by ziamor on 6/5/2017.
 */
public class EnemyEntity extends GameEntity implements Collidable, Damageable {
    private Direction dirFaceing;
    final int enemyValue = 500;
    float enemyWidth = 0.75f, enemyHeight = 0.5f, deathTime, maxDeathTime = 3f, maxX = 0.1f, jumpForce = 0.1f;
    float currentHealth, maxHealth = 1;
    boolean dead;
    Vector2 target;
    Rectangle enemyCollider;
    Rectangle[] colliders;
    StateMachine<EnemyEntity, EnemyState> stateMachine;
    EnemyAnimation enemyAnimation;
    TextureRegion currentFrame;
    Array<Rectangle> possibleCollisions;

    boolean followPath;

    private float gravity = GameScreen.gravity;

    Vector2 center;

    private BehaviorTree<EnemyEntity> behaviorTree;

    CollisionHelper collisionHelper;
    PlayerEntity player;

    private PathFollower pathFollower;
    private float max_pos_error;

    public EnemyEntity(Texture spriteSheet, Vector2 start_pos, CollisionHelper collisionHelper, PlayerEntity player, WaypointGraph graph) {
        super(start_pos);
        this.target = new Vector2();
        this.enemyCollider = new Rectangle(pos.x, pos.y, enemyWidth, enemyHeight);
        this.stateMachine = new DefaultStateMachine<EnemyEntity, EnemyState>(this, EnemyState.IDLE, EnemyState.GLOBAL_STATE);
        this.enemyAnimation = new EnemyAnimation(spriteSheet);
        this.possibleCollisions = new Array<Rectangle>();
        this.colliders = new Rectangle[]{enemyCollider};
        this.center = new Vector2();

        this.collisionHelper = collisionHelper;
        this.player = player;

        this.max_pos_error = 0.5f;
        this.pathFollower = new PathFollower(graph, max_pos_error);
        followPath = pathFollower.findPath(start_pos, new Vector2(22, 3));
    }

    @Override
    public void update(float deltatime) {
        stateMachine.update();
        //behaviorTree.step();
        updatePath();

        if (dead) {
            deathTime += deltatime;
            if (deathTime >= maxDeathTime)
                this.dispose();
        } else
            //vel.x = vel.x * (1 - deltatime * 4) + target.x * (deltatime * 4);
            vel.x = target.x;

        vel.y += gravity * deltatime;

        if (target.y != 0) {
            vel.y = target.y;
            target.y = 0;
        }
        //Update the new position
        pos.x += vel.x;
        pos.y += vel.y;

        updateColliders();
        //Gdx.app.log("", vel.toString());
    }

    @Override
    public void render(float deltatime, Batch batch) {
        currentFrame = enemyAnimation.getCurrentFrame(deltatime);
        float width = currentFrame.getRegionWidth() * GameScreen.unitScale;
        float dir = -getDirectionFacingScale();

        // If the player is facing to the left, scale the animation frame to be negative to flip it.
        // Also the position of the frame needs to be shifted to the right by the width of the frame
        batch.draw(currentFrame, dir < 0 ? pos.x + width : pos.x, pos.y, enemyAnimation.getScaleX() * dir, enemyAnimation.getScaleY());
    }

    public void updateColliders() {
        enemyCollider.set(pos.x, pos.y, enemyWidth, enemyHeight);
        enemyCollider.getCenter(center);
    }

    public Direction getDirFaceing() {
        return dirFaceing;
    }

    public float getDirectionFacingScale() {
        if (dirFaceing == Direction.LEFT)
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

        pathFollower.debugRender(shapeRenderer);
    }

    public boolean isMoving() {
        return true;
    }

    @Override
    public void onEntityCollision(GameEntity obj, Rectangle collider, CollisionHelper collisionHelper) {
        if (isDead())
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
    public void onWallCollision(Rectangle wall, Rectangle collider, TiledMapTileLayer.Cell cell, CollisionHelper collisionHelper) {
        this.pushOutOfCollision(collider, wall, collisionHelper);
        updateColliders();
        //dirFaceing = !dirFaceing;
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

    public boolean isFollowing() {
        return followPath;
    }

    private void updatePath() {
        if (!isFollowing() || pathFollower.isDestReached()) {
            target.x = 0;
            return;
        }

        /*if (curNode == null) {
            prevNode = pathNodes[0];
            curNode = pathNodes[pathIndex];
        } else if (pathFollower.hasReachedTargetNode(prevNode, curNode)) {
            if (pathIndex + 1 >= pathNodes.length) {
                target.x = 0;
                return;
            }
            prevNode = pathNodes[pathIndex++];
            curNode = pathNodes[pathIndex];
        }

        if (curNode.getCenterX() < center.x)
            setDirection(GameEntity.Direction.LEFT);
        else
            setDirection(GameEntity.Direction.RIGHT);

        if (curNode.getY() > pos.y)
            target.y = jumpForce;
        target.x = maxX * getDirectionFacingScale();*/
        Vector2 dest = pathFollower.getCurrentTarget(center);
        if (dest != null) {
            if (dest.x < center.x)
                setDirection(GameEntity.Direction.LEFT);
            else
                setDirection(GameEntity.Direction.RIGHT);

            if (dest.y > center.y)// && dest.y - pos.y > max_pos_error)
                target.y = jumpForce;
            target.x = maxX * getDirectionFacingScale();
        }
    }

    public void setDirection(Direction dir) {
        if (dir != dirFaceing) {
            dirFaceing = dir;
            vel.x = 0;//TODO look if this is a good idea}
        }
    }

    public boolean isOnGround() {
        //TODO better way
        return vel.y == 0;
    }

    public void setTree(BehaviorTree<EnemyEntity> behaviorTree) {
        this.behaviorTree = behaviorTree;
    }

    public void patrol() {
        if (dirFaceing == null)
            setDirection(Direction.RIGHT);
        if (dirFaceing == Direction.RIGHT) {
            if (!collisionHelper.isPlatform((int) (pos.x + 1), (int) (pos.y)))
                dirFaceing = Direction.LEFT;
        } else if (!collisionHelper.isPlatform((int) (pos.x - 1), (int) (pos.y)))
            dirFaceing = Direction.RIGHT;

        target.x = maxX * getDirectionFacingScale();
    }

    public boolean isPlayerNear() {
        if (Math.abs(player.getPos().x - pos.x) < 2f)
            if (Math.abs(player.getPos().y - pos.y) < 2f)
                return true;
        return false;
    }

    public boolean moveTowardsPlayer() {
        if (enemyCollider.overlaps(player.getColliders()[0])) {
            target.x = 0;
            return true;
        }

        if (pos.x < player.getPos().x)
            setDirection(Direction.RIGHT);
        else
            setDirection(Direction.LEFT);

        target.x = maxX * getDirectionFacingScale();
        return false;
    }
}
