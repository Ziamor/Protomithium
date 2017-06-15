package com.ziamor.platformer.engine;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class WaypointGraph {

    private GameLevel level;
    private WaypointNode[][] matrix;
    private boolean[][] blockers;

    public WaypointGraph(GameLevel level) {
        this.level = level;

        matrix = new WaypointNode[level.getWidth()][level.getHeight()];

        blockers = level.getBlockingMatrix();

        for (int y = 0; y < matrix[0].length; y++) {
            boolean buildingPlatform = false;
            for (int x = 0; x < matrix.length; x++) {
                if (!blockers[x][y]) {
                    if (buildingPlatform) {
                        if (isPlatform(x, y))
                            if (isPlatform(x + 1, y))
                                matrix[x][y] = new WaypointNode(WaypointNode.WaypointType.NORMAL);
                            else
                                matrix[x][y] = new WaypointNode(WaypointNode.WaypointType.RIGHT_EDGE);
                        else
                            buildingPlatform = false;
                    } else {
                        if (isPlatform(x, y)) {
                            if (isPlatform(x + 1, y))
                                matrix[x][y] = new WaypointNode(WaypointNode.WaypointType.LEFT_EDGE);
                            else
                                matrix[x][y] = new WaypointNode(WaypointNode.WaypointType.SOLO_EDGE);
                            buildingPlatform = true;
                        }
                    }
                }
            }
        }
    }

    public void debugRender(float deltatime, ShapeRenderer shapeRenderer) {
        for (int y = 0; y < matrix[0].length; y++)
            for (int x = 0; x < matrix.length; x++) {
                if (matrix[x][y] != null) {
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    switch (matrix[x][y].getType()) {
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
}
