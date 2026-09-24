package com.game.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.game.ecs.component.AgentControlComponent;
import com.game.ecs.component.BoundComponent;
import com.game.ecs.component.EnemyTriggerComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.TeleportTriggerComponent;
import com.game.managers.GameSessionManager;
import com.game.MainGame;
import com.game.screens.ScreenType;

/**
 * Gia lap hanh vi nguoi choi tren WorldMap.
 *
 * <p>Doc {@link AgentControlComponent} cua entity player, chon huong di chuyen theo behavior,
 * roi dieu khien nhan vat bang CUNG kenh input voi nguoi choi that:
 * dat cac flag {@code GameSessionManager.moveLeft/moveRight/moveUp/moveDown}.
 * {@code PlayerInputSystem} se doc cac flag nay va di chuyen entity — nen logic va animation
 * giong het khi nguoi choi bam joystick.
 *
 * <p>Thu tu: System nay chay TRUOC {@code PlayerInputSystem} (priority nho hon). Khi agent
 * dang lai, dat co {@code GameSessionManager.agentDriving = true} de PlayerInputSystem
 * KHONG ghi de flag tu joystick.
 */
public class AgentControlSystem extends EntitySystem {

    private final ComponentMapper<AgentControlComponent> am = ComponentMapper.getFor(AgentControlComponent.class);
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<BoundComponent> bm = ComponentMapper.getFor(BoundComponent.class);

    private final Engine engine;
    private ImmutableArray<Entity> agents;
    private ImmutableArray<Entity> enemyTriggers;
    private ImmutableArray<Entity> teleportTriggers;

    private static final float WANDER_INTERVAL = 1.5f; // giay moi lan doi huong

    public AgentControlSystem(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void addedToEngine(Engine engine) {
        agents = engine.getEntitiesFor(Family.all(
                AgentControlComponent.class, PlayerComponent.class, PositionComponent.class).get());
        enemyTriggers = engine.getEntitiesFor(Family.all(
                EnemyTriggerComponent.class, BoundComponent.class).get());
        teleportTriggers = engine.getEntitiesFor(Family.all(
                TeleportTriggerComponent.class, BoundComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        // Chi lai 1 player-agent (nhan vat nguoi choi). Neu khong co -> tra quyen nguoi choi.
        if (agents == null || agents.size() == 0) {
            GameSessionManager.getInstance().agentDriving = false;
            return;
        }

        Entity player = agents.first();
        AgentControlComponent agent = am.get(player);
        PositionComponent pos = pm.get(player);

        // Dong bo voi cau hinh runtime (API enableAgent/disableAgent): cho phep bat/tat
        // va doi behavior ngay ca khi player da spawn tu truoc.
        GameSessionManager gsm = GameSessionManager.getInstance();
        if (agent != null) {
            agent.enabled = gsm.agentEnabled;
            if (gsm.agentEnabled) {
                try {
                    agent.behavior = AgentControlComponent.Behavior.valueOf(gsm.agentBehavior);
                } catch (IllegalArgumentException | NullPointerException e) {
                    agent.behavior = AgentControlComponent.Behavior.WANDER;
                }
            }
        }

        if (agent == null || !agent.enabled || agent.behavior == AgentControlComponent.Behavior.IDLE) {
            GameSessionManager.getInstance().agentDriving = false;
            return;
        }

        // Xac dinh huong di chuyen mong muon (dx, dy) theo behavior
        float dx = 0f, dy = 0f;

        switch (agent.behavior) {
            case GOTO_POINT: {
                float[] d = toward(pos.x, pos.y, agent.targetX, agent.targetY, agent.arriveRadius);
                dx = d[0];
                dy = d[1];
                break;
            }
            case HUNT_ENEMY: {
                // Da cham enemy -> tu vao battle (giong nguoi choi bam nut tan cong).
                if (gsm.currentEnemy != null) {
                    gsm.enemyMapId = gsm.currentEnemy.id + "";
                    stopMoving();
                    MainGame.getScM().showScreen(ScreenType.BATTLE);
                    return;
                }
                float[] c = nearestBoundCenter(pos, enemyTriggers);
                if (c != null) {
                    float[] d = toward(pos.x, pos.y, c[0], c[1], agent.arriveRadius);
                    dx = d[0];
                    dy = d[1];
                }
                break;
            }
            case GOTO_TELEPORT: {
                // Da cham teleport -> tu di sang map ke tiep.
                if (gsm.pendingTeleport != null) {
                    gsm.profile.pos.x = -1;
                    gsm.profile.pos.y = -1;
                    gsm.profile.area = gsm.targetMapId;
                    stopMoving();
                    MainGame.getScM().showScreen(ScreenType.WORLD_MAP);
                    return;
                }
                float[] c = nearestBoundCenter(pos, teleportTriggers);
                if (c != null) {
                    float[] d = toward(pos.x, pos.y, c[0], c[1], agent.arriveRadius);
                    dx = d[0];
                    dy = d[1];
                }
                break;
            }
            case WANDER: {
                agent.wanderTimer -= deltaTime;
                if (agent.wanderTimer <= 0f || (agent.wanderDirX == 0f && agent.wanderDirY == 0f)) {
                    float angle = MathUtils.random(0f, MathUtils.PI2);
                    agent.wanderDirX = MathUtils.cos(angle);
                    agent.wanderDirY = MathUtils.sin(angle);
                    agent.wanderTimer = WANDER_INTERVAL;
                }
                dx = agent.wanderDirX;
                dy = agent.wanderDirY;
                break;
            }
            default:
                break;
        }

        applyMoveFlags(dx, dy);
    }

    /** Tra ve vector huong (da chuan hoa hoac 0) tu (x,y) toi (tx,ty); 0 neu da trong arriveRadius. */
    private float[] toward(float x, float y, float tx, float ty, float arriveRadius) {
        float dx = tx - x;
        float dy = ty - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist <= arriveRadius || dist == 0f) {
            return new float[]{0f, 0f};
        }
        return new float[]{dx / dist, dy / dist};
    }

    /** Tim tam BoundComponent (rect) gan nhat; null neu khong co. */
    private float[] nearestBoundCenter(PositionComponent pos, ImmutableArray<Entity> triggers) {
        if (triggers == null || triggers.size() == 0) {
            return null;
        }
        float bestDist2 = Float.MAX_VALUE;
        float[] best = null;
        for (Entity e : triggers) {
            BoundComponent b = bm.get(e);
            if (b == null || b.rect == null) {
                continue;
            }
            Rectangle r = b.rect;
            float cx = r.x + r.width / 2f;
            float cy = r.y + r.height / 2f;
            float dx = cx - pos.x;
            float dy = cy - pos.y;
            float d2 = dx * dx + dy * dy;
            if (d2 < bestDist2) {
                bestDist2 = d2;
                best = new float[]{cx, cy};
            }
        }
        return best;
    }

    /** Dat lai flag di chuyen ve 0 (dung truoc khi doi Screen). */
    private void stopMoving() {
        GameSessionManager gsm = GameSessionManager.getInstance();
        gsm.moveLeft = gsm.moveRight = gsm.moveUp = gsm.moveDown = false;
        gsm.agentDriving = false;
    }

    /** Dieu khien nhan vat qua CUNG kenh input voi nguoi choi (flag move* + co agentDriving). */
    private void applyMoveFlags(float dx, float dy) {
        GameSessionManager gsm = GameSessionManager.getInstance();
        final float threshold = 0.2f; // giong nguong joystick trong PlayerInputSystem

        gsm.moveLeft = dx < -threshold;
        gsm.moveRight = dx > threshold;
        gsm.moveUp = dy > threshold;
        gsm.moveDown = dy < -threshold;

        // Bao PlayerInputSystem: agent dang lai -> dung ghi de flag tu joystick.
        gsm.agentDriving = (gsm.moveLeft || gsm.moveRight || gsm.moveUp || gsm.moveDown);
    }
}
