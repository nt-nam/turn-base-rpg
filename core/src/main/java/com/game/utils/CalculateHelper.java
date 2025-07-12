package com.game.utils;

public class CalculateHelper {
    public static int xp(int level) {
        return (int) ((level * (level * 0.5f)) * 100);
    }

    public static int statLevel(int statRoot, int level) {
        return (int) (statRoot*Math.pow(1.1, level));
    }

    public static int statStar(int statRoot, int level) {
        return (int) (statRoot*Math.pow(1.5, level));
    }
}
