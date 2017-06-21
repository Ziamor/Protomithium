package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Created by ziamor on 6/16/2017.
 */
public class WayPointGraphConnectionPath implements GraphPath<Connection<WaypointNode>> {
    Array<Connection<WaypointNode>> connections;

    public WayPointGraphConnectionPath() {
        connections = new Array<Connection<WaypointNode>>();
    }

    @Override
    public int getCount() {
        return connections.size;
    }

    @Override
    public Connection<WaypointNode> get(int index) {
        return connections.get(index);
    }

    @Override
    public void add(Connection<WaypointNode> node) {
        connections.add(node);
    }

    @Override
    public void clear() {
        connections.clear();
    }

    @Override
    public void reverse() {
        connections.reverse();
    }

    @Override
    public Iterator<Connection<WaypointNode>> iterator() {
        return connections.iterator();
    }

    public void simplifyPath() {
        WaypointNode startNode = connections.first().getFromNode();
        WaypointNode endNode = connections.get(connections.size - 1).getToNode();

        Array<WaypointNode> nodesToKeep = new Array<WaypointNode>();
        Array<WaypointNode> nodesToPrune = new Array<WaypointNode>();

        for (Connection<WaypointNode> c : connections) {
            nodesToPrune.add(c.getToNode());
        }

        WaypointNode prevNode = startNode;
        for (WaypointNode node : nodesToPrune) {
            if (node != endNode) {
                // Check if the prev node was a jump node, if so add it
                if (prevNode.getZ() == 0 && node.getZ() != 0 && !nodesToKeep.contains(prevNode, false))
                    nodesToKeep.add(prevNode);

                // Check if the node is the first air node
               // if (node.getZ() == 3)
                //    nodesToKeep.add(node);
                    // Check if landing node
                else if (prevNode.getZ() != 0 && node.getZ() == 0)
                    nodesToKeep.add(node);
                    // Check if at apex of jump
                else if ((prevNode.getZ() < 6 && prevNode.getZ() > 0) && node.getZ() == 6)
                    nodesToKeep.add(node);
            }
            prevNode = node;
        }
        nodesToKeep.add(endNode);

        connections.clear();
        prevNode = startNode;
        for (WaypointNode node : nodesToKeep) {
            connections.add(new WaypointConnection(prevNode, node));
            prevNode = node;
        }

        connections.reverse();
    }
}
