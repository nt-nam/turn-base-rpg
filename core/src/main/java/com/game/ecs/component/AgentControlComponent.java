package com.game.ecs.component;

import com.badlogic.ashley.core.Component;

/**
 * Gia lap "y dinh nguoi choi" cho mot entity player tren WorldMap.
 *
 * <p>Component nay KHONG tu di chuyen entity. No chi mo ta MONG MUON cua agent
 * (di dau, san enemy hay di teleport). {@code AgentControlSystem} se doc component
 * va dieu khien nhan vat bang CUNG kenh input voi nguoi choi that
 * ({@code GameSessionManager.moveLeft/Right/Up/Down}) — nen hanh vi giong het bam joystick.
 *
 * <p>Agent (AI ben ngoai) co the dieu khien bang cach doi {@code behavior} / {@code target*}
 * hoac goi cac helper {@code moveTo/hunt/wander/idle}.
 */
public class AgentControlComponent implements Component {

    public enum Behavior {
        /** Dung yen (khong sinh input). */
        IDLE,
        /** Di chuyen toi toa do muc tieu (targetX/targetY). */
        GOTO_POINT,
        /** Tim & tien toi enemy trigger gan nhat tren map. */
        HUNT_ENEMY,
        /** Tim & tien toi teleport trigger gan nhat tren map. */
        GOTO_TELEPORT,
        /** Lang thang ngau nhien (doi huong dinh ky). */
        WANDER
    }

    /** Cong tac tong: false thi System bo qua entity nay (tra quyen cho nguoi choi). */
    public boolean enabled = true;

    public Behavior behavior = Behavior.IDLE;

    /** Toa do muc tieu cho GOTO_POINT (toa do world, cung he voi PositionComponent). */
    public float targetX;
    public float targetY;

    /** Nguong khoang cach (world units) coi la "da toi" muc tieu. */
    public float arriveRadius = 24f;

    /** WANDER: thoi gian con lai cua huong hien tai (giay) va huong dang di. */
    public float wanderTimer = 0f;
    public float wanderDirX = 0f;
    public float wanderDirY = 0f;

    public AgentControlComponent() {
    }

    // ---- Helper cho agent dieu khien (fluent) ----

    public AgentControlComponent moveTo(float x, float y) {
        this.behavior = Behavior.GOTO_POINT;
        this.targetX = x;
        this.targetY = y;
        return this;
    }

    public AgentControlComponent hunt() {
        this.behavior = Behavior.HUNT_ENEMY;
        return this;
    }

    public AgentControlComponent goToTeleport() {
        this.behavior = Behavior.GOTO_TELEPORT;
        return this;
    }

    public AgentControlComponent wander() {
        this.behavior = Behavior.WANDER;
        return this;
    }

    public AgentControlComponent idle() {
        this.behavior = Behavior.IDLE;
        return this;
    }

    public AgentControlComponent enabled(boolean value) {
        this.enabled = value;
        return this;
    }
}
