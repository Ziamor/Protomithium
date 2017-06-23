package com.ziamor.platformer.Entities.Enemies;

import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ziamor.platformer.engine.Pathfinding.WayPointGraphNodePath;
import com.ziamor.platformer.engine.Pathfinding.WayPointHeuristic;
import com.ziamor.platformer.engine.Pathfinding.WaypointGraph;
import com.ziamor.platformer.engine.Pathfinding.WaypointNode;

/**
 * Created by ziamor on 6/23/2017.
 */
public class PathFollower {
    private PathFinder<WaypointNode> pathfinder;
    private WaypointGraph graph;
    private WayPointHeuristic heuristic;
    private WayPointGraphNodePath path;
    private WaypointNode startNode, destNode, prevNode, curNode;
    private WaypointNode[] nodePath;

    private Vector2 targetPos;
    private boolean foundPath, destReached;
    private float min_pos_err;
    private int pathIndex;

    public PathFollower(WaypointGraph graph, float min_pos_err) {
        this.graph = graph;
        this.min_pos_err = min_pos_err;

        this.pathfinder = new IndexedAStarPathFinder<WaypointNode>(graph);
        this.heuristic = new WayPointHeuristic();
        this.path = new WayPointGraphNodePath();

        this.targetPos = new Vector2();
    }

    public boolean findPath(Vector2 start, Vector2 dest) {
        if (path == null || start == null || dest == null)
            return false;

        reset();

        startNode = graph.getNode((int) start.x, (int) start.y, 0);
        destNode = graph.getNode((int) dest.x, (int) dest.y, 0);

        foundPath = pathfinder.searchNodePath(startNode, destNode, heuristic, path);

        path.simplifyPath();

        if (foundPath && path.getCount() > 1) {
            prevNode = path.get(0);
            curNode = path.get(1);

            targetPos.set(curNode.getCenterX(), curNode.getY());

            nodePath = new WaypointNode[path.getCount()];
            for (int i = 0; i < nodePath.length; i++)
                nodePath[i] = path.get(i);

            return true;
        } else {
            path.clear();
            reset();
            return false;
        }
    }

    public Vector2 getCurrentTarget(Vector2 pos) {
        if (curNode == null || destReached)
            return null;
        if (hasReachedTarget(pos))
            if (getNextNode())
                return targetPos.set(curNode.getCenterX(), curNode.getY());
            else
                return null;
        else
            return targetPos;
    }

    public boolean hasReachedTarget(Vector2 pos) {
        if (prevNode == null || curNode == null)
            return false;

        if (destReached)
            return true;

        // Check x pos
        if (prevNode.getX() < curNode.getX()) {
            if (pos.x < curNode.getCenterX())
                return false;
        } else if (pos.x > curNode.getCenterX())
            return false;

        // Check y pos
        if (Math.abs(pos.y - curNode.getCenterY()) < min_pos_err)
            return true;
        if (prevNode.getY() < curNode.getCenterY()) {
            if (pos.y < curNode.getCenterY())
                return false;
        } else if (pos.y > curNode.getCenterY())
            return false;

        return true;
    }

    public boolean getNextNode() {
        if (++pathIndex >= nodePath.length) {
            reset();
            destReached = true;
            return false;
        }
        prevNode = curNode;
        curNode = nodePath[pathIndex];

        return true;
    }

    public boolean isDestReached() {
        return destReached;
    }

    public void reset() {
        foundPath = false;
        path.clear();
        pathIndex = 0;
        destReached = false;
        curNode = null;
        prevNode = null;
        startNode = null;
        destNode = null;
    }

    public void debugRender(ShapeRenderer shapeRenderer) {
        if (!destReached && path != null) {
            for (WaypointNode n : path) {
                n.renderNode(shapeRenderer, false);
            }
        }
        if (curNode != null)
            curNode.renderNode(shapeRenderer, true);
    }
}
