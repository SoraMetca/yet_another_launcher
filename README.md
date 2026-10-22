# Yet Another Launcher

A highly customizable Android home-screen replacement. Yet Another Launcher (YAL) is built to replace your stock launcher while giving you as much control as possible over looks and behavior.

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![API](https://img.shields.io/badge/Android%20SDK-29-blue)

<!-- add some screenshots -->

### Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Configuration & Backup](#configuration--backup)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

<br />

### Features

### App drawer
- Display installed apps as a **grid** or a **list**.
- Sort **alphabetically** (A–Z / Z–A) or define a **custom order** by hand.
- **Dynamic search** to find apps quickly.
- **Context menu** (long-press an app for ~2 s, then release):
  - Uninstall the app
  - Open the app's native system settings / info page
  - **Rename** the app (display name only, in the launcher list)
  - **Hide** the app without uninstalling it
- **Hidden apps list** in Settings, where hidden apps can be launched and have their visibility restored.

### Home screen
- **Long-press and drag** an app icon to drop a shortcut anywhere on the home screen.
- **Multiple home screens** – swipe left/right to move between pages, each holding shortcuts or widgets.
- Support for **Android widgets** and **app folders**.

### Customization
- Configurable app drawer appearance (grid / list).
- **Live wallpaper** support (image, GIF and video wallpapers).
- Selectable **app launch animation**: None, Fade in, Zoom in, Slide from the bottom, Slide from the top, Enter.
- Option to **hide the status bar**.
- **Gesture menu** – map gestures (swipe up, swipe down, double tap, long press) to actions:
  - Open app drawer
  - Open home screen settings
  - Open launcher settings

### Settings persistence & backup
- All settings are **saved automatically** to internal storage and survive app restarts.
- **Export** your configuration to a JSON file and **import** it back – use it as a backup or to share your setup with others.

<br />

### Tech Stack

| Technology / Library | Version | Purpose | Why |
|---|---|---|---|
| Kotlin | – | Application language | First-class language for Android development |
| Android SDK | 29 | Main application framework | Official Android SDK |
| AndroidX Fragment | 1.8.9 | Displaying screen content | Easy navigation between screens |
| Jetpack DataStore (Preferences) | 1.2.1 | Storing settings | Better alternative to `SharedPreferences` |
| Kotlin Coroutines & Flow | – | Asynchronous work, reactive settings streams | Idiomatic, lifecycle-friendly async on Android |
| AndroidX Lifecycle (ViewModel) | – | UI state holding | Survives configuration changes |
| Material Components | – | UI components (e.g. text input for search) | Consistent, accessible widgets |
| AppWidgetHost API | – | Hosting third-party widgets | Standard Android widget support |
| Gradle | – | Build automation, dependency management | Standard build system for Android projects |
| Android Gradle Plugin (AGP) | – | Building, packaging and signing the APK | Required to build Android apps with Gradle |

<br />

### Architecture

The code is split into modules (packages) by the functionality they provide. Settings are persisted through DataStore and exposed to the UI via `Flow`s; each setting group has its own repository, created lazily by a central `RepositoryProvider`.

```
app
├── activity      Initializes values needed for the app to run (MainActivity, SettingsActivity)
├── core          Shared functionality
│   ├── adapter     RecyclerView / ViewPager adapters
│   ├── swipes      Swipe & gesture detection
│   ├── wallpaper   Image / video wallpaper views
│   └── widget      Widget hosting views
├── model         Representation of entities (AppObject, FolderObject, PagerObject, AppDrawerViewModel…)
├── settings
│   ├── repository  Classes that work with DataStore
│   └── options     Enums representing setting options
└── view          Graphical presentation of the app
    ├── app_drawer
    ├── home_screen
    └── settings
```

<br />

### Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio)
- JDK 17
- Android SDK Platform 29
- An Android device or emulator (API 29+)

<br />

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/SoraMetca/yet_another_launcher.git
   ```
   ```bash
   cd yet-another-launcher
   
   ```
2. **Open the project** in Android Studio and let Gradle sync.
3. **Run** the `app` configuration on a device or emulator, or build from the command line:
   ```bash
   ./gradlew assembleDebug
   ```
   ```bash
   ./gradlew installDebug
   ```
4. **Set as default launcher:** press the Home button on the device and choose *Yet Another Launcher* → *Always*. (You can also go to *Settings → Apps → Default apps → Home app*.)

<br />

### Usage

| Action | Result |
|---|---|
| Open the app drawer | Use the gesture assigned to it (default configurable in Settings → Gestures) |
| Long-press an app (~2 s) and release | Opens the context menu (uninstall, app info, rename, hide) |
| Long-press and drag an app | Drops a shortcut on the home screen |
| Swipe left / right on the home screen | Switch between home screen pages |
| Settings → Hidden applications | Launch or unhide hidden apps |

<br />

### Configuration & Backup

Settings are stored automatically via DataStore. To back up or share a configuration:

1. Go to **Settings → Profiles**.
2. Tap **Export** to write a file named `YAL_Profile_<yyyy-MM-dd>.json`.
3. To restore, tap **Import** and select a previously exported JSON file.

The backup is a JSON document with a schema version, timestamp and all stored preferences:

```json
{
  "backup_schema_version": 1,
  "timestamp": 1760000000000,
  "preferences": { "...": "..." }
}
```

Backups with a newer, incompatible schema version are rejected on import.

### License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for the full license text.
