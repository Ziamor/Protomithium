package com.ziamor.platformer.Entities.Enemies;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class FollowPathTask extends LeafTask<EnemyEntity> {
    @Override
    public Status execute() {
        EnemyEntity ent = this.getObject();
        if (ent.isDestReached())
            return Status.SUCCEEDED;

        if (ent.followPath())
            return Status.RUNNING;

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<EnemyEntity> copyTo(Task<EnemyEntity> task) {
        return null;
    }
}
