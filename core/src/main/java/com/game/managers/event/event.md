event
├── GameEvent(interface)
├── EventManager
├── world
│   ├── DialogEndedEvent
│   ├── DialogStartedEvent
│   ├── ItemPickedEvent
│   ├── MapTransitionEvent
│   ├── NPCInteractionEvent
│   ├── PlayerEncounteredEnemyEvent
│   ├── PlayerPositionChangedEvent
│   ├── QuestUpdatedEvent
│   └── SaveGameRequestedEvent
├── battle
│   ├── encounter
│   │   ├── BattleStartRequestedEvent
│   │   ├── BattleTransitionCompletedEvent
│   │   └── BattleTransitionStartedEvent
│   ├── phase
│   │   ├── BattleLogEvent
│   │   ├── BattlePausedEvent
│   │   ├── BattleResumedEvent
│   │   ├── BattleStartedEvent
│   │   ├── EnemyActionSelectedEvent
│   │   ├── EnemyDamagedEvent
│   │   ├── EnemyHealedEvent
│   │   ├── EnemyStatusEffectAppliedEvent
│   │   ├── PlayerActionSelectedEvent
│   │   ├── PlayerDamagedEvent
│   │   ├── PlayerHealedEvent
│   │   ├── PlayerStatusEffectAppliedEvent
│   │   ├── TurnEndedEvent
│   │   └── TurnStartedEvent
│   └── result
│       ├── BattleDefeatEvent
│       ├── BattleEndedEvent
│       ├── BattleEscapeEvent
│       ├── BattleRewardGainedEvent
│       ├── BattleVictoryEvent
│       ├── PlayerDiedEvent
│       └── PlayerLevelUpEvent
├── ui
│   ├── ShowPopupEvent
│   ├── HidePopupEvent
│   ├── ShowNotificationEvent
│   ├── HideNotificationEvent
│   ├── ScreenFadeInEvent
│   ├── ScreenFadeOutEvent
│   ├── MusicPlayEvent
│   ├── MusicStopEvent
│   ├── SoundPlayEvent
│   └── ScreenShakeEvent
├── system
│   ├── SaveGameEvent
│   ├── LoadGameEvent
│   ├── SettingsChangedEvent
│   ├── LanguageChangedEvent
│   ├── GamePausedEvent
│   ├── GameResumedEvent
│   └── AchievementUnlockedEvent
