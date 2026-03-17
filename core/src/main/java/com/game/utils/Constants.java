package com.game.utils;

import com.game.managers.GameSessionManager;

public class Constants {
    //TEXT
    public static final String GAME_TITLE         = "Turn Base Game";

    //FONT
    public static final String BMF                = "font/arial_uni_30.fnt";

    //PATH
    public static final String CHARACTER_ATLAS    = "atlas/characters/";
    public static final String SKILL              = "atlas/skill/";

    //ATLAS
    public static final String UI_WOOD                  = "atlas/ui/wood.atlas";
    public static final String UI_POPUP                 = "atlas/ui/popup.atlas";
    public static final String ATLAS_ITEM               = "atlas/inventory/item.atlas";
    public static final String ATLAS_ICON               = "atlas/ui/icon.atlas";
    public static final String SKILL_SKILL              = "atlas/skill/skill.atlas";
    public static final String SKILL_BOOM               = "atlas/skill/boom.atlas";
    public static final String SKILL_FIRE               = "atlas/skill/fire.atlas";
    public static final String SKILL_LAZE               = "atlas/skill/laze.atlas";

    //JSON — Dùng playerPath() thay vì các field mutable
    public static final String MAININFO_JSON_LOCAL      = "data/maininfo.json";

    /** Trả về đường dẫn file JSON theo player hiện tại */
    public static String playerPath(String filename) {
        return "data/select/" + GameSessionManager.getInstance().playerName + "/" + filename;
    }

    //JSON BASE
    public static final String ACHIEVEMENT_BASE_JSON    = "data/base/achievement.json";
    public static final String MISSION_BASE_JSON        = "data/base/mission_base.json";
    public static final String DAILY_BASE_JSON          = "data/base/daily_rewards.json";
    public static final String EQUIP_JSON               = "data/base/equip_base.json";
    public static final String ITEM_JSON                = "data/base/items_base.json";
    public static final String CHARACTER_BASE_JSON      = "data/base/character_base.json";
    public static final String SKILL_JSON               = "data/base/skill_base.json";

    //MUSIC
    public static final String MUSIC_BATTLE             = "music/battle.mp3";
    public static final String MUSIC_WORLD1             = "music/world1.mp3";

    //SOUND
    public static final String SOUND_BUBBLE_SWITCH      = "sound/bubble_switch.mp3";
    public static final String SOUND_BUBBLE_SHOOT       = "sound/bubble_shoot.mp3";

}
