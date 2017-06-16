package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * Created by ziamor on 6/16/2017.
 */
public class WayPointHeuristic implements Heuristic<WaypointNode> {
    @Override
    public float estimate(WaypointNode node, WaypointNode endNode) {
        return 1f;
    }
}
