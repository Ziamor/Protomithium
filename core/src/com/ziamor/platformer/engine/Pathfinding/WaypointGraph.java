package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.engine.GameLevel;

public class WaypointGraph implements IndexedGraph<WaypointNode> {

    private GameLevel level;
    private boolean[][] blockers;
    private WaypointNode[][] nodeMatrix;
    private Array<WaypointNode> nodes;

    public WaypointGraph(GameLevel level) {
        this.level = level;
        createGraph();
    }

    public void createGraph() {
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
                            createDoubleConnection(newNode, prev);
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
                    node.addConnection(dropNode);
            }
            if (node.isRightEdge()) {
                WaypointNode dropNode = getFirstDropPlatform(node.getX() + 1, node.getY());
                if (dropNode != null)
                    node.addConnection(dropNode);
            }
        }
    }

    public void debugRender(ShapeRenderer shapeRenderer) {
        for (WaypointNode n : nodes) {
            n.renderNode(shapeRenderer, false);
            for (Connection<WaypointNode> c : n.getConnections()) {
                ((WaypointConnection) c).renderConnection(shapeRenderer, false);
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

    public WaypointNode getNode(int x, int y) {
        if (x < 0 || x > nodeMatrix.length)
            return null;
        if (y < 0 || y > nodeMatrix[x].length)
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

    public void createDoubleConnection(WaypointNode a, WaypointNode b) {
        a.addConnection(b);
        b.addConnection(a);
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
