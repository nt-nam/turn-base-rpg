package com.game.screens.start;

import static com.game.utils.Constants.MAININFO_JSON_LOCAL;

import com.game.managers.GameSessionManager;
import com.game.models.entity.Account;
import com.game.models.entity.Lineup;
import com.game.models.entity.Profile;
import com.game.utils.Constants;
import com.game.utils.DataHelper;
import com.game.utils.JsonSaver;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Logic tao & luu tru tai khoan nguoi choi moi (persistence), tach khoi NewPlayerScreen (tang View).
 * Giu nguyen hanh vi goc: luu lineup.json, hero_full.json, info.json, maininfo, tao account.
 */
public final class NewPlayerAccountCreator {

    private NewPlayerAccountCreator() {
    }

    /**
     * Tao profile + luu toan bo du lieu cho nguoi choi moi.
     *
     * @param playerName ten nguoi choi
     * @param knightId   nameRegion cua nhan vat da chon
     */
    public static void create(String playerName, String knightId) {
        GameSessionManager.getInstance().playerName = playerName;
        GameSessionManager.getInstance().selectedCharacterId = knightId;

        Profile profile = new Profile(playerName, knightId);
        GameSessionManager.getInstance().profile = profile;

        List<Lineup> lineups = new ArrayList<>();
        Lineup g = new Lineup();
        g.grid = "1,1";
        g.characterId = "character0";
        g.nameRegion = knightId;
        lineups.add(g);
        JsonSaver.saveObject(Constants.playerPath("lineup.json"), lineups);

        createFullParty(knightId);

        List<Account> accounts = DataHelper.loadAccountList(true);
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        Account a = new Account();
        a.id = playerName;
        a.level = 1;
        a.characterSelect = knightId;
        accounts.add(a);

        JsonSaver.saveObject("data/select/" + playerName + "/info.json", GameSessionManager.getInstance().profile);
        JsonSaver.saveObject(MAININFO_JSON_LOCAL, accounts);
        JsonSaver.createAccount();
    }

    private static void createFullParty(String knightId) {
        List<JsonObject> jsonObjectList = new ArrayList<>();
        JsonObject equip = new JsonObject();
        equip.addProperty("weapon", "empty");
        equip.addProperty("armor", "empty");
        equip.addProperty("jewelry", "empty");
        equip.addProperty("support", "empty");

        JsonObject character = new JsonObject();
        character.addProperty("characterId", "character0");
        character.addProperty("nameRegion", knightId);
        character.addProperty("grid", "1,1");
        character.addProperty("star", 0);
        character.addProperty("level", 1);
        character.add("equip", equip);

        jsonObjectList.add(character);

        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonObjectList);

        JsonSaver.saveString(Constants.playerPath("hero_full.json"), jsonString);
    }
}
