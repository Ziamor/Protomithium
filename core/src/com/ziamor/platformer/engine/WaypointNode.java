package com.ziamor.platformer.engine;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private int x, y, index;
    private Array<Connection<WaypointNode>> connections;

    public WaypointNode(WayPointType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.index = current_index++;
        this.connections = new Array<Connection<WaypointNode>>();
    }

    public void addConnection(WaypointNode prev) {
        if (prev == null)
            return;

        Connection<WaypointNode> newConnection = new WaypointConnection(this, prev);
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
        shapeRenderer.circle(x + 0.5f, y + 0.5f, 0.15f, 6);
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
        return x;
    }

    public int getY() {
        return y;
    }

    public int getIndex() {
        return index;
    }
}
