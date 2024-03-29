package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class WaypointConnection implements Connection<WaypointNode> {
    public enum ConnectionType {
        NORMAL, DROP, JUMP
    }

    private WaypointNode fromNode, toNode;
    private ConnectionType type;

    public WaypointConnection(WaypointNode fromNode, WaypointNode toNode) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.type = ConnectionType.JUMP;
    }

    public void renderConnection(ShapeRenderer shapeRenderer, boolean isPath) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (isPath)
            shapeRenderer.setColor(0f, 1f, 0f, 1f);
        else {
            switch (type) {
                case NORMAL:
                    shapeRenderer.setColor(0f, 0, 0, 1f);
                    break;
                case DROP:
                    shapeRenderer.setColor(0f, 0, 1f, 1f);
                    break;
                case JUMP:
                    shapeRenderer.setColor(0.5f, 0.2f, 0f, 1f);
                    break;
            }
        }
        shapeRenderer.line(fromNode.getX() + 0.5f, fromNode.getY() + 0.5f, toNode.getX() + 0.5f, toNode.getY() + 0.5f);
        shapeRenderer.end();
    }

    public ConnectionType getType() {
        return type;
    }

    @Override
    public float getCost() {
        return 1;
    }

    @Override
    public WaypointNode getFromNode() {
        return fromNode;
    }

    @Override
    public WaypointNode getToNode() {
        return toNode;
    }
}
