package com.game.core;

import com.badlogic.ashley.core.ComponentMapper;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.GridComponent;
import com.game.ecs.component.ListSkillComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.StatComponent;

public class Mappers {
    public static final ComponentMapper<CharacterComponent> base = ComponentMapper.getFor(CharacterComponent.class);
    public static final ComponentMapper<StatComponent> stat = ComponentMapper.getFor(StatComponent.class);
    public static final ComponentMapper<GridComponent> grid = ComponentMapper.getFor(GridComponent.class);
    public static final ComponentMapper<ListSkillComponent> skills = ComponentMapper.getFor(ListSkillComponent.class);
    public static final ComponentMapper<SpriteComponent> sprite = ComponentMapper.getFor(SpriteComponent.class);
    public static final ComponentMapper<AnimationStateComponent> animation = ComponentMapper.getFor(AnimationStateComponent.class);
    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
}
