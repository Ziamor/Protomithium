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
}
