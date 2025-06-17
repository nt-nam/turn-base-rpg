// KnightLoader.java (tạo entity từ JSON)
package com.game.ecs.factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.game.ecs.component.CharacterBaseDataComponent;
import com.game.utils.data.KnightBaseData;

public class KnightLoader {
    public static void loadAllKnights(Engine engine) {
        Json json = new Json();
        Array<KnightBaseData> knightList = json.fromJson(Array.class, KnightBaseData.class, Gdx.files.internal("data/knight_base.json"));
        for (KnightBaseData k : knightList) {
            Entity e = engine.createEntity();
            CharacterBaseDataComponent c = new CharacterBaseDataComponent();
            c.knightId = k.knightId;
            c.name = k.name;
            c.hp = k.hp;
            c.mp = k.mp;
            c.atk = k.atk;
            c.def = k.def;
            c.agi = k.agi;
            c.crit = k.crit;
            c.desc = k.desc;
            // c.skills = k.skills;
            e.add(c);
            engine.addEntity(e);
        }
    }
}
