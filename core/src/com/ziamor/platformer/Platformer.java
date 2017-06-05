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
import com.badlogic.gdx.math.Vector2;
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

    Batch batch;
    ShapeRenderer shapeRenderer;
    Texture playerSpriteSheet;

    CollisionHelper collisionHelper;

    @Override
    public void create() {
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();
        tiledMap = new TmxMapLoader().load("level1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 128f);
        batch = tiledMapRenderer.getBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 25, 15);
        camera.update();

        playerSpriteSheet = new Texture("spritesheet_players.png");

        playerEntity = new PlayerEntity(playerSpriteSheet, new Vector2(6, 5));
        inputProcessor = new PlayerInputProcessor(playerEntity);
        Gdx.input.setInputProcessor(inputProcessor);

        collisionHelper = new CollisionHelper(tiledMap);
    }

    @Override
    public void render() {
        float deltatime = Gdx.graphics.getDeltaTime();
        //Update game objects

        //Update playerEntity
        playerEntity.update(collisionHelper, deltatime);

        //Graphics stuff
        Gdx.gl.glClearColor(0.2f, 0.4f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(backgroundLayers);

        batch.begin();
        playerEntity.render(deltatime, batch);
        batch.end();

        if (debug) {
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            playerEntity.debugRender(deltatime, shapeRenderer);
        }
    }

    @Override
    public void dispose() {
        playerSpriteSheet.dispose();
        tiledMapRenderer.dispose();
    }
}