package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.engine.CollisionHelper;
import com.ziamor.platformer.engine.GameLevel;

public class WaypointGraph implements IndexedGraph<WaypointNode> {

    private GameLevel level;
    private boolean[][] blockers;
    private WaypointNode[][] nodeMatrix;
    private Array<WaypointNode> nodes;
    private float unit_width, unit_height;

    //TODO remove to local
    Array<JumpTrajectory> jumpTrajectories;

    public WaypointGraph(GameLevel level, float gravity, float jump_force, float xVel, float unit_width, float unit_height, CollisionHelper collisionHelper) {
        this.level = level;
        this.unit_width = unit_width;
        this.unit_height = unit_height;
        createGraph(gravity, jump_force, xVel, collisionHelper);
    }

    public void createGraph(float gravity, float jump_force, float xVel, CollisionHelper collisionHelper) {
        blockers = level.getBlockingMatrix();
        nodeMatrix = new WaypointNode[blockers.length][blockers[0].length];
        nodes = new Array<WaypointNode>();

        // Construct the nodes and add platform connections
        for (int y = 0; y < blockers[0].length; y++) {
            boolean buildingPlatform = false;
            for (int x = 0; x < blockers.length; x++) {
                if (!blockers[x][y]) {
                    WaypointNode newNode = null;
                    if (buildingPlatform) {
                        if (isPlatform(x, y))
                            if (isPlatform(x + 1, y))
                                newNode = new WaypointNode(WaypointNode.WayPointType.NORMAL, x, y);
                            else
                                newNode = new WaypointNode(WaypointNode.WayPointType.RIGHT_EDGE, x, y);
                        else
                            buildingPlatform = false;
                    } else {
                        if (isPlatform(x, y)) {
                            if (isPlatform(x + 1, y))
                                newNode = new WaypointNode(WaypointNode.WayPointType.LEFT_EDGE, x, y);
                            else
                                newNode = new WaypointNode(WaypointNode.WayPointType.SOLO_EDGE, x, y);
                            buildingPlatform = true;
                        }
                    }
                    if (newNode != null) {
                        nodes.add(newNode);
                        nodeMatrix[x][y] = newNode;
                        WaypointNode prev = getNode(x - 1, y);
                        if (prev != null) {
                            newNode.addConnection(prev, WaypointConnection.ConnectionType.NORMAL);
                            prev.addConnection(newNode, WaypointConnection.ConnectionType.NORMAL);
                        }
                    }
                } else
                    buildingPlatform = false;
            }
        }

        // Generate drop connections
        for (WaypointNode node : nodes) {
            if (node.isLeftEdge()) {
                WaypointNode dropNode = getFirstDropPlatform(node.getX() - 1, node.getY());
                if (dropNode != null)
                    node.addConnection(dropNode, WaypointConnection.ConnectionType.DROP);
            }
            if (node.isRightEdge()) {
                WaypointNode dropNode = getFirstDropPlatform(node.getX() + 1, node.getY());
                if (dropNode != null)
                    node.addConnection(dropNode, WaypointConnection.ConnectionType.DROP);
            }
        }

        //Generate jump connections
        // First we need to calculate some jump trajectories around point(0,0)
        jumpTrajectories = new Array<JumpTrajectory>();

        float interval_length = 3f;
        int interval = 30;
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                jumpTrajectories.add(new JumpTrajectory(gravity, jump_force / y, xVel / x, interval_length, interval));
            }
        }
        Vector2 offset = new Vector2();
        Rectangle collider = new Rectangle();
        Array<Rectangle> possibleColliders = new Array<Rectangle>();
        for (WaypointNode node : nodes) {
            for (JumpTrajectory jp : jumpTrajectories) {
                for (Vector2 vec : jp.points)
                    if (checkForJumpConnection(jp, vec, offset, collider, node, collisionHelper, possibleColliders, false))
                        break;
                for (Vector2 vec : jp.points)
                    if (checkForJumpConnection(jp, vec, offset, collider, node, collisionHelper, possibleColliders, true))
                        break;
            }
        }
    }

    public boolean checkForJumpConnection(JumpTrajectory jp, Vector2 vec, Vector2 offset, Rectangle collider, WaypointNode node, CollisionHelper collisionHelper, Array<Rectangle> possibleColliders, boolean reverseX) {
        if (reverseX)
            offset.set(vec.x * -1f + node.getX(), vec.y + node.getY());
        else
            offset.set(vec.x + node.getX(), vec.y + node.getY());

        collider.set(offset.x, offset.y, unit_width, unit_height);
        possibleColliders = collisionHelper.getPossibleCollisions(collider, possibleColliders, "walls");
        for (Rectangle blocker : possibleColliders) {
            if (collider.overlaps(blocker)) {
                return true;// Blocked, stop search
            }
        }

        WaypointNode nodeToCheck = getNode((int) offset.x, (int) offset.y);
        if (nodeToCheck != null && nodeToCheck != node)
            if (jp.isFalling(vec.x, reverseX)) {
                node.addConnection(nodeToCheck, jp);
                //Found a connection, stop search
                return true;
            }
        // Keep searching
        return false;
    }

    public void debugRender(ShapeRenderer shapeRenderer) {
        for (WaypointNode n : nodes) {
            n.renderNode(shapeRenderer, false);
            for (Connection<WaypointNode> c : n.getConnections()) {
                ((WaypointConnection) c).renderConnection(shapeRenderer, false);
            }
        }
        for (Vector2 vec : jumpTrajectories.first().points) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            if (jumpTrajectories.first().isFalling(vec.x, false))
                shapeRenderer.setColor(1f, 0f, 0f, 1f);
            else
                shapeRenderer.setColor(0f, 1f, 0f, 1f);
            shapeRenderer.circle(vec.x + 5, vec.y + 12, 0.1f, 6);
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(vec.x + 5, vec.y + 12, unit_width, unit_height);
            shapeRenderer.end();
        }
    }

    private boolean isPlatform(int x, int y) {
        // Check that we are not going out of bounds
        if (x >= blockers.length || y <= 0)
            return false;
        // If the tile is a blocker then it's not a platform
        if (blockers[x][y])
            return false;
        // Check if the lower tile is solid
        if (blockers[x][y - 1])
            return true;
        // The tile below was not solid, not a platform
        return false;
    }

    public WaypointNode getNode(int x, int y) {
        if (x < 0 || x >= nodeMatrix.length)
            return null;
        if (y < 0 || y >= nodeMatrix[x].length)
            return null;
        return nodeMatrix[x][y];
    }

    /**
     * From an x and y pos find the first platform that the entity would hit if it were to drop,
     * will return null if no platform is found or the drop point is blocked.
     *
     * @param x
     * @param y
     * @return
     */
    public WaypointNode getFirstDropPlatform(int x, int y) {
        if (x < 0 || x >= nodeMatrix.length)
            return null;
        if (y < 0 || y >= nodeMatrix[x].length)
            return null;
        // Check if the start tile is blocked
        if (blockers[x][y])
            return null;
        for (int i = y; i >= 0; i--)
            if (nodeMatrix[x][i] != null)
                return nodeMatrix[x][i];
        return null;
    }

    @Override
    public int getIndex(WaypointNode node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }

    @Override
    public Array<Connection<WaypointNode>> getConnections(WaypointNode fromNode) {
        return fromNode.getConnections();
    }
}
