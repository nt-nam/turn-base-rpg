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
import com.pxworld.client.screens.battle.BattlePauseScreen
import com.pxworld.client.screens.battle.SkillSelectScreen
import com.pxworld.client.screens.battle.TargetSelectScreen
import com.pxworld.client.screens.battle.UnitInspectScreen
import com.pxworld.client.screens.battle.TurnTimelineScreen
import com.pxworld.client.screens.battle.WeaknessHintScreen
import com.pxworld.client.screens.battle.DamageBreakdownScreen
import com.pxworld.client.screens.battle.LevelUpScreen
import com.pxworld.client.screens.battle.FleeConfirmScreen
import com.pxworld.client.screens.battle.RetryConfirmScreen
import com.pxworld.client.screens.battle.ReplayListScreen
import com.pxworld.client.screens.battle.BattleRewardsScreen
import com.pxworld.client.screens.battle.BattleQuestProgressScreen
import com.pxworld.client.screens.heroes.HeroStatsScreen
import com.pxworld.client.screens.heroes.HeroSkillsScreen
import com.pxworld.client.screens.heroes.HeroEquipmentScreen
import com.pxworld.client.screens.heroes.HeroLoreScreen
import com.pxworld.client.screens.heroes.HeroLevelUpScreen
import com.pxworld.client.screens.heroes.HeroMergeConfirmScreen
import com.pxworld.client.screens.heroes.HeroCompareScreen
import com.pxworld.client.screens.heroes.HeroDismissScreen
import com.pxworld.client.screens.heroes.LineupPresetsScreen
import com.pxworld.client.screens.heroes.LineupAnalysisScreen
import com.pxworld.client.screens.heroes.SynergyViewScreen
import com.pxworld.client.screens.heroes.ClassCounterChartScreen
import com.pxworld.client.screens.inventory.BagMaterialsScreen
import com.pxworld.client.screens.inventory.BagKeyItemsScreen
import com.pxworld.client.screens.inventory.EquipmentCompareScreen
import com.pxworld.client.screens.inventory.EquipmentAssignScreen
import com.pxworld.client.screens.inventory.EquipmentUpgradeScreen
import com.pxworld.client.screens.inventory.EquipmentSalvageScreen
import com.pxworld.client.screens.inventory.BagFilterScreen
import com.pxworld.client.screens.inventory.SellConfirmScreen
import com.pxworld.client.screens.inventory.BulkSellScreen
import com.pxworld.client.screens.inventory.ItemUseTargetScreen
import com.pxworld.client.screens.inventory.ItemUseResultScreen
import com.pxworld.client.screens.economy.OfferDetailScreen
import com.pxworld.client.screens.economy.PurchaseResultScreen
import com.pxworld.client.screens.economy.RecruitRatesScreen
import com.pxworld.client.screens.economy.RecruitHistoryScreen
import com.pxworld.client.screens.economy.PityTrackerScreen
import com.pxworld.client.screens.economy.CheckinClaimScreen
import com.pxworld.client.screens.economy.CurrencyExchangeScreen
import com.pxworld.client.screens.economy.IdleRewardsScreen
import com.pxworld.client.screens.progression.QuestDetailScreen
import com.pxworld.client.screens.progression.QuestClaimScreen
import com.pxworld.client.screens.progression.AchievementDetailScreen
import com.pxworld.client.screens.progression.CodexHeroesScreen
import com.pxworld.client.screens.progression.CodexEnemiesScreen
import com.pxworld.client.screens.progression.CodexItemsScreen
import com.pxworld.client.screens.progression.CodexMapsScreen
import com.pxworld.client.screens.progression.TipsLibraryScreen
import com.pxworld.client.screens.progression.GlossaryScreen
import com.pxworld.client.screens.world.RegionMapScreen
import com.pxworld.client.screens.world.FastTravelScreen
import com.pxworld.client.screens.world.MinimapScreen
import com.pxworld.client.screens.world.QuestTrackerScreen
import com.pxworld.client.screens.world.MapTransitionScreen
import com.pxworld.client.screens.settings.SettingsAudioScreen
import com.pxworld.client.screens.settings.SettingsGraphicsScreen
import com.pxworld.client.screens.settings.SettingsControlsScreen
import com.pxworld.client.screens.settings.SettingsLanguageScreen
import com.pxworld.client.screens.settings.SettingsAccessibilityScreen
import com.pxworld.client.screens.settings.SettingsPrivacyScreen
import com.pxworld.client.screens.settings.DataDownloadScreen
import com.pxworld.client.screens.settings.DeleteAccountScreen
import com.pxworld.client.screens.settings.CreditsScreen
import com.pxworld.client.screens.settings.HelpCenterScreen
import com.pxworld.client.screens.settings.FaqScreen
import com.pxworld.client.screens.settings.BugReportScreen
import com.pxworld.client.screens.settings.PlaytimeReportScreen
import com.pxworld.client.screens.debug.DebugBattleSandboxScreen
import com.pxworld.client.screens.debug.DebugMapInspectorScreen
import com.pxworld.client.screens.debug.DebugSaveEditorScreen
import com.pxworld.client.screens.debug.DebugFlagsScreen
import com.pxworld.client.screens.debug.DebugPerformanceScreen
import com.pxworld.client.screens.debug.DebugLogsScreen
import com.pxworld.client.screens.debug.DebugLocalePreviewScreen
import com.pxworld.client.screens.debug.DebugAutomationScreen
import com.pxworld.client.screens.boot.LegalNoticeScreen
import com.pxworld.client.screens.boot.PrivacyConsentScreen
import com.pxworld.client.screens.boot.LanguagePickScreen
import com.pxworld.client.screens.boot.PatchNotesScreen
import com.pxworld.client.screens.boot.OfflineModeScreen
import com.pxworld.client.screens.onboarding.TutorialMoveScreen
import com.pxworld.client.screens.onboarding.TutorialBattleScreen
import com.pxworld.client.screens.onboarding.TutorialLineupScreen
import com.pxworld.client.screens.onboarding.TutorialRewardScreen
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
            GameScreenId.BATTLE_BATTLE_PAUSE to ::BattlePauseScreen,
            GameScreenId.BATTLE_SKILL_SELECT to ::SkillSelectScreen,
            GameScreenId.BATTLE_TARGET_SELECT to ::TargetSelectScreen,
            GameScreenId.BATTLE_UNIT_INSPECT to ::UnitInspectScreen,
            GameScreenId.BATTLE_TURN_TIMELINE to ::TurnTimelineScreen,
            GameScreenId.BATTLE_WEAKNESS_HINT to ::WeaknessHintScreen,
            GameScreenId.BATTLE_DAMAGE_BREAKDOWN to ::DamageBreakdownScreen,
            GameScreenId.BATTLE_LEVEL_UP to ::LevelUpScreen,
            GameScreenId.BATTLE_FLEE_CONFIRM to ::FleeConfirmScreen,
            GameScreenId.BATTLE_RETRY_CONFIRM to ::RetryConfirmScreen,
            GameScreenId.BATTLE_REPLAY_LIST to ::ReplayListScreen,
            GameScreenId.BATTLE_BATTLE_REWARDS to ::BattleRewardsScreen,
            GameScreenId.BATTLE_BATTLE_QUEST_PROGRESS to ::BattleQuestProgressScreen,
            GameScreenId.HEROES_HERO_STATS to ::HeroStatsScreen,
            GameScreenId.HEROES_HERO_SKILLS to ::HeroSkillsScreen,
            GameScreenId.HEROES_HERO_EQUIPMENT to ::HeroEquipmentScreen,
            GameScreenId.HEROES_HERO_LORE to ::HeroLoreScreen,
            GameScreenId.HEROES_HERO_LEVEL_UP to ::HeroLevelUpScreen,
            GameScreenId.HEROES_HERO_MERGE_CONFIRM to ::HeroMergeConfirmScreen,
            GameScreenId.HEROES_HERO_COMPARE to ::HeroCompareScreen,
            GameScreenId.HEROES_HERO_DISMISS to ::HeroDismissScreen,
            GameScreenId.HEROES_LINEUP_PRESETS to ::LineupPresetsScreen,
            GameScreenId.HEROES_LINEUP_ANALYSIS to ::LineupAnalysisScreen,
            GameScreenId.HEROES_SYNERGY_VIEW to ::SynergyViewScreen,
            GameScreenId.HEROES_CLASS_COUNTER_CHART to ::ClassCounterChartScreen,
            GameScreenId.INVENTORY_BAG_MATERIALS to ::BagMaterialsScreen,
            GameScreenId.INVENTORY_BAG_KEY_ITEMS to ::BagKeyItemsScreen,
            GameScreenId.INVENTORY_EQUIPMENT_COMPARE to ::EquipmentCompareScreen,
            GameScreenId.INVENTORY_EQUIPMENT_ASSIGN to ::EquipmentAssignScreen,
            GameScreenId.INVENTORY_EQUIPMENT_UPGRADE to ::EquipmentUpgradeScreen,
            GameScreenId.INVENTORY_EQUIPMENT_SALVAGE to ::EquipmentSalvageScreen,
            GameScreenId.INVENTORY_BAG_FILTER to ::BagFilterScreen,
            GameScreenId.INVENTORY_SELL_CONFIRM to ::SellConfirmScreen,
            GameScreenId.INVENTORY_BULK_SELL to ::BulkSellScreen,
            GameScreenId.INVENTORY_ITEM_USE_TARGET to ::ItemUseTargetScreen,
            GameScreenId.INVENTORY_ITEM_USE_RESULT to ::ItemUseResultScreen,
            GameScreenId.ECONOMY_OFFER_DETAIL to ::OfferDetailScreen,
            GameScreenId.ECONOMY_PURCHASE_RESULT to ::PurchaseResultScreen,
            GameScreenId.ECONOMY_RECRUIT_RATES to ::RecruitRatesScreen,
            GameScreenId.ECONOMY_RECRUIT_HISTORY to ::RecruitHistoryScreen,
            GameScreenId.ECONOMY_PITY_TRACKER to ::PityTrackerScreen,
            GameScreenId.ECONOMY_CHECKIN_CLAIM to ::CheckinClaimScreen,
            GameScreenId.ECONOMY_CURRENCY_EXCHANGE to ::CurrencyExchangeScreen,
            GameScreenId.ECONOMY_IDLE_REWARDS to ::IdleRewardsScreen,
            GameScreenId.PROGRESSION_QUEST_DETAIL to ::QuestDetailScreen,
            GameScreenId.PROGRESSION_QUEST_CLAIM to ::QuestClaimScreen,
            GameScreenId.PROGRESSION_ACHIEVEMENT_DETAIL to ::AchievementDetailScreen,
            GameScreenId.PROGRESSION_CODEX_HEROES to ::CodexHeroesScreen,
            GameScreenId.PROGRESSION_CODEX_ENEMIES to ::CodexEnemiesScreen,
            GameScreenId.PROGRESSION_CODEX_ITEMS to ::CodexItemsScreen,
            GameScreenId.PROGRESSION_CODEX_MAPS to ::CodexMapsScreen,
            GameScreenId.PROGRESSION_TIPS_LIBRARY to ::TipsLibraryScreen,
            GameScreenId.PROGRESSION_GLOSSARY to ::GlossaryScreen,
            GameScreenId.WORLD_REGION_MAP to ::RegionMapScreen,
            GameScreenId.WORLD_FAST_TRAVEL to ::FastTravelScreen,
            GameScreenId.WORLD_MINIMAP to ::MinimapScreen,
            GameScreenId.WORLD_QUEST_TRACKER to ::QuestTrackerScreen,
            GameScreenId.WORLD_MAP_TRANSITION to ::MapTransitionScreen,
            GameScreenId.SETTINGS_SETTINGS_AUDIO to ::SettingsAudioScreen,
            GameScreenId.SETTINGS_SETTINGS_GRAPHICS to ::SettingsGraphicsScreen,
            GameScreenId.SETTINGS_SETTINGS_CONTROLS to ::SettingsControlsScreen,
            GameScreenId.SETTINGS_SETTINGS_LANGUAGE to ::SettingsLanguageScreen,
            GameScreenId.SETTINGS_SETTINGS_ACCESSIBILITY to ::SettingsAccessibilityScreen,
            GameScreenId.SETTINGS_SETTINGS_PRIVACY to ::SettingsPrivacyScreen,
            GameScreenId.SETTINGS_DATA_DOWNLOAD to ::DataDownloadScreen,
            GameScreenId.SETTINGS_DELETE_ACCOUNT to ::DeleteAccountScreen,
            GameScreenId.SETTINGS_CREDITS to ::CreditsScreen,
            GameScreenId.SETTINGS_HELP_CENTER to ::HelpCenterScreen,
            GameScreenId.SETTINGS_FAQ to ::FaqScreen,
            GameScreenId.SETTINGS_BUG_REPORT to ::BugReportScreen,
            GameScreenId.SETTINGS_PLAYTIME_REPORT to ::PlaytimeReportScreen,
            GameScreenId.DEBUG_DEBUG_BATTLE_SANDBOX to ::DebugBattleSandboxScreen,
            GameScreenId.DEBUG_DEBUG_MAP_INSPECTOR to ::DebugMapInspectorScreen,
            GameScreenId.DEBUG_DEBUG_SAVE_EDITOR to ::DebugSaveEditorScreen,
            GameScreenId.DEBUG_DEBUG_FLAGS to ::DebugFlagsScreen,
            GameScreenId.DEBUG_DEBUG_PERF_OVERLAY to ::DebugPerformanceScreen,
            GameScreenId.DEBUG_DEBUG_LOGS to ::DebugLogsScreen,
            GameScreenId.DEBUG_DEBUG_LOCALE_PREVIEW to ::DebugLocalePreviewScreen,
            GameScreenId.DEBUG_DEBUG_AUTOMATION to ::DebugAutomationScreen,
            GameScreenId.BOOT_LEGAL_NOTICE to ::LegalNoticeScreen,
            GameScreenId.BOOT_PRIVACY_CONSENT to ::PrivacyConsentScreen,
            GameScreenId.BOOT_LANGUAGE_PICK to ::LanguagePickScreen,
            GameScreenId.BOOT_PATCH_NOTES to ::PatchNotesScreen,
            GameScreenId.BOOT_OFFLINE_MODE to ::OfflineModeScreen,
            GameScreenId.ONBOARDING_TUTORIAL_MOVE to ::TutorialMoveScreen,
            GameScreenId.ONBOARDING_TUTORIAL_BATTLE to ::TutorialBattleScreen,
            GameScreenId.ONBOARDING_TUTORIAL_LINEUP to ::TutorialLineupScreen,
            GameScreenId.ONBOARDING_TUTORIAL_REWARD to ::TutorialRewardScreen,
            GameScreenId.BATTLE_REPLAY_VIEWER to { context, args -> BattleMainScreen(GameScreenId.BATTLE_REPLAY_VIEWER, context, args) },
            GameScreenId.ECONOMY_SHOP_ITEMS to { context, args -> ShopHomeScreen(GameScreenId.ECONOMY_SHOP_ITEMS, context, args) },
            GameScreenId.ECONOMY_SHOP_EQUIPMENT to { context, args -> ShopHomeScreen(GameScreenId.ECONOMY_SHOP_EQUIPMENT, context, args) },
        ),
    )
}
