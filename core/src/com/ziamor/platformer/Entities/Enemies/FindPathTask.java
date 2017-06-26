package com.ziamor.platformer.Entities.Enemies;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class FindPathTask extends LeafTask<EnemyEntity> {

    @Override
    public Status execute() {
        EnemyEntity ent = this.getObject();
        if (ent.getPathToPlayer())
            return Status.SUCCEEDED;
        else
            return Status.FAILED;
    }

    @Override
    protected Task<EnemyEntity> copyTo(Task<EnemyEntity> task) {
        return task;
    }
}
