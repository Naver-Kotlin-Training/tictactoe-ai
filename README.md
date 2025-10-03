# Tic Tac Toe (Kotlin + Jetpack Compose)

An elegant **Tic Tac Toe - AI** Android app built for Week 1 Demo
Implemented the Ai opponent feature along with various difficulties, improved UI with Material3 
Design theme, score tracking history and alot more. Come play and explore on your own.
---

## 🚀 Features
- Built with **Kotlin** and **Jetpack Compose** and **Material3 Design**
- Clean **MVVM architecture** with 'ViewModel' and 'StateFlow'
- **Three AI difficulty levels**
   - *Easy*: Random valid moves
   - *Medium*: Basic strategy (center & corners)
   - *Hard*: Full **Minimax** with alpha-beta pruning
-  **Move visualization** (shows AI thinking process)
- **Score tracking** (persistent using Room)

---

## 📂 Project Structure
```text
app/
├── manifests/                  # AndroidManifest.xml
├── java/com/example/tictactoe/ # Kotlin source code
│   ├── MainActivity.kt         # Entry point
│   ├── model/                  # Game models (Player, Move, GameState)
│   ├── ai/                     # MinimaxEngine & AI logic
│   ├── data/                   # DataStore / Room entities
│   ├── repository/             # Repository for score persistence
│   ├── ui/                     # Compose UI layer
│   │   ├── GameScreen.kt       # Main game screen
│   │   └── components/         # Board & UI components
│   └── viewmodel/              # GameViewModel (state + logic)
└── res/
    ├── drawable/               # App icons
    ├── values/                 # colors.xml, themes.xml, strings.xml
```
---

## 🛠️ Tech Stack
- **Kotlin** – Primary language
- **Jetpack Compose** – Declarative UI
- **Material3** - Theme & Elevated UI
- **Android Studio** – Development environment
- **Room / DataStore** - Persistent scoring
- **Coroutines + Stateflow** - Async AI & state management
- **Gradle (KTS)** – Build system

---

## 📦 Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/Naver-Kotlin-Training/tictactoe-ai.git
   cd /tictactoe-ai
Open in Android Studio.

Select a device or emulator.

Press Run ▶️.

## 📲 APK Release
Download the latest APK from the Releases section.

## 📝 Roadmap
 [x] Implement full game logic

 [x] Add score tracking

 [x] Add restart / reset button

 [x] Improve UI with animations

 [x] Optional AI opponent

## 📜 License
This project is licensed under the MIT License – feel free to use and modify it.
