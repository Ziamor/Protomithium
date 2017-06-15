package com.ziamor.platformer.engine;

public class WaypointNode {
    public enum WaypointType {
        LEFT_EDGE,
        RIGHT_EDGE,
        SOLO_EDGE,
        NORMAL;
    }

    WaypointType type;

    public WaypointNode(WaypointType type) {
        this.type = type;
    }

    public WaypointType getType() {
        return type;
    }
}
