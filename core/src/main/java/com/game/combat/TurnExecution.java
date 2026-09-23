package com.game.combat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.ListSkillComponent;
import com.game.ecs.component.SkillComponent;
import com.game.ecs.component.StatComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.BuffComponent;

public class TurnExecution {
    public TurnExecution() {
    }

    public TurnResult executeTurn(Entity actor, Entity target) {
        // Lấy các component cần thiết
        CharacterComponent ca = actor.getComponent(CharacterComponent.class);
        StatComponent actorStats = actor.getComponent(StatComponent.class);
        ListSkillComponent listSkill = actor.getComponent(ListSkillComponent.class);

        // Kiểm tra tính hợp lệ của các component
        if (ca == null || actorStats == null || listSkill == null) {
            Gdx.app.error("TurnExecution", "Actor " + (ca != null ? ca.nameRegion : "null") +
                " thiếu component: ca=" + (ca != null) + ", stats=" + (actorStats != null) +
                ", skills=" + (listSkill != null));
            return null;
        }

        // Chọn kỹ năng
        SkillComponent selectedSkill = selectSkill(actorStats, listSkill);
        if (selectedSkill == null) {
            Gdx.app.error("TurnExecution", "Không tìm thấy kỹ năng hợp lệ cho actor " + ca.nameRegion);
            return null;
        }

        // Áp dụng kỹ năng và tạo TurnResult
        TurnResult turn = applySkill(actor, target, selectedSkill);
        turn.actorId = ca.nameRegion;
        CharacterComponent targetChar = target.getComponent(CharacterComponent.class);
        turn.targetId = targetChar != null ? targetChar.nameRegion : "unknown";
        turn.skillUsed = selectedSkill.name;
        turn.actorEntity = actor;
        turn.targetEntity = target;

        // Cập nhật MP cho actor
        int mpCost = getSkillCost(selectedSkill);
        updateMP(actorStats, -mpCost);

        // Cập nhật MP cho target
        StatComponent targetStats = target.getComponent(StatComponent.class);
        if (targetStats != null) {
            updateMP(targetStats, 5);
            if (turn.damage > 0) {
                targetStats.mp = Math.max(targetStats.mp - BattleConfig.getMpTakenDamageLoss(), 0);
            }
        }

        return turn;
    }

    private int getSkillCost(SkillComponent skill) {
        if (skill.mpCost > 0) return skill.mpCost;
        if (skill.id == 2) return BattleConfig.getMpSkill2Cost();
        if (skill.id == 3) return BattleConfig.getMpSkill3Cost();
        return 0;
    }

    private SkillComponent selectSkill(StatComponent actorStats, ListSkillComponent listSkill) {
        SkillComponent bestSkill = null;
        for (SkillComponent skill : listSkill.skills) {
            if (actorStats.mp >= getSkillCost(skill)) {
                if (bestSkill == null || skill.id > bestSkill.id) {
                    bestSkill = skill;
                }
            }
        }
        
        if (bestSkill != null) {
            return bestSkill;
        }

        Gdx.app.error("TurnExecution", "Không tìm thấy kỹ năng hợp lệ");
        return null;
    }

    private void updateMP(StatComponent stats, int mpChange) {
        stats.mp += mpChange;
        stats.mp = Math.min(stats.mp, stats.maxMp);
        stats.mp = Math.max(stats.mp, 0);
    }

    public TurnResult applySkill(Entity actor, Entity target, SkillComponent skill) {
        // Lấy TurnResult từ pool để tái sử dụng
        TurnResult result = TurnResultPool.getInstance().obtain();
        // Lấy các component cần thiết
        StatComponent actorStats = actor.getComponent(StatComponent.class);
        StatComponent targetStats = target.getComponent(StatComponent.class);
        CharacterComponent ca = actor.getComponent(CharacterComponent.class);
        CharacterComponent ct = target.getComponent(CharacterComponent.class);

        // Kiểm tra tính hợp lệ của các component
        if (actorStats == null || targetStats == null || ca == null || ct == null) {
            Gdx.app.error("TurnExecution", "Thiếu component: actorStats=" + (actorStats != null) +
                ", targetStats=" + (targetStats != null) + ", ca=" + (ca != null) + ", ct=" + (ct != null));
            return result;
        }

        // Tính hệ số nhân sát thương nếu có khắc chế
        float multiplier = (ca.counters != null && ct.classType != null && ca.counters.contains(ct.classType, false))
            ? BattleConfig.getCounterMultiplier() : 1f;

        // Tính xác suất chí mạng dựa trên critRate
        float critChance = actorStats.critRate;
        boolean isCritical = MathUtils.random() < critChance;
        float critMultiplier = isCritical ? actorStats.critDamage : 1f;

        // Tính xác suất né tránh (kiểm tra sau khi tính sát thương)
        float avoidChance = (float) targetStats.agi / (targetStats.agi + actorStats.agi + 1) * 0.05f;
        boolean isAvoided = !isCritical && MathUtils.random() < avoidChance;

        // Chuỗi mô tả hiệu ứng kỹ năng
        StringBuilder effectDesc = new StringBuilder();

        // Xử lý các hiệu ứng của kỹ năng
        if (skill.effect != null) {
            // Duyệt qua các trường trong skill.effect
            for (JsonValue effect : skill.effect) {
                String effectName = effect.name;
                float effectValue = effect.asFloat();
                EffectType type = EffectType.fromString(effectName);

                switch (type) {
                    case DAMAGE:
                        // Tính sát thương: effectValue + actorStats.atk + 5
                        int baseDamage = (int) (effectValue + actorStats.atk + 5);
                        int attackTimes = Math.max(skill.effect.getInt(EffectType.ATTACK_TIMES.getKey(), 1), 1);
                        for (int i = 0; i < attackTimes; i++) {
                            // Nhân với counter và critical
                            int damageBeforeDef = (int) (baseDamage * multiplier * critMultiplier);
                            // Trừ def của target
                            int singleDamage = Math.max(damageBeforeDef - targetStats.def, 15); // Đảm bảo sát thương tối thiểu
                            singleDamage = isAvoided ? 0 : singleDamage;
                            targetStats.hp = Math.max(targetStats.hp - singleDamage, 0);
                            result.damage += singleDamage;
                            Gdx.app.log("TurnExecution", "Tấn công " + (i + 1) + ": baseDamage=" + baseDamage +
                                ", damageBeforeDef=" + damageBeforeDef + ", singleDamage=" + singleDamage +
                                ", target HP=" + targetStats.hp + ", isAvoided=" + isAvoided);
                        }
                        result.isCritical = isCritical;
                        result.isMiss = isAvoided;
                        result.targetDead = targetStats.hp <= 0;
                        effectDesc.append(isAvoided ? "Tấn công bị né! " : "Gây " + result.damage + " sát thương. ");
                        if (isCritical) effectDesc.append("Chí mạng! ");
                        if (result.targetDead) effectDesc.append("Mục tiêu bị hạ gục. ");
                        break;

                    case ARMOR:
                        BuffComponent buffArmor = actor.getComponent(BuffComponent.class);
                        if (buffArmor == null) {
                            buffArmor = new BuffComponent();
                            actor.add(buffArmor);
                        }
                        int durationArmor = skill.effect.getInt("duration", 3);
                        buffArmor.addBuff(EffectType.ARMOR, effectValue, durationArmor, actorStats);
                        result.state.put(EffectType.ARMOR.getKey(), effectValue);
                        effectDesc.append("Tăng " + effectValue + " giáp (" + durationArmor + " lượt). ");
                        break;

                    case DODGE_CHANCE:
                        BuffComponent buffDodge = actor.getComponent(BuffComponent.class);
                        if (buffDodge == null) {
                            buffDodge = new BuffComponent();
                            actor.add(buffDodge);
                        }
                        int durationDodge = skill.effect.getInt("duration", 3);
                        buffDodge.addBuff(EffectType.DODGE_CHANCE, effectValue, durationDodge, actorStats);
                        result.state.put(EffectType.DODGE_CHANCE.getKey(), effectValue);
                        effectDesc.append("Tăng " + effectValue + " khả năng né tránh (" + durationDodge + " lượt). ");
                        break;

                    case CRIT_CHANCE:
                        BuffComponent buffCrit = actor.getComponent(BuffComponent.class);
                        if (buffCrit == null) {
                            buffCrit = new BuffComponent();
                            actor.add(buffCrit);
                        }
                        int durationCrit = skill.effect.getInt("duration", 3);
                        buffCrit.addBuff(EffectType.CRIT_CHANCE, effectValue, durationCrit, actorStats);
                        result.state.put(EffectType.CRIT_CHANCE.getKey(), effectValue);
                        effectDesc.append("Tăng " + effectValue + " tỷ lệ chí mạng (" + durationCrit + " lượt). ");
                        break;

                    case MANA_REGEN:
                        // Hồi MP cho actor
                        actorStats.mp = (int) Math.min(actorStats.mp + effectValue, actorStats.maxMp);
                        result.state.put(EffectType.MANA_REGEN.getKey(), effectValue);
                        effectDesc.append("Hồi " + effectValue + " MP. ");
                        break;

                    case HEAL:
                        // Hồi máu cho các mục tiêu
                        int numTargets = skill.effect.getInt(EffectType.TARGETS.getKey(), 1);
                        Array<Entity> targets = selectHealTargets(actor, skill, numTargets);
                        for (Entity healTarget : targets) {
                            StatComponent healStats = healTarget.getComponent(StatComponent.class);
                            CharacterComponent healChar = healTarget.getComponent(CharacterComponent.class);
                            if (healStats != null && healChar != null) {
                                healStats.hp = (int) Math.min(healStats.hp + effectValue, healStats.maxHp);
                                if (healTarget == target || targets.size == 1) {
                                    result.targetId = healChar.nameRegion;
                                    result.damage = (int) -effectValue; // Số âm cho hồi máu
                                }
                                effectDesc.append("Hồi " + effectValue + " HP cho " + healChar.nameRegion + ". ");
                            }
                        }
                        break;

                    case DAMAGE_REFLECTION:
                        // Phản sát thương cho cả actor và target
                        actorStats.hp = (int) Math.max(actorStats.hp - effectValue, 0);
                        targetStats.hp = (int) Math.max(targetStats.hp - effectValue, 0);
                        result.damage += effectValue;
                        result.targetDead = targetStats.hp <= 0;
                        result.state.put(EffectType.DAMAGE_REFLECTION.getKey(), effectValue);
                        effectDesc.append("Phản " + effectValue + " sát thương. ");
                        break;

                    case ATTACK_TIMES:
                    case TARGETS:
                        // Các thông số bổ sung, không cần xử lý hiệu ứng độc lập
                        break;

                    case UNKNOWN:
                    default:
                        // Ghi log và lưu các hiệu ứng không ánh xạ tới chỉ số
                        Gdx.app.log("TurnExecution", "Hiệu ứng không xác định: " + effectName + " với giá trị " + effectValue);
                        result.state.put(effectName, effectValue);
                        effectDesc.append("Hiệu ứng " + effectName + ": " + effectValue + ". ");
                        break;
                }
            }
        } else {
            // Fallback: Nếu kỹ năng không có effect, xử lý như đánh thường với 5 sát thương
            int baseDamage = (int) (actorStats.atk + 5);
            int singleDamage = (int) (baseDamage * multiplier * critMultiplier);
            singleDamage = Math.max(singleDamage - targetStats.def, 15);
            singleDamage = isAvoided ? 0 : singleDamage;
            targetStats.hp = Math.max(targetStats.hp - singleDamage, 0);
            result.damage += singleDamage;
            result.isCritical = isCritical;
            result.isMiss = isAvoided;
            result.targetDead = targetStats.hp <= 0;
            effectDesc.append(isAvoided ? "Tấn công bị né! " : "Gây " + result.damage + " sát thương. ");
            if (isCritical) effectDesc.append("Chí mạng! ");
            if (result.targetDead) effectDesc.append("Mục tiêu bị hạ gục. ");
            Gdx.app.log("TurnExecution", "Kỹ năng không có effect, xử lý đánh thường: singleDamage=" + singleDamage +
                ", target HP=" + targetStats.hp);
        }

        result.effectDescription = effectDesc.toString();

        // Ghi log kết quả lượt đi
        Gdx.app.log("TurnExecution", "Hoàn thành lượt: damage=" + result.damage + ", targetDead=" + result.targetDead);
        result.damage = (int) MathUtils.random(result.damage * 0.9f, result.damage * 1.1f);
        return result;
    }

    private Array<Entity> selectHealTargets(Entity actor, SkillComponent skill, int numTargets) {
        Array<Entity> targets = new Array<>();
        boolean isPlayer = actor.getComponent(PlayerComponent.class) != null;
        Array<Entity> team = isPlayer ? BattleSimulator.getPlayerTeam() : BattleSimulator.getEnemyTeam();

        // Sắp xếp team theo HP (thấp đến cao) để ưu tiên hồi máu cho nhân vật yếu nhất
        team.sort((a, b) -> {
            StatComponent sa = a.getComponent(StatComponent.class);
            StatComponent sb = b.getComponent(StatComponent.class);
            return Integer.compare(sa.hp, sb.hp);
        });

        // Chọn tối đa numTargets nhân vật còn sống
        for (Entity e : team) {
            StatComponent stats = e.getComponent(StatComponent.class);
            if (stats != null && stats.hp > 0 && targets.size < numTargets) {
                targets.add(e);
            }
        }

        // Nếu không có mục tiêu hợp lệ, hồi máu cho actor
        if (targets.isEmpty()) {
            StatComponent actorStats = actor.getComponent(StatComponent.class);
            if (actorStats != null && actorStats.hp > 0) {
                targets.add(actor);
            }
        }

        return targets;
    }
}
