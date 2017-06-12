package com.ziamor.platformer.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.ziamor.platformer.Entities.GameEntity;

/**
 * Created by ziamor on 6/12/2017.
 */
public class TargetOrthographicCamera extends OrthographicCamera {
    private float mapWidth, mapHeight, lerp = 5f;
    private GameEntity target;

    public TargetOrthographicCamera(float mapWidth, float mapHeight) {
        this(null, mapWidth, mapHeight);
    }

    public TargetOrthographicCamera(GameEntity target, float mapWidth, float mapHeight) {
        this.target = target;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void setTarget(GameEntity newTarget) {
        this.target = newTarget;
    }

    public void UpdatePosition(float delta) {
        if (target == null)
            return;
        float x_vel = (target.getPos().x - position.x) * lerp * delta;
        float y_vel = (target.getPos().y - position.y) * lerp * delta;
        position.x = MathUtils.clamp(position.x + x_vel, viewportWidth / 2, mapWidth - viewportWidth / 2);
        position.y = MathUtils.clamp(position.y + y_vel, viewportHeight / 2, mapHeight - viewportHeight / 2);
    }
}
