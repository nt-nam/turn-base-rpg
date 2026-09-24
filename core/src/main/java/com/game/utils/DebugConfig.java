package com.game.utils;

/**
 * Co dieu khien cac cong cu debug (debug draw, check atlas...).
 * Mac dinh TAT cho ban release. Bat len khi can chan doan.
 *
 * <p>Cac class/he thong debug (CheckRegionScreen, DebugDrawSystem, SpriteDebugRenderSystem)
 * nen kiem tra {@link #ENABLED} truoc khi kich hoat, thay vi bat/tat bang cach comment code.
 */
public final class DebugConfig {

    /** Bat/tat toan bo cong cu debug. */
    public static boolean ENABLED = false;

    private DebugConfig() {
    }
}
