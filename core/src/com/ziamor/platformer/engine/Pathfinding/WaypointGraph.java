package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.ziamor.platformer.engine.CollisionHelper;
import com.ziamor.platformer.engine.GameLevel;

public class WaypointGraph implements IndexedGraph<WaypointNode> {

    private GameLevel level;
    private boolean[][] blockers;
    private WaypointNode[][][] nodeMatrix;
    private Array<WaypointNode> nodes;
    private float unit_width, unit_height;

    private int maxJumpValue = 3 * 2;//TODO get jump height

    public WaypointGraph(GameLevel level, float gravity, float jump_force, float xVel, float unit_width, float unit_height, CollisionHelper collisionHelper) {
        this.level = level;
        this.unit_width = unit_width;
        this.unit_height = unit_height;
        createGraph(gravity, jump_force, xVel, collisionHelper);
    }

    public void createGraph(float gravity, float jump_force, float xVel, CollisionHelper collisionHelper) {
        blockers = level.getBlockingMatrix();
        nodeMatrix = new WaypointNode[blockers.length][blockers[0].length][maxJumpValue * 2 + 1]; // plus one if for  odd state
        nodes = new Array<WaypointNode>();

        // Start by creating unconnected nodes in spaces that are not blocked
        for (int x = 0; x < nodeMatrix.length; x++)
            for (int y = 0; y < nodeMatrix[x].length; y++)
                for (int z = 0; z < nodeMatrix[x][y].length; z++) {
                    if (!blockers[x][y]) {
                        nodeMatrix[x][y][z] = new WaypointNode(WaypointNode.WayPointType.NORMAL, x, y, z);
                        nodes.add(nodeMatrix[x][y][z]);
                    }
                }

        for (int x = 0; x < nodeMatrix.length; x++)
            for (int y = 0; y < nodeMatrix[x].length; y++)
                for (int z = 0; z < nodeMatrix[x][y].length; z++) {

                    WaypointNode curNode = nodeMatrix[x][y][z];

                    // No node in this spot
                    if (curNode == null)
                        continue;

                    if (z > 0 && isPlatform(x, y)) {
                        WaypointNode platformNode = getNode(x, y, 0);
                        if (platformNode != null)
                            curNode.addConnection(platformNode);
                        continue;
                    }

                    WaypointNode downNode = getNode(x, y - 1, maxJumpValue);
                    WaypointNode upNode = null;
                    WaypointNode leftNode = null;
                    WaypointNode rightNode = null;

                    if (z < maxJumpValue) {
                        // Check if z if even
                        if ((z & 1) == 0) {
                            upNode = getNode(x, y + 1, z + 2);

                            if (isPlatform(x - 1, y))
                                leftNode = getNode(x - 1, y, 0);
                            else
                                leftNode = getNode(x - 1, y, z + 1);

                            if (isPlatform(x + 1, y))
                                rightNode = getNode(x + 1, y, 0);
                            else
                                rightNode = getNode(x + 1, y, z + 1);
                        } else {
                            // z is odd
                            upNode = getNode(x, y + 1, z + 1);
                        }
                    } else if (z == maxJumpValue) {
                        if (isPlatform(x - 1, y))
                            leftNode = getNode(x - 1, y, 0);
                        else
                            leftNode = getNode(x - 1, y, maxJumpValue + 1);

                        if (isPlatform(x + 1, y))
                            rightNode = getNode(x + 1, y, 0);
                        else
                            rightNode = getNode(x + 1, y, maxJumpValue + 1);
                    }
                    if (downNode != null)
                        curNode.addConnection(downNode);
                    if (upNode != null)
                        curNode.addConnection(upNode);
                    if (leftNode != null)
                        curNode.addConnection(leftNode);
                    if (rightNode != null)
                        curNode.addConnection(rightNode);
                }
    }

    public void debugRender(ShapeRenderer shapeRenderer) {
        for (int x = 0; x < nodeMatrix.length; x++)
            for (int y = 0; y < nodeMatrix[x].length; y++) {
                WaypointNode n = nodeMatrix[x][y][0];
                if (n == null)
                    continue;
                //for (WaypointNode n : nodes) {
                n.renderNode(shapeRenderer, false);
                for (Connection<WaypointNode> c : n.getConnections()) {
                    ((WaypointConnection) c).renderConnection(shapeRenderer, false);
                }
            }
    }

    private boolean isPlatform(int x, int y) {
        if (x < 0 || x >= nodeMatrix.length)
            return false;
        if (y < 0 || y >= nodeMatrix[x].length)
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

    public WaypointNode getNode(int x, int y, int z) {
        if (x < 0 || x >= nodeMatrix.length)
            return null;
        if (y < 0 || y >= nodeMatrix[x].length)
            return null;
        if (z < 0 || z >= nodeMatrix[x][y].length)
            return null;
        return nodeMatrix[x][y][z];
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
