package com.game.core;

import com.badlogic.ashley.core.ComponentMapper;
import com.game.ecs.component.CharacterBaseDataComponent;
import com.game.ecs.component.StatComponent;

public class Mappers {
    public static final ComponentMapper<CharacterBaseDataComponent> base = ComponentMapper.getFor(CharacterBaseDataComponent.class);
    public static final ComponentMapper<StatComponent> stat = ComponentMapper.getFor(StatComponent.class);

}
