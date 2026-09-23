package com.game.screens.start;

import static com.game.utils.Constants.CHARACTER_ATLAS;
import static com.game.utils.Constants.SKILL_SKILL;
import static com.game.utils.Constants.UI_POPUP;
import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.game.MainGame;
import com.game.models.entity.CharacterBase;
import com.game.utils.DataHelper;

import java.util.List;

/**
 * Tap trung logic preload asset cho NewPlayerScreen.
 * Tach khoi Screen (tang View). Logic giu nguyen hanh vi goc.
 */
public final class NewPlayerAssets {

    private NewPlayerAssets() {
    }

    public static void load() {
        List<CharacterBase> characterBaseList = DataHelper.loadCharacterBaseList();
        for (CharacterBase baseData : characterBaseList) {
            MainGame.getAsM().load(CHARACTER_ATLAS + baseData.nameRegion + ".atlas", TextureAtlas.class);
        }
        MainGame.getAsM().load(SKILL_SKILL, TextureAtlas.class);
        MainGame.getAsM().load(UI_WOOD, TextureAtlas.class);
        MainGame.getAsM().load(UI_POPUP, TextureAtlas.class);
    }
}
