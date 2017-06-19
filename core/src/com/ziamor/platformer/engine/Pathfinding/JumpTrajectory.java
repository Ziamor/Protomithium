package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.math.Vector2;

public class JumpTrajectory {
    Vector2[] points;
    float x_falling_start = 0;
    float jump_force, xVel;

    public JumpTrajectory(float gravity, float jump_force, float xVel, float interval_length, int num_intervals) {
        this.jump_force = jump_force;
        this.xVel = xVel;
        points = new Vector2[num_intervals];
        for (int i = 1; i <= points.length; i++) {
            float t = (float) i * interval_length;
            float x = xVel * t;
            float y = (jump_force * t) + 0.5f * gravity * t * t;
            points[i - 1] = new Vector2(x, y);
        }

        x_falling_start = xVel * (-jump_force / gravity - 1);
    }

    public boolean isFalling(float x, boolean reverseX) {
        if (!reverseX && x >= x_falling_start)
            return true;
        else if (reverseX && x <= -x_falling_start)
            return true;
        return false;
    }

    public float getxVel() {
        return xVel;
    }

    public float getJump_force() {
        return jump_force;
    }
}
