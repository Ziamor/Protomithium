package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class WaypointNode {

    public enum WayPointType {
        LEFT_EDGE,
        RIGHT_EDGE,
        SOLO_EDGE,
        NORMAL
    }

    private static int current_index = 0;
    private WayPointType type;
    private int index;
    private Vector3 pos;
    private Array<Connection<WaypointNode>> connections;

    public WaypointNode(WayPointType type, int x, int y, int z) {
        this.type = type;
        this.pos = new Vector3(x, y, z);
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
            /*switch (type) {
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
            }*/
            switch ((int) pos.z) {
                case 1:
                case 3:
                    shapeRenderer.setColor(0f, 0, 1, 1f);
                    break;
                case 5:
                    shapeRenderer.setColor(0f, 1f, 0f, 1f);
                    break;
                case 6:
                    shapeRenderer.setColor(0.5f, 0.1f, 0f, 1f);
                    break;
                case 7:
                    shapeRenderer.setColor(1f, 0f, 0f, 1f);
                    break;
                default:
                    shapeRenderer.setColor(0f, 0f, 0f, 1f);
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

    public int getZ() {
        return (int) pos.z;
    }

    public float getCenterX() {
        return pos.x + 0.5f;
    }

    public float getCenterY() {
        return pos.y + 0.5f;
    }

    public Vector3 getVector() {
        return pos;
    }

    public int getIndex() {
        return index;
    }
}
