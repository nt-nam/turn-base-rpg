package com.game.ui.widget;

import static com.game.utils.Constants.ATLAS_ITEM;
import static com.game.utils.Constants.BMF;
import static com.game.utils.Constants.PARTY_ATTACK;
import static com.game.utils.Constants.PARTY_FULL;
import static com.game.utils.Constants.SKILL_JSON;
import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.Constants.WAREHOUSE_JSON;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.game.MainGame;
import com.game.ecs.component.AnimationStateComponent;
import com.game.ecs.component.CharacterComponent;
import com.game.ecs.component.EnemyComponent;
import com.game.ecs.component.GridComponent;
import com.game.ecs.component.InfoComponent;
import com.game.ecs.component.LabelComponent;
import com.game.ecs.component.ListSkillComponent;
import com.game.ecs.component.PlayerComponent;
import com.game.ecs.component.PositionComponent;
import com.game.ecs.component.ProgressBarComponent;
import com.game.ecs.component.SizeComponent;
import com.game.ecs.component.SkillComponent;
import com.game.ecs.component.SpriteComponent;
import com.game.ecs.component.StatComponent;
import com.game.ecs.component.WarehouseComponent;
import com.game.ui.base.UIGroup;
import com.game.ui.base.UIImage;
import com.game.ui.base.UILabel;
import com.game.ui.base.UIProgressBar;
import com.game.ui.base.UITable;
import com.game.utils.data.GridData;
import com.game.utils.data.JsonLoader;

public class HerosPP {
    public static Group pp(float w, float h) {
        float size = w * 0.1f;
        float margin = size * 0.2f;
        Array<WarehouseComponent> warehouse = JsonLoader.loadArray(WAREHOUSE_JSON, WarehouseComponent.class, false);
        JsonValue gridJson = JsonLoader.getJsonValue(PARTY_ATTACK, true);

        UIGroup popup = new UIGroup().name("hero").size(w, h);

        TextureRegion profile = MainGame.getAsM().getRegion(UI_POPUP, "origin");
        new UIImage(profile).nine(profile, 30, 30, 30, 30)
            .name("origin")
            .parent(popup)
            .bounds(w * 0.01f, h * 0.05f, w * 0.38f, h * 0.9f);

        TextureRegion board = MainGame.getAsM().getRegion(UI_POPUP, "board");
        new UIImage(board).nine(board, 30, 30, 30, 30)
            .name("board")
            .parent(popup)
            .bounds(w * 0.4f, h * 0.05f, w * 0.6f, h * 0.9f);


        new UILabel("Team",BMF).pos(w*0.05f,h*0.8f).fontScale(2.5f).parent(popup);


        UITable grid = new UITable().name("grid").size(size * 3, size * 3).pos(w * 0.05f, h * 0.12f);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                UIGroup uiGroup;
                JsonValue characterJson = JsonLoader.getJsonValueByKey2(PARTY_ATTACK, "grid", i + "," + j);
                if (characterJson != null) {
                    JsonValue characterData = JsonLoader.getJsonValueByKey2(PARTY_FULL, "characterId", characterJson.get("characterId").asString());
                    System.out.println("cso " + characterJson);
                    uiGroup = new UIGroup().name(characterJson.get("characterId").asString()).child(
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                            .size(size, size),
                        new UIImage(MainGame.getAsM().getRegion("atlas/characters/" + characterData.get("characterBaseId").asString() + ".atlas", "idle"))
                            .size(size - margin, size - margin)
                            .pos(margin * 0.5f, margin * 0.5f)
                    ).onClick(() -> {
                        // Logic xử lý khi click vào item
                    });
                } else {
                    System.out.println("khong có: " + i + "," + j);
                    uiGroup = new UIGroup().name("empty").child(
                        new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                            .size(size, size));
                }


                grid.add(uiGroup).size(size, size);

            }
            grid.row();
        }
        popup.addActor(grid);


        UITable table = new UITable().name("table").size(size * 5, size * 3).pos(w * 0.43f, h * 0.12f);

        for (int i = 0; i < 15; i++) {
            UIGroup uiGroup;
            if (i < warehouse.size) {
                WarehouseComponent item = warehouse.get(i);
                uiGroup = new UIGroup().name(item.id).child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size),
                    new UIImage(MainGame.getAsM().getRegion(ATLAS_ITEM, item.id))
                        .size(size - margin, size - margin)
                        .pos(margin * 0.5f, margin * 0.5f)
                ).onClick(() -> {
                    // Logic xử lý khi click vào item
                });
            } else {
                uiGroup = new UIGroup().name("empty").child(
                    new UIImage(MainGame.getAsM().getRegion(UI_POPUP, "empty"))
                        .size(size, size));
            }


            table.add(uiGroup).size(size, size);

            if ((i + 1) % 5 == 0) {
                table.row();
            }
        }
        popup.addActor(table);

        return popup;
    }

}
