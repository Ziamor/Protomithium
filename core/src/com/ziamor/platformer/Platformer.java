package com.ziamor.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.Entities.EnemyEntity;
import com.ziamor.platformer.Entities.GameEntity;
import com.ziamor.platformer.Entities.Player.PlayerEntity;
import com.ziamor.platformer.Entities.Player.PlayerInputProcessor;

public class Platformer extends ApplicationAdapter {
    public static float unitScale = 1 / 128f;
    private final boolean debug = true;
    float width, height;
    int[] backgroundLayers = {0};

    OrthographicCamera camera;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    TiledMap tiledMap;

    PlayerEntity playerEntity;
    PlayerInputProcessor inputProcessor;

    EnemyEntity enemyEntity;

    Batch batch;
    ShapeRenderer shapeRenderer;
    Texture playerSpriteSheet, enemySpriteSheet;

    CollisionHelper collisionHelper;

    Array<GameEntity> entities;
    Array<Collidable> collidables;
    Array<Rectangle> possibleCollisions;

    @Override
    public void create() {
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();
        this.possibleCollisions = new Array<Rectangle>();
        tiledMap = new TmxMapLoader().load("level1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 128f);
        batch = tiledMapRenderer.getBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 25, 15);
        camera.update();

        playerSpriteSheet = new Texture("spritesheet_players.png");
        enemySpriteSheet = new Texture("spritesheet_enemies.png");

        playerEntity = new PlayerEntity(playerSpriteSheet, new Vector2(6, 5));
        enemyEntity = new EnemyEntity(enemySpriteSheet, new Vector2(20, 5));
        inputProcessor = new PlayerInputProcessor(playerEntity);
        Gdx.input.setInputProcessor(inputProcessor);

        collisionHelper = new CollisionHelper(tiledMap);

        entities = new Array<GameEntity>();
        collidables = new Array<Collidable>();
        addEntity(playerEntity);
        addEntity(enemyEntity);
    }

    @Override
    public void render() {
        float deltatime = Gdx.graphics.getDeltaTime();
        //Update game objects

        //Update playerEntity
        playerEntity.update(collisionHelper, deltatime);

        enemyEntity.update(collisionHelper, deltatime);

        for (Collidable ent : collidables) {
            Rectangle[] colliders = ent.getColliders();
            if (colliders != null)
                for (Rectangle collider : colliders) {
                    if (ent.collidesWithWalls(collider)) {
                        collisionHelper.getPossibleCollisions(collider, possibleCollisions, "walls");
                        for (Rectangle wall : possibleCollisions) {
                            if (collider.overlaps(wall))
                                ent.onWallCollision(wall, collider);
                        }
                    }
                }
        }

        //Graphics stuff
        Gdx.gl.glClearColor(0.2f, 0.4f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(backgroundLayers);

        batch.begin();
        playerEntity.render(deltatime, batch);
        enemyEntity.render(deltatime, batch);
        batch.end();

        if (debug) {
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            playerEntity.debugRender(deltatime, shapeRenderer);
        }
    }

    protected void addEntity(GameEntity ent) {
        if (ent == null) {
            Gdx.app.debug("Error", "Tried to add a null entity");
            return;
        }
        entities.add(ent);
        if (ent instanceof Collidable)
            collidables.add((Collidable) ent);
    }

    @Override
    public void dispose() {
        playerSpriteSheet.dispose();
        enemySpriteSheet.dispose();
        tiledMapRenderer.dispose();
    }
}
