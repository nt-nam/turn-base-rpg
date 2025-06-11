package com.game.screens;

public enum ScreenType {
    BATTLE,              //	Turn-based battle screen → battle UI, battle flow
    BATTLE_RESULT,       // Show kết quả sau battle (Victory, Defeat, Loot, Exp)
    SPLASH,              // Logo|splash lúc khởi động game
    LOADING,
    MENU_GAME,           // Menu chính: New Game, Continue, Option, Exit
    NEW_PLAYER,          // Chọn nhân vât đầu game
    SELECT_PLAYER,       // Chọn nhân vât đã chơi
    MAIN_SETTING,
    MAIN_GAME,           //Màn chơi chính → player di chuyển trên world map (map tiled)
    WORLD_MAP,           //Màn chơi chính → player di chuyển trên world map (map tiled)
    CHARACTER_SELECT,    //Xem / nâng skill / stat nhân vật
    INVENTORY,           //Quản lý Item, Equip, Use Item
    QUEST,               //Quản lý nhiệm vụ
    MINI_MAP,            //Xem World Map mini hoặc dungeon map
    PAUSE,               //Pause → Resume, Save, Quit to Menu
    TUTORIAL
}
