package com.game.ecs.factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.game.MainGame;
import com.game.combat.StatCalculator;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.EnemyComponent;
import com.game.ecs.component.GridComponent;
import com.game.ecs.component.InfoComponent;
import com.game.ecs.component.LabelComponent;
import com.game.ecs.component.ListSkillComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.HealthBarComponent;
import com.game.ecs.component.SizeComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.StatComponent;
import com.game.managers.GameSessionManager;
import com.game.models.entity.CharacterBase;
import com.game.models.entity.Hero;
import com.game.models.entity.Lineup;
import com.game.models.entity.MapBattle;
import com.game.models.entity.skill.SkillBase;
import com.game.ui.base.UIImage;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.utils.Constants;
import com.game.utils.DataHelper;
import com.game.utils.data.AnimationCache;

import java.util.List;

public class EntityFactory {
    private Engine engine;
    private List<SkillBase> skillBaseList;
    private List<CharacterBase> characterBaseList;

    public EntityFactory(Engine engine, List<SkillBase> skillBaseList, List<CharacterBase> characterBaseList) {
        this.engine = engine;
        this.skillBaseList = skillBaseList;
        this.characterBaseList = characterBaseList;
    }

    public void loadAllAnimations(String characterId, String atlasPath) {
        TextureAtlas atlas = MainGame.getAsM().get(atlasPath, TextureAtlas.class);
        AnimationCache.put(characterId, "idle", new Animation<>(0.1f, atlas.findRegions("idle"), Animation.PlayMode.LOOP));
        AnimationCache.put(characterId, "attack", new Animation<>(0.1f, atlas.findRegions("attack"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "jump", new Animation<>(0.1f, atlas.findRegions("jump"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "run", new Animation<>(0.1f, atlas.findRegions("run"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "die", new Animation<>(0.1f, atlas.findRegions("die"), Animation.PlayMode.NORMAL));
        AnimationCache.put(characterId, "hurt", new Animation<>(0.1f, atlas.findRegions("hurt"), Animation.PlayMode.NORMAL));
    }


    public void createPlayerTeam(float startX, float startY, float tileSize, Group rootGroup, Array<Entity> team) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float posX = startX + i * tileSize * 1.1f;
                float posY = startY + j * tileSize * 1.1f;
                new UIImage(Constants.UI_POPUP, "empty").parent(rootGroup).bounds(posX, posY, tileSize, tileSize);

                Lineup lineup = DataHelper.get(DataHelper.loadLineupList(true), "grid", i + "," + j);

                if (lineup == null || lineup.characterId == null) {
                    continue;
                }

                Hero hero = DataHelper.get(DataHelper.loadHeroList(Constants.playerPath("hero_full.json"), false), "characterId", lineup.characterId);
                createCharacterEntity(hero, lineup.nameRegion, posX, posY, tileSize, i, j, false, false, rootGroup, team);
            }
        }
    }

    public int createEnemyTeam(float startX, float startY, float tileSize, Group rootGroup, Array<Entity> team, String path, MapBattle mapBattle) {
        int maxLevelEnemy = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float posX = startX + i * tileSize * 1.1f;
                float posY = startY + j * tileSize * 1.1f;
                float posBossX = startX;
                float posBossY = startY;

                new UIImage(Constants.UI_POPUP, "empty").parent(rootGroup).bounds(posX, posY, tileSize, tileSize);
                if (path == null) continue;

                Hero hero = DataHelper.get(mapBattle.heroEnemyList, "grid", i + "," + j);
                if (hero == null || hero.characterId == null) {
                    continue;
                }

                maxLevelEnemy = Math.max(maxLevelEnemy, hero.level);
                boolean isBoss = hero.characterId.equals("boss");
                float actualX = isBoss ? posBossX : posX;
                float actualY = isBoss ? posBossY : posY;

                createCharacterEntity(hero, hero.nameRegion, actualX, actualY, tileSize, i, j, true, isBoss, rootGroup, team);
            }
        }
        return maxLevelEnemy;
    }

    private void createCharacterEntity(Hero hero, String nameRegion, float posX, float posY, float tileSize, int gridX, int gridY, boolean isEnemy, boolean isBoss, Group rootGroup, Array<Entity> team) {
        InfoComponent infoCharacter = new InfoComponent();
        infoCharacter.characterId = hero.characterId;
        infoCharacter.nameRegion = nameRegion;
        infoCharacter.level = hero.level;
        infoCharacter.star = hero.star;
        infoCharacter.equip = new InfoComponent.Equipment();

        CharacterComponent dataEntity = new CharacterComponent(DataHelper.get(characterBaseList, "nameRegion", infoCharacter.nameRegion));

        loadAllAnimations(dataEntity.nameRegion, Constants.CHARACTER_ATLAS + dataEntity.nameRegion + ".atlas");
        Entity entity = engine.createEntity();

        float sizeX = isBoss ? tileSize * 2.5f : tileSize;
        float sizeY = isBoss ? tileSize * 2.5f : tileSize;
        
        float entityPosX = isBoss ? posX + tileSize * 0.5f : posX;
        float entityPosY = isBoss ? posY + tileSize * 0.5f : posY + tileSize * 0.3f;

        entity.add(new PositionComponent(entityPosX, entityPosY));
        entity.add(new GridComponent(gridX, gridY));
        entity.add(new SpriteComponent(dataEntity.nameRegion, "idle", isEnemy));
        entity.add(isEnemy ? new EnemyComponent() : new PlayerComponent());
        entity.add(new SizeComponent(sizeX, sizeY));
        entity.add(infoCharacter);

        StatComponent stat = StatCalculator.calculate(dataEntity, infoCharacter.level, infoCharacter.star);
        entity.add(stat);

        ListSkillComponent listSkill = new ListSkillComponent(DataHelper.get(skillBaseList, "name", dataEntity.classType.toLowerCase()));
        entity.add(listSkill);

        entity.add(dataEntity);
        entity.add(new AnimationStateComponent());

        float barX = isBoss ? posX + 0.5f * tileSize : posX;
        float barY = isBoss ? posY - tileSize * 0.2f : posY;
        float barW = isBoss ? tileSize * 2 : tileSize * 1f;
        float barH = isBoss ? tileSize * 0.2f : tileSize * 0.1f;

        entity.add(new HealthBarComponent(1.0f, barX, barY, barW, barH));
        entity.add(new LabelComponent());
        
        team.add(entity);
        engine.addEntity(entity);
    }
}
