package com.ziamor.platformer.engine.Pathfinding;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Created by ziamor on 6/16/2017.
 */
public class WayPointGraphNodePath implements GraphPath<WaypointNode> {

    Array<WaypointNode> nodes;

    public WayPointGraphNodePath() {
        nodes = new Array<WaypointNode>();
    }

    @Override
    public int getCount() {
        return nodes.size;
    }

    @Override
    public WaypointNode get(int index) {
        return nodes.get(index);
    }

    @Override
    public void add(WaypointNode node) {
        nodes.add(node);
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public void reverse() {
        nodes.reverse();
    }

    @Override
    public Iterator<WaypointNode> iterator() {
        return nodes.iterator();
    }

    public void simplifyPath() {
        if (nodes.size < 2)
            return;

        WaypointNode startNode = nodes.first();
        WaypointNode endNode = nodes.get(nodes.size - 1);

        Array<WaypointNode> nodesToKeep = new Array<WaypointNode>();
        Array<WaypointNode> nodesToPrune = new Array<WaypointNode>();

        for (WaypointNode n : nodes) {
            nodesToPrune.add(n);
        }

        WaypointNode prevNode = startNode;

        //TDO get values
        double maxJump = 6;

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
                else if ((prevNode.getZ() < maxJump && prevNode.getZ() > 0) && node.getZ() == maxJump + 2)
                    if (!nodesToKeep.contains(prevNode, false))
                        nodesToKeep.add(prevNode);

            }
            prevNode = node;
        }
        nodesToKeep.add(endNode);

        nodes.clear();
        nodes.add(startNode);
        for (WaypointNode node : nodesToKeep) {
            nodes.add(node);
        }
    }
}
