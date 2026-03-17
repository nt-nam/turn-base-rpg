package com.game.ui.hud;

import static com.game.utils.Constants.SKILL_SKILL;
import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.game.MainGame;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.SkillStateComponent;
import com.game.ui.base.UIButton;
import com.game.managers.GameSessionManager;

public class BattleButtonInput {
    private Entity player;
    private Entity enemy;
    private Entity skill;
    private AnimationStateComponent stateAnim;
    private SkillStateComponent stateSkill;
    private boolean isPause;
    private final Group rootGroup;

    public BattleButtonInput(Entity player, Entity enemy, Group rootGroup) {
        this.player = player;
        this.enemy = enemy;
        this.rootGroup = rootGroup;
        stateAnim = new AnimationStateComponent();
        stateSkill = skill.getComponent(SkillStateComponent.class);
        createBtn();
    }

    private void createBtn() {
        TextureRegion up = MainGame.getAsM().getRegion(UI_WOOD, "btn_up");
        TextureRegion down = MainGame.getAsM().getRegion(UI_WOOD, "btn_down");
        TextureRegion skill1 = MainGame.getAsM().getRegion(SKILL_SKILL, GameSessionManager.getInstance().skillCharacter + "_attack");
        UIButton btnSkill1 = new UIButton(skill1, up, down)
            .size(rootGroup.getWidth() * 0.1f, rootGroup.getWidth() * 0.1f)
            .pos(rootGroup.getWidth() * 0.05f, rootGroup.getHeight() * 0.05f)
            .onClick(() -> {
            });
        rootGroup.addActor(btnSkill1);

        TextureRegion upLeft = new TextureRegion(up);
        TextureRegion downLeft = new TextureRegion(down);
        TextureRegion skill2 = MainGame.getAsM().getRegion(SKILL_SKILL, GameSessionManager.getInstance().skillCharacter + "_attack_big");
        UIButton btnSkill2 = new UIButton(skill2, upLeft, downLeft)
            .size(rootGroup.getWidth() * 0.1f, rootGroup.getWidth() * 0.1f)
            .pos(rootGroup.getWidth() * 0.15f, rootGroup.getHeight() * 0.05f)
            .onClick(() -> {
            });
        rootGroup.addActor(btnSkill2);

        TextureRegion upTop = new TextureRegion(up);
        TextureRegion downTop = new TextureRegion(down);
        TextureRegion skill3 = MainGame.getAsM().getRegion(SKILL_SKILL, GameSessionManager.getInstance().skillCharacter + "_heal");
        UIButton btnSkill3 = new UIButton(skill3, upTop, downTop)
            .size(rootGroup.getWidth() * 0.1f, rootGroup.getWidth() * 0.1f)
            .pos(rootGroup.getWidth() * 0.25f, rootGroup.getHeight() * 0.05f)
            .onClick(() -> {
            });
        rootGroup.addActor(btnSkill3);

        TextureRegion upBottom = new TextureRegion(up);
        TextureRegion downBottom = new TextureRegion(down);
        TextureRegion skill4 = MainGame.getAsM().getRegion(SKILL_SKILL, GameSessionManager.getInstance().skillCharacter + "_ultimate");
        UIButton btnSkill4 = new UIButton(skill4, upBottom, downBottom)
            .size(rootGroup.getWidth() * 0.1f, rootGroup.getWidth() * 0.1f)
            .pos(rootGroup.getWidth() * 0.35f, rootGroup.getHeight() * 0.05f)
            .onClick(() -> {
            });
        rootGroup.addActor(btnSkill4);
    }

    private void battle(Entity offensive, Entity defensive, SkillStateComponent.State type, int hp) {
        switch (type) {
            case ATTACK:
                stateAnim = offensive.getComponent(AnimationStateComponent.class);
                stateAnim.current = AnimationStateComponent.State.ATTACK;
                stateAnim = defensive.getComponent(AnimationStateComponent.class);
                stateAnim.current = AnimationStateComponent.State.HURT;
                stateSkill = skill.getComponent(SkillStateComponent.class);
                stateSkill.current = SkillStateComponent.State.ATTACK;
//                ((UIProgressBar) rootGroup.findActor("progressBarEnemies")).update(hp);
                break;
            case ATTACK_BIG:
                stateAnim = offensive.getComponent(AnimationStateComponent.class);
                stateAnim.current = AnimationStateComponent.State.ATTACK;
                stateAnim = defensive.getComponent(AnimationStateComponent.class);
                stateAnim.current = AnimationStateComponent.State.HURT;
                stateSkill = skill.getComponent(SkillStateComponent.class);
                stateSkill.current = SkillStateComponent.State.ATTACK_BIG;
//                ((UIProgressBar) rootGroup.findActor("progressBarEnemies")).update(hp);
                break;
            case HEAL:
                stateAnim = offensive.getComponent(AnimationStateComponent.class);
                stateAnim.current = AnimationStateComponent.State.JUMP;
                stateAnim = defensive.getComponent(AnimationStateComponent.class);
                stateAnim.current = AnimationStateComponent.State.HURT;
                stateSkill = skill.getComponent(SkillStateComponent.class);
                stateSkill.current = SkillStateComponent.State.HEAL;
//                ((UIProgressBar) rootGroup.findActor("progressBarPlayer")).update(hp);
                break;
            case ULTIMATE:
                stateAnim = offensive.getComponent(AnimationStateComponent.class);
                stateAnim.current = AnimationStateComponent.State.ATTACK;
                stateAnim = defensive.getComponent(AnimationStateComponent.class);
                stateAnim.current = AnimationStateComponent.State.HURT;
                stateSkill = skill.getComponent(SkillStateComponent.class);
                stateSkill.current = SkillStateComponent.State.ULTIMATE;
//                ((UIProgressBar) rootGroup.findActor("progressBarEnemies")).update(hp);
                break;

        }
    }
}
