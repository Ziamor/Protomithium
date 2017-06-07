package com.ziamor.platformer.Entities;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by ziamor on 6/7/2017.
 */
public abstract class GameEntity {
    protected Vector2 pos, vel;

    public GameEntity(Vector2 start_pos){
        this.pos = start_pos;
        this.vel = new Vector2();
    }
}
