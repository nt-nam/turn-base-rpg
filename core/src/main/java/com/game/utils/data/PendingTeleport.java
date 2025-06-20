package com.game.utils.data;

public class PendingTeleport {
    public String nextMap;
    public int nextSpawn;
    public String name;
    public PendingTeleport(String map, int spawn, String name) {
        this.nextMap = map;
        this.nextSpawn = spawn;
        this.name = name;
    }
}
