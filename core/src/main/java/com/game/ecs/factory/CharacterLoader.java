// KnightLoader.java (tạo entity từ JSON)
package com.game.ecs.factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.game.ecs.component.CharacterBaseDataComponent;
import com.game.utils.data.CharacterBaseData;

public class CharacterLoader {
    private static Array<CharacterBaseData> characterList;

    // Load tất cả character vào ECS engine, cho mọi loại (knight, npc, enemy, ...)
    public static void loadAllCharacters(Engine engine) {
        Json json = new Json();
        characterList = json.fromJson(Array.class, CharacterBaseData.class, Gdx.files.internal("data/character_base.json"));
        for (CharacterBaseData c : characterList) {
            Entity e = engine.createEntity();
            e.add(CharacterBaseDataComponent.from(c));
            engine.addEntity(e);
        }
    }

    // Lấy data theo id
    public static CharacterBaseData getCharacterBaseData(String characterId) {
        if (characterList == null) return null;
        for (CharacterBaseData c : characterList)
            if (c.characterId.equals(characterId)) return c;
        return null;
    }

    // Lấy list theo type (knight/npc/enemy/...)
    public static Array<CharacterBaseData> getCharactersByType(String type) {
        Array<CharacterBaseData> result = new Array<>();
        if (characterList == null) return result;
        for (CharacterBaseData c : characterList)
            if (type.equals(c.classType)) result.add(c);
        return result;
    }
}
