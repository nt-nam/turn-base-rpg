package com.game.controllers;

import com.game.managers.GameSessionManager;
import com.game.models.entity.Equip;
import com.game.models.entity.Hero;
import com.game.models.entity.Item;
import com.game.models.entity.MapBattle;
import com.game.models.entity.Reward;
import com.game.screens.battle.BattleScreen;
import com.game.ui.views.BattleHudView;
import com.game.utils.Constants;
import com.game.utils.JsonSaver;

public class BattleController {
    private BattleScreen screen;
    private BattleHudView hudView;
    private MapBattle mapBattle;
    private int maxLevelEnemy;

    public BattleController(BattleScreen screen, BattleHudView hudView, MapBattle mapBattle, int maxLevelEnemy) {
        this.screen = screen;
        this.hudView = hudView;
        this.mapBattle = mapBattle;
        this.maxLevelEnemy = maxLevelEnemy;
    }

    public void onBattleWin() {
        if (mapBattle.rewardList != null) {
            for (Reward reward : mapBattle.rewardList) {
                if ("coin".equals(reward.type)) GameSessionManager.getInstance().profile.addCoin(reward.quantity);
                if ("gem".equals(reward.type)) GameSessionManager.getInstance().profile.gem += reward.quantity;
                if ("item".equals(reward.type)) {
                    GameSessionManager.getInstance().itemList.add(new Item(reward.nameRegion, reward.quantity));
                }
                if ("equip".equals(reward.type)) {
                    GameSessionManager.getInstance().equipList.add(new Equip(reward.nameRegion));
                }
                if ("hero".equals(reward.type)) {
                    //add Hero Placeholder
                }
            }
        }

        plusEXP(0.35f);
        GameSessionManager.getInstance().profile.numberOfEnemies++;
        
        if ("village_0".equals(GameSessionManager.getInstance().targetMapId)) {
            if (GameSessionManager.getInstance().missionList != null && !GameSessionManager.getInstance().missionList.isEmpty()) {
                 GameSessionManager.getInstance().missionList.get(0).progress = 1;
                 JsonSaver.saveObject(Constants.playerPath("mission.json"), GameSessionManager.getInstance().missionList);
            }
        }
        
        if (hudView != null) {
            hudView.showPopupWin(mapBattle.rewardList);
        }
    }

    public void onBattleFail() {
        plusEXP(0.15f);
        if (hudView != null) {
            hudView.showPopupFail();
        }
    }

    private void plusEXP(float per) {
        GameSessionManager session = GameSessionManager.getInstance();
        for (Hero he : session.heroList) {
            if (!he.grid.equals("empty")) {
                System.out.println(he.grid + " received exp");
                he.exp += (int) ((maxLevelEnemy * 100) * per);
                he.checkLevel();
            }
        }
        session.profile.exp += (int) ((maxLevelEnemy * 100) * per);
        
        if (session.profile.exp >= session.profile.level * 100) {
            session.profile.exp -= session.profile.level * 100;
            session.profile.level++;
        }
        
        if(session.achievementList != null && session.achievementList.size() > 2) {
            session.achievementList.get(2).number++;
            JsonSaver.saveObject(Constants.playerPath("achievement.json"), session.achievementList);
        }
        
        JsonSaver.saveObject(Constants.playerPath("hero_full.json"), session.heroList);
        JsonSaver.saveObject(Constants.playerPath("info.json"), session.profile);
    }
}
