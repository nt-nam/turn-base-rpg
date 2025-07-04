package com.game.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.game.MainGame;
import com.game.ecs.component.*;

public class PartySetupSystem extends EntitySystem {
    private final Engine engine;
    private final float tileSize;
    private final Array<Entity> playerTeam = new Array<>();
    private final Array<Entity> enemyTeam = new Array<>();
    private boolean initialized = false;

    public PartySetupSystem(Engine engine, float tileSize) {
        this.engine = engine;
        this.tileSize = tileSize;
    }

    public Array<Entity> getPlayerTeam() {
        return playerTeam;
    }

    public Array<Entity> getEnemyTeam() {
        return enemyTeam;
    }

    @Override
    public void update(float deltaTime) {
        if (!initialized) {
            initializeTeams();
            initialized = true;
        }
    }

    private void initializeTeams() {
        JsonReader jsonReader = new JsonReader();
        Json json = new Json();
        JsonValue playerPartyData = jsonReader.parse(Gdx.files.internal("full_party.json"));
        JsonValue enemyPartyData = jsonReader.parse(Gdx.files.internal("enemy_party.json"));
        JsonValue charDataArray = jsonReader.parse(MainGame.getAsM().get("character_base.json", String.class));
        JsonValue equipData = jsonReader.parse(Gdx.files.internal("equip_data.json"));

        // Chuyển mảng character_base.json thành object để truy xuất nhanh
        JsonValue charData = new JsonValue(JsonValue.ValueType.object);
        for (JsonValue entry : charDataArray) {
            String charId = entry.getString("characterId");
            charData.addChild(charId, entry);
        }

        // Xử lý đội player
        processTeam(playerPartyData, charData, equipData, playerTeam, true);

        // Xử lý đội enemy
        processTeam(enemyPartyData, charData, equipData, enemyTeam, false);
    }

    private void processTeam(JsonValue partyData, JsonValue charData, JsonValue equipData, Array<Entity> team, boolean isPlayer) {
        Json json = new Json();
        for (JsonValue.JsonIterator iterator = partyData.iterator(); iterator.hasNext(); ) {
            JsonValue entry = iterator.next();
            String charId = entry.name();
            JsonValue charInfo = charData.get(charId);

            if (charInfo == null) {
                Gdx.app.error("PartySetupSystem", "Không tìm thấy ID nhân vật: " + charId);
                continue;
            }

            // Deserialize JSON thành CharacterBaseData
            CharacterComponent baseData = json.fromJson(CharacterComponent.class, charInfo.toJson(JsonWriter.OutputType.json));

            int gridX = entry.getInt("gridX");
            int gridY = entry.getInt("gridY");
            if (gridX >= 0 && gridY >= 0 && gridX < 3 && gridY < 3) {
                Entity entity = engine.createEntity();

                // GridPositionComponent
                entity.add(new GridComponent(gridX, gridY));

                // PositionComponent
                float worldX = gridX * tileSize;
                float worldY = gridY * tileSize;
                entity.add(new PositionComponent(worldX, worldY));

                // SpriteComponent
                String spriteId = baseData.characterBaseId; // Giả sử characterId là spriteId
                entity.add(new SpriteComponent(spriteId, "idle", !isPlayer));

                // SizeComponent
                float size = tileSize * 0.9f;
                entity.add(new SizeComponent(size, size));

                // StatComponent
                float baseHp = baseData.hp;
                float baseMp = baseData.mp;
                float baseAtk = baseData.atk;
                float baseDef = baseData.def;
                float baseAgi = baseData.agi;
                float baseCrit = baseData.crit;

                // Tính chỉ số theo level
                int level = entry.getInt("level");
                float multiplier = (float) Math.pow(1.1, level - 1);
                int hp = (int)(baseHp * multiplier);
                int mp = (int)(baseMp * multiplier);
                int atk = (int)(baseAtk * multiplier);
                int def = (int)(baseDef * multiplier);
                int agi = (int)(baseAgi * multiplier);
                int crit = (int)(baseCrit * multiplier);

                // Cộng chỉ số từ trang bị
                JsonValue equip = entry.get("equip");
                for (String equipSlot : new String[]{"weapon", "armor", "jewelry", "support"}) {
                    String equipId = equip.getString(equipSlot);
                    JsonValue equipStats = equipData.get(equipId);
                    hp += equipStats.getInt("hp", 0);
                    mp += equipStats.getInt("mp", 0);
                    atk += equipStats.getInt("atk", 0);
                    def += equipStats.getInt("def", 0);
                    agi += equipStats.getInt("agi", 0);
                    crit += equipStats.getInt("crit", 0);
                }

                entity.add(new StatComponent(hp, mp, atk, def, agi, crit));

                // BattleCharacterComponent
                entity.add(new BattleCharacterComponent(isPlayer, baseData.skills));

                // CharacterBaseDataComponent
                entity.add(baseData);

                // AnimationStateComponent
                entity.add(new AnimationStateComponent());

                // SpriteDebugComponent
                entity.add(new SpriteDebugComponent());

                team.add(entity);
                engine.addEntity(entity);
            }
        }
    }
}
