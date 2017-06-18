package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class WaypointConnection implements Connection<WaypointNode> {

    private WaypointNode fromNode, toNode;

    public WaypointConnection(WaypointNode fromNode, WaypointNode toNode) {
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    public void renderConnection(ShapeRenderer shapeRenderer, boolean isPath) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (isPath)
            shapeRenderer.setColor(0f, 1f, 0f, 1f);
        else
            shapeRenderer.setColor(0f, 0f, 0f, 1f);
        shapeRenderer.line(fromNode.getX() + 0.5f, fromNode.getY() + 0.5f, toNode.getX() + 0.5f, toNode.getY() + 0.5f);
        shapeRenderer.end();
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
