package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class WaypointNode {

    public enum WayPointType {
        LEFT_EDGE,
        RIGHT_EDGE,
        SOLO_EDGE,
        NORMAL;
    }

    private static int current_index = 0;
    private WayPointType type;
    private int index;
    private Vector2 pos;
    private Array<Connection<WaypointNode>> connections;

    public WaypointNode(WayPointType type, int x, int y) {
        this.type = type;
        this.pos = new Vector2(x, y);
        this.index = current_index++;
        this.connections = new Array<Connection<WaypointNode>>();
    }

    public void addConnection(WaypointNode prev, WaypointConnection.ConnectionType connectionType) {
        if (prev == null)
            return;

        Connection<WaypointNode> newConnection = new WaypointConnection(this, prev, connectionType);
        connections.add(newConnection);
    }

    public void addConnection(WaypointNode prev, JumpTrajectory jp) {
        if (prev == null)
            return;

        Connection<WaypointNode> newConnection = new WaypointConnection(this, prev, jp);
        connections.add(newConnection);
    }


    public Array<Connection<WaypointNode>> getConnections() {
        return connections;
    }

    public void renderNode(ShapeRenderer shapeRenderer, boolean isPath) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (isPath)
            shapeRenderer.setColor(0f, 1f, 0, 1f);
        else
            switch (type) {
                case NORMAL:
                    shapeRenderer.setColor(0f, 0, 0, 1f);
                    break;
                case LEFT_EDGE:
                    shapeRenderer.setColor(0.5f, 0, 5f, 1f);
                    break;
                case RIGHT_EDGE:
                    shapeRenderer.setColor(0.5f, 0, 0f, 1f);
                    break;
                case SOLO_EDGE:
                    shapeRenderer.setColor(0.5f, 0.25f, 0f, 1f);
                    break;
            }
        shapeRenderer.circle(pos.x + 0.5f, pos.y + 0.5f, 0.15f, 6);
        shapeRenderer.end();
    }

    public boolean isLeftEdge() {
        if (type == WayPointType.LEFT_EDGE || type == WayPointType.SOLO_EDGE)
            return true;
        return false;
    }

    public boolean isRightEdge() {
        if (type == WayPointType.RIGHT_EDGE || type == WayPointType.SOLO_EDGE)
            return true;
        return false;
    }

    public WayPointType getType() {
        return type;
    }

    public int getX() {
        return (int) pos.x;
    }

    public int getY() {
        return (int) pos.y;
    }

    public Vector2 getVector() {
        return pos;
    }

    public int getIndex() {
        return index;
    }
}
