com/example/my_rouge_mobile/
├── MainActivity.kt
├── viewmodel/
│   ├── GameViewModel.kt        ← бывший GameController (логика команд)
│   └── GameUiState.kt          ← снимок состояния для UI
├── ui/
│   ├── theme/                  ← уже есть
│   ├── screens/
│   │   ├── StartScreen.kt      ← бывший StartWindow (ввод имени)
│   │   ├── PrologueScreen.kt   ← бывший PrologueWindow
│   │   ├── GameScreen.kt       ← бывший GameRender + GameWindow
│   │   ├── GameOverScreen.kt   ← бывший GameOverWindow
│   │   └── VictoryScreen.kt    ← бывший VictoryWindow
│   ├── dialogs/
│   │   ├── BackpackDialog.kt   ← бывший BackpackWindow
│   │   ├── PocketDialog.kt     ← бывший PocketWindow
│   │   ├── ExitMenuDialog.kt   ← бывший ExitWindow
│   │   ├── LeaderboardDialog.kt← бывший Leaderboard
│   │   └── InformationDialog.kt← бывший Information
│   ├── components/
│   │   ├── Hud.kt              ← бывший ShowStatistic
│   │   ├── MessageOverlay.kt   ← бывший MessageOutput + Messages
│   │   ├── ControlPad.kt       ← новый, touch-управление
│   │   └── GameCanvas.kt       ← бывший GameRender (только отрисовка)
│   └── render/
│       ├── Camera.kt           ← новый, вьюпорт вокруг игрока
│       ├── MapRenderer.kt      ← комнаты, коридоры, туман
│       ├── EntityRenderer.kt   ← враги, предметы, игрок
│       └── ProjectileRenderer.kt← снаряды, лучи, волны
├── mapping/
│   └── SpriteMapping.kt        ← тип → R.drawable.*
└── input/
└── ...                     ← (можно удалить, если управление в ControlPad)