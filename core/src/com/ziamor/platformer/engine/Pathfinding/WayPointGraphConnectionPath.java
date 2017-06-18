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
}
