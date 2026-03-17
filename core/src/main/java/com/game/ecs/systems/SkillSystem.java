package com.game.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.game.combat.BattleConfig;
import com.game.combat.Mappers;
import com.game.combat.TurnResult;
import com.game.combat.TurnResultPool;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.SkillComponent;
import com.game.ecs.component.StatComponent;

public class SkillSystem {
    public TurnResult applySkill(Entity actor, Entity target, SkillComponent skill) {
        TurnResult result = TurnResultPool.getInstance().obtain();
        StatComponent actorStats = Mappers.stat.get(actor);
        StatComponent targetStats = Mappers.stat.get(target);
        if (actorStats == null || targetStats == null) return result;

        // Simple damage calculation
        int baseDamage = actorStats.atk;
        if (skill.id == 2) baseDamage *= 1.5f;
        if (skill.id == 3) baseDamage *= 2.0f;
        result.damage = Math.max(0, baseDamage - targetStats.def);
        targetStats.hp = Math.max(0, targetStats.hp - result.damage);
        result.targetDead = targetStats.hp <= 0;
        return result;
    }
    public TurnResult applySkill2(Entity actor, Entity target, SkillComponent skill,
                                 Array<Entity> playerTeam, Array<Entity> enemyTeam) {
        CharacterComponent ca = Mappers.base.get(actor);
        CharacterComponent ct = Mappers.base.get(target);
        StatComponent a = Mappers.stat.get(actor);
        StatComponent t = Mappers.stat.get(target);
        if (ca == null || ct == null || a == null || t == null || skill == null) {
            return TurnResultPool.getInstance().obtain();
        }

        TurnResult result = TurnResultPool.getInstance().obtain();
        result.actorId = ca.nameRegion;
        result.targetId = ct.nameRegion;
        result.skillUsed = skill.name;
        result.targetEntity = target;

        float multiplier = ca.counters != null && ct.classType != null && ca.counters.contains(ct.classType, false)
            ? BattleConfig.getCounterMultiplier() : 1f;
        boolean isCritical = MathUtils.random() < a.crit * 0.01f;
        float avoidChance = (float) t.agi / (t.agi + a.agi + 1) * 0.5f;
        boolean isAvoid = MathUtils.random() < avoidChance;

        if (skill.effect != null) {
            // Damage
            if (skill.effect.has("damage")) {
                int baseDamage = skill.effect.getInt("damage", 0);
                if (skill.id == 1) {
                    baseDamage = (int) (a.atk * 1.5f - t.def / 2f); // Basic Attack
                }
                int attackTimes = skill.effect.getInt("attackTimes", 1);
                for (int i = 0; i < attackTimes; i++) {
                    int singleDamage = (int) (baseDamage * multiplier);
                    singleDamage = Math.max(singleDamage, 1);
                    singleDamage = isCritical ? (int) (singleDamage * MathUtils.random(1.5f, 2f)) : singleDamage;
                    singleDamage = isAvoid && !isCritical ? 0 : singleDamage;
                    t.hp = Math.max(t.hp - singleDamage, 0);
                    result.damage += singleDamage;
                }
                result.isCritical = isCritical;
                result.targetDead = t.hp <= 0;
            }

            // Armor Boost
            if (skill.effect.has("armor")) {
                float armorBoost = skill.effect.getFloat("armor", 0);
                a.def += armorBoost;
            }

            // Dodge Chance
            if (skill.effect.has("dodgeChance")) {
                float dodgeBoost = skill.effect.getFloat("dodgeChance", 0);
                a.agi += dodgeBoost;
            }

            // Critical Chance
            if (skill.effect.has("critChance")) {
                float critBoost = skill.effect.getFloat("critChance", 0);
                a.crit += critBoost;
            }

            // Mana Regen
            if (skill.effect.has("manaRegen")) {
                float manaRegen = skill.effect.getFloat("manaRegen", 0);
                a.mp += manaRegen;
            }

            // Heal
            if (skill.effect.has("heal")) {
                int healAmount = skill.effect.getInt("heal", 0);
                int numTargets = skill.effect.getInt("targets", 1);
                Array<Entity> targets = selectHealTargets(actor, playerTeam, enemyTeam, skill);
                for (Entity healTarget : targets) {
                    StatComponent targetStat = Mappers.stat.get(healTarget);
                    if (targetStat != null) {
                        targetStat.hp = (int) Math.min(targetStat.hp + healAmount, getMaxHp(healTarget));
                        if (healTarget == target) {
                            result.targetId = Mappers.base.get(healTarget).nameRegion;
                            result.damage = -healAmount; // Âm để biểu thị heal
                        }
                    }
                }
            }

            // Damage Reflection
            if (skill.effect.has("damageReflection")) {
                float reflection = skill.effect.getFloat("damageReflection", 0);
                a.hp = (int) Math.max(a.hp - reflection, 0);
                t.hp = (int) Math.max(t.hp - reflection, 0);
                result.damage += reflection;
                result.targetDead = t.hp <= 0;
            }
        }

        return result;
    }

    private Array<Entity> selectHealTargets(Entity actor, Array<Entity> playerTeam, Array<Entity> enemyTeam,
                                            SkillComponent skill) {
        boolean isEnemy = enemyTeam.contains(actor, true);
        Array<Entity> team = isEnemy ? enemyTeam : playerTeam;
        Array<Entity> targets = new Array<>();

        Array<Entity> sortedTeam = new Array<>(team);
        sortedTeam.sort((e1, e2) -> {
            StatComponent s1 = Mappers.stat.get(e1);
            StatComponent s2 = Mappers.stat.get(e2);
            if (s1 == null || s2 == null) return 0;
            return Float.compare(s1.hp, s2.hp);
        });

        int numTargets = skill.effect.getInt("targets", 1);
        for (Entity e : sortedTeam) {
            StatComponent s = Mappers.stat.get(e);
            if (s != null && s.hp > 0 && targets.size < numTargets) {
                targets.add(e);
            }
        }

        return targets;
    }

    private float getMaxHp(Entity entity) {
        StatComponent stat = Mappers.stat.get(entity);
        return stat != null ? stat.hp : 100; // Giá trị mặc định
    }
}
