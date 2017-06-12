package com.ziamor.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.Entities.Enemies.EnemyEntity;
import com.ziamor.platformer.Entities.GameEntity;
import com.ziamor.platformer.Entities.Items.Coin;
import com.ziamor.platformer.Entities.Player.PlayerEntity;
import com.ziamor.platformer.Entities.Player.PlayerInputProcessor;
import com.ziamor.platformer.engine.CollisionHelper;
import com.ziamor.platformer.engine.TargetOrthographicCamera;

public class GameScreen implements Screen {
    public static float unitScale = 1 / 128f;

    private final boolean debug = false;

    float screenPxWidth, screenPxHeight, screenWidth = 25, screenHeight = 15;
    int mapWidth = 75, mapHeight = 15;
    int[] backgroundLayers = {0};

    Stage stage;
    Table table;
    Skin skin;
    Label lblScore;
    ProgressBar healthBar;

    Platformer game;

    InputMultiplexer inputMultiplexer;

    TargetOrthographicCamera camera;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    TiledMap tiledMap;

    PlayerEntity playerEntity;
    PlayerInputProcessor inputProcessor;

    EnemyEntity enemyEntity;

    Batch batch;
    ShapeRenderer shapeRenderer;
    Texture playerSpriteSheet, enemySpriteSheet, itemSpriteSheet;
    TextureRegion[][] itemTextures;

    com.ziamor.platformer.engine.CollisionHelper collisionHelper;

    Array<GameEntity> entities;
    Array<com.ziamor.platformer.engine.Collidable> collidables;
    Array<Rectangle> possibleCollisions;


    public GameScreen(Platformer game) {
        this.game = game;
        this.screenPxWidth = Gdx.graphics.getWidth();
        this.screenPxHeight = Gdx.graphics.getHeight();
        this.possibleCollisions = new Array<Rectangle>();
        this.skin = game.skin;

        inputMultiplexer = new InputMultiplexer();

        tiledMap = new TmxMapLoader().load("level1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
        batch = tiledMapRenderer.getBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new TargetOrthographicCamera(mapWidth, mapHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();

        stage = new Stage();
        inputMultiplexer.addProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.top();
        table.left();
        stage.addActor(table);

        table.add(new Label("Health:", skin));
        healthBar = new ProgressBar(0, 100, 1, false, skin);

        table.add(healthBar);
        table.row();
        table.add(new Label("Score:", skin));
        lblScore = new Label("0", skin);
        table.add(lblScore);

        playerSpriteSheet = new Texture("spritesheet_players.png");
        enemySpriteSheet = new Texture("spritesheet_enemies.png");
        itemSpriteSheet = new Texture("spritesheet_items.png");

        playerEntity = new PlayerEntity(playerSpriteSheet, new Vector2(6, 5));
        enemyEntity = new EnemyEntity(enemySpriteSheet, new Vector2(20, 5));
        inputProcessor = new PlayerInputProcessor(playerEntity);
        inputMultiplexer.addProcessor(inputProcessor);

        Gdx.input.setInputProcessor(inputMultiplexer);

        collisionHelper = new CollisionHelper(tiledMap);

        itemTextures = TextureRegion.split(itemSpriteSheet, 128, 128);

        entities = new Array<GameEntity>();
        collidables = new Array<com.ziamor.platformer.engine.Collidable>();
        addEntity(playerEntity);
        addEntity(enemyEntity);
        addEntity(new Coin(itemTextures, new Vector2(3, 5)));

        camera.setTarget(playerEntity);
        healthBar.setRange(0, playerEntity.getMaxHealth());
        healthBar.setValue(playerEntity.getCurrentHealth());
        healthBar.setAnimateDuration(1);
    }

    @Override
    public void render(float delta) {

        //Update entities
        for (GameEntity ent : entities) {
            if (ent.isReadyToDispose()) {
                removeEntity(ent);
                continue;
            }
            ent.update(delta);
        }
        // Handle collisions
        //for(int i = 0; i < collidables.size; i++){
        for (com.ziamor.platformer.engine.Collidable ent : collidables) {
            //Collidable ent = collidables.get(i);
            Rectangle[] colliders = ent.getColliders();
            if (colliders != null) {
                //Notify the entity that collision checking is beginning
                ent.onCollisionCheckBegin();
                // Get a list of colliders from the entity
                for (Rectangle collider : colliders) {
                    //Check if the entity cares about wall collisions
                    if (ent.collidesWithWalls(collider)) {
                        // Get a list of all walls the entities collider may be colliding with
                        collisionHelper.getPossibleCollisions(collider, possibleCollisions, "walls");
                        for (Rectangle wall : possibleCollisions) {
                            // If the collider is overlapping, notify the object
                            if (collider.overlaps(wall)) {
                                ent.onWallCollision(wall, collider, collisionHelper);
                            }
                        }
                    }
                    //TODO, how collisions with other entities are handled doesn't feel right, look in to it later
                    //Check if the entity cares about entity collisions
                    if (ent.collidesWithEntities(collider)) {
                        // Libgdx does not allow for nested iterators, can't use a second for each
                        for (int i = 0; i < collidables.size; i++) {
                            com.ziamor.platformer.engine.Collidable possibleEntityBlocker = collidables.get(i);
                            //for (Collidable possibleEntityBlocker : collidables) {
                            //Don't check if an entity is colliding with itself
                            if (ent == possibleEntityBlocker)
                                continue;
                            for (Rectangle blockerCollider : possibleEntityBlocker.getColliders()) {
                                //If the collider doesn't collide with other entities, we skip it
                                if (!possibleEntityBlocker.collidesWithEntities(blockerCollider))
                                    continue;
                                // If the collider is overlapping, notify the object
                                if (collider.overlaps(blockerCollider)) {
                                    ent.onEntityCollision((GameEntity) possibleEntityBlocker, collider, collisionHelper);
                                }
                            }
                        }
                    }
                }
            }
        }

        //Clear the screen
        Gdx.gl.glClearColor(0.2f, 0.4f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.UpdatePosition(delta);
        camera.update();

        // Render the background
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(backgroundLayers);

        batch.begin();
        // Render entities
        for (GameEntity ent : entities)
            ent.render(delta, batch);
        batch.end();

        lblScore.setText(playerEntity.getScore() + "");
        healthBar.setValue(playerEntity.getCurrentHealth());

        stage.act(delta);
        stage.draw();

        if (debug) {
            //Gdx.app.log("Number of ent", entities.size + "");
            //Gdx.app.log("Number of collidable ents", collidables.size + "");

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            for (GameEntity ent : entities)
                ent.debugRender(delta, shapeRenderer);
        }
    }

    protected void addEntity(GameEntity ent) {
        if (ent == null) {
            Gdx.app.debug("Error", "Tried to add a null entity");
            return;
        }
        entities.add(ent);
        if (ent instanceof com.ziamor.platformer.engine.Collidable)
            collidables.add((com.ziamor.platformer.engine.Collidable) ent);
    }

    protected void removeEntity(GameEntity ent) {
        if (ent == null) {
            Gdx.app.debug("Error", "Tried to remove a null entity");
            return;
        }
        entities.removeValue(ent, true);
        collidables.removeValue((com.ziamor.platformer.engine.Collidable) ent, true);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        playerSpriteSheet.dispose();
        enemySpriteSheet.dispose();
        itemSpriteSheet.dispose();
        tiledMapRenderer.dispose();
        stage.dispose();
    }
}
