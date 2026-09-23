package com.game.screens.start;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.game.ecs.component.PlayerSelectedComponent;
import com.game.managers.GameSessionManager;
import com.game.models.entity.Account;
import com.game.utils.Constants;
import com.game.utils.DataHelper;

/**
 * Logic chon 1 account co san va nap toan bo du lieu nguoi choi (profile/equip/hero/mission...).
 * Tach khoi SelectPlayerScreen (tang View). Giu nguyen hanh vi goc.
 */
public final class SelectPlayerLoader {

    private SelectPlayerLoader() {
    }

    /**
     * Tao entity ECS PlayerSelected, set session va nap du lieu cho account duoc chon.
     *
     * @param engine  Ashley engine (de tao entity su kien chon)
     * @param account account nguoi choi da chon
     */
    public static void selectAccount(Engine engine, Account account) {
        Entity eventEntity = engine.createEntity();
        PlayerSelectedComponent comp = engine.createComponent(PlayerSelectedComponent.class);
        comp.playerName = account.id;
        comp.knightId = account.characterSelect;
        eventEntity.add(comp);
        engine.addEntity(eventEntity);

        GameSessionManager.getInstance().playerName = account.id;
        GameSessionManager.getInstance().selectedCharacterId = account.characterSelect;
        // playerPath() se tu dong dung GameSessionManager.getInstance().playerName
        Gdx.app.debug("SelectPlayerLoader", GameSessionManager.getInstance().playerName);
        DataHelper.loadProfile(true);
        DataHelper.loadEquipList(true);
        DataHelper.loadItemBaseList(true);
        DataHelper.loadHeroList(Constants.playerPath("hero_full.json"), true);
        DataHelper.loadMissionList(true);
        DataHelper.loadAchievementList(true);
    }
}
