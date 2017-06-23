package com.ziamor.platformer.Entities.Enemies;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

/**
 * Created by ziamor on 6/22/2017.
 */
public class IsPlayerInRangeTask extends LeafTask<EnemyEntity> {

    @Override
    public Status execute() {
        EnemyEntity ent = getObject();
        if (ent.isPlayerNear())
            return Status.SUCCEEDED;
        return Status.FAILED;
    }

    @Override
    protected Task<EnemyEntity> copyTo(Task<EnemyEntity> task) {
        return task;
    }
}
