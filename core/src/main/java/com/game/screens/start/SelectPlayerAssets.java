package com.game.screens.start;

import static com.game.utils.Constants.UI_WOOD;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.game.MainGame;
import com.game.models.entity.Account;
import com.game.utils.DataHelper;

import java.util.List;

/**
 * Tap trung logic preload asset cho SelectPlayerScreen.
 * Tach khoi Screen (tang View). Logic giu nguyen hanh vi goc.
 *
 * @return danh sach account da load (de Screen dung lai neu can).
 */
public final class SelectPlayerAssets {

    private SelectPlayerAssets() {
    }

    public static List<Account> load() {
        List<Account> accounts = DataHelper.loadAccountList(true);
        if (accounts != null) {
            for (Account element : accounts) {
                MainGame.getAsM().load("atlas/characters/" + element.characterSelect + ".atlas", TextureAtlas.class);
            }
        }
        MainGame.getAsM().load(UI_WOOD, TextureAtlas.class);
        return accounts;
    }
}
