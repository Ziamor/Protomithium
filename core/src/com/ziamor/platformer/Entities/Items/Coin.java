package com.ziamor.platformer.Entities.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ziamor.platformer.Entities.GameEntity;
import com.ziamor.platformer.Entities.Player.PlayerEntity;
import com.ziamor.platformer.engine.Collidable;
import com.ziamor.platformer.engine.CollisionHelper;

/**
 * Created by ziamor on 6/8/2017.
 */
public class Coin extends GameEntity implements Collidable {
    final int coinValue = 100;
    float coinWidth = 0.5f, coinHeight = 0.5f, coinOffsetX = 0.25f, getCoinOffsetY = 0.25f;
    protected TextureRegion coinTexture;
    protected Rectangle coinCollider;
    protected Rectangle[] colliders;

    public Coin(TextureRegion[][] itemTextures, Vector2 start_pos) {
        super(start_pos);
        this.coinTexture = itemTextures[3][5];
        this.coinCollider = new Rectangle();
        this.colliders = new Rectangle[]{coinCollider};
        updateColliders();
    }

    public void updateColliders() {
        coinCollider.set(pos.x + coinOffsetX, pos.y + getCoinOffsetY, coinWidth, coinHeight);
    }

    @Override
    public void update(float deltatime) {

    }

    @Override
    public void render(float deltatime, Batch batch) {
        batch.draw(coinTexture, pos.x, pos.y, 1f, 1f);
    }

    @Override
    public void onEntityCollision(GameEntity obj, Rectangle collider, CollisionHelper collisionHelper) {
        if (obj instanceof PlayerEntity) {
            ((PlayerEntity) obj).addScore(coinValue);
            this.dispose();
        }
    }

    @Override
    public void onWallCollision(Rectangle wall, Rectangle collider, TiledMapTileLayer.Cell cell, CollisionHelper collisionHelper) {

    }

    @Override
    public void onCollisionCheckBegin() {

    }

    @Override
    public boolean collidesWithWalls(Rectangle collider) {
        return false;
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
