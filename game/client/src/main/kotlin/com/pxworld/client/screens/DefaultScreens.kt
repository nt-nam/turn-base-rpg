package com.pxworld.client.screens

import com.pxworld.client.navigation.ScreenRegistry
import com.pxworld.client.screens.battle.BattleLogScreen
import com.pxworld.client.screens.battle.BattleMainScreen
import com.pxworld.client.screens.battle.BattleResultScreen
import com.pxworld.client.screens.boot.MainMenuScreen
import com.pxworld.client.screens.boot.SlotDeleteConfirmScreen
import com.pxworld.client.screens.boot.SlotListScreen
import com.pxworld.client.screens.boot.SplashScreen
import com.pxworld.client.screens.debug.DebugAtlasBrowserScreen
import com.pxworld.client.screens.debug.DebugCheatsScreen
import com.pxworld.client.screens.debug.DebugMenuScreen
import com.pxworld.client.screens.debug.DebugScreenJumpScreen
import com.pxworld.client.screens.economy.DailyCheckinScreen
import com.pxworld.client.screens.economy.PurchaseConfirmScreen
import com.pxworld.client.screens.economy.RecruitHomeScreen
import com.pxworld.client.screens.economy.RecruitResultScreen
import com.pxworld.client.screens.economy.ShopHomeScreen
import com.pxworld.client.screens.heroes.HeroOverviewScreen
import com.pxworld.client.screens.heroes.HeroRosterScreen
import com.pxworld.client.screens.heroes.HeroStarUpScreen
import com.pxworld.client.screens.heroes.LineupEditorScreen
import com.pxworld.client.screens.inventory.BagConsumablesScreen
import com.pxworld.client.screens.inventory.BagEquipmentScreen
import com.pxworld.client.screens.inventory.EquipmentDetailScreen
import com.pxworld.client.screens.inventory.ItemDetailScreen
import com.pxworld.client.screens.onboarding.HeroCreateClassScreen
import com.pxworld.client.screens.onboarding.HeroCreateConfirmScreen
import com.pxworld.client.screens.onboarding.HeroCreateNameScreen
import com.pxworld.client.screens.progression.AchievementListScreen
import com.pxworld.client.screens.progression.QuestListScreen
import com.pxworld.client.screens.settings.SettingsHomeScreen
import com.pxworld.client.screens.world.EncounterPreviewScreen
import com.pxworld.client.screens.world.PauseMenuScreen
import com.pxworld.client.screens.world.WorldExploreScreen
import com.pxworld.screens.GameScreenId

object DefaultScreens {

    fun registry(): ScreenRegistry = ScreenRegistry(
        mapOf(
            GameScreenId.BOOT_SPLASH to ::SplashScreen,
            GameScreenId.BOOT_MAIN_MENU to ::MainMenuScreen,
            GameScreenId.BOOT_SLOT_LIST to ::SlotListScreen,
            GameScreenId.BOOT_SLOT_DELETE_CONFIRM to ::SlotDeleteConfirmScreen,
            GameScreenId.ONBOARDING_HERO_CREATE_CLASS to ::HeroCreateClassScreen,
            GameScreenId.ONBOARDING_HERO_CREATE_NAME to ::HeroCreateNameScreen,
            GameScreenId.ONBOARDING_HERO_CREATE_CONFIRM to ::HeroCreateConfirmScreen,
            GameScreenId.WORLD_WORLD_EXPLORE to ::WorldExploreScreen,
            GameScreenId.WORLD_ENCOUNTER_PREVIEW to ::EncounterPreviewScreen,
            GameScreenId.WORLD_PAUSE_MENU to ::PauseMenuScreen,
            GameScreenId.BATTLE_BATTLE_MAIN to ::BattleMainScreen,
            GameScreenId.BATTLE_BATTLE_VICTORY to { context, args -> BattleResultScreen(GameScreenId.BATTLE_BATTLE_VICTORY, context, args) },
            GameScreenId.BATTLE_BATTLE_DEFEAT to { context, args -> BattleResultScreen(GameScreenId.BATTLE_BATTLE_DEFEAT, context, args) },
            GameScreenId.BATTLE_BATTLE_DRAW to { context, args -> BattleResultScreen(GameScreenId.BATTLE_BATTLE_DRAW, context, args) },
            GameScreenId.BATTLE_BATTLE_LOG to ::BattleLogScreen,
            GameScreenId.HEROES_HERO_ROSTER to ::HeroRosterScreen,
            GameScreenId.HEROES_HERO_OVERVIEW to ::HeroOverviewScreen,
            GameScreenId.HEROES_HERO_STAR_UP to ::HeroStarUpScreen,
            GameScreenId.HEROES_LINEUP_EDITOR to ::LineupEditorScreen,
            GameScreenId.INVENTORY_BAG_EQUIPMENT to ::BagEquipmentScreen,
            GameScreenId.INVENTORY_BAG_CONSUMABLES to ::BagConsumablesScreen,
            GameScreenId.INVENTORY_EQUIPMENT_DETAIL to ::EquipmentDetailScreen,
            GameScreenId.INVENTORY_ITEM_DETAIL to ::ItemDetailScreen,
            GameScreenId.ECONOMY_SHOP_HOME to ::ShopHomeScreen,
            GameScreenId.ECONOMY_PURCHASE_CONFIRM to ::PurchaseConfirmScreen,
            GameScreenId.ECONOMY_RECRUIT_HOME to ::RecruitHomeScreen,
            GameScreenId.ECONOMY_RECRUIT_RESULT_SINGLE to ::RecruitResultScreen,
            GameScreenId.ECONOMY_DAILY_CHECKIN to ::DailyCheckinScreen,
            GameScreenId.PROGRESSION_QUEST_SIDE to ::QuestListScreen,
            GameScreenId.PROGRESSION_ACHIEVEMENT_LIST to ::AchievementListScreen,
            GameScreenId.SETTINGS_SETTINGS_HOME to ::SettingsHomeScreen,
            GameScreenId.DEBUG_DEBUG_MENU to ::DebugMenuScreen,
            GameScreenId.DEBUG_DEBUG_SCREEN_JUMP to ::DebugScreenJumpScreen,
            GameScreenId.DEBUG_DEBUG_CHEATS to ::DebugCheatsScreen,
            GameScreenId.DEBUG_DEBUG_ATLAS_BROWSER to ::DebugAtlasBrowserScreen,
        ),
    )
}
