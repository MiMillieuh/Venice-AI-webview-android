# Venice AI — Android WebView App

A native **UNOFFICIAL** Android WebView wrapper for [venice.ai/chat](https://venice.ai/chat).

[<img width="282" height="84" alt="badge_obtainium" src="https://github.com/user-attachments/assets/3b0e3707-fc23-48e4-9763-c2a265cccaee" />](https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22org.amethytstlab.veniceai%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMiMillieuh%2FVenice-AI-webview-android%22%2C%22author%22%3A%22MiMillieuh%22%2C%22name%22%3A%22Venice%20AI%22%2C%22preferredApkIndex%22%3A0%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Afalse%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22filterReleaseTitlesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22filterReleaseNotesByRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22verifyLatestTag%5C%22%3Afalse%2C%5C%22sortMethodChoice%5C%22%3A%5C%22date%5C%22%2C%5C%22useLatestAssetDateAsReleaseDate%5C%22%3Afalse%2C%5C%22releaseTitleAsVersion%5C%22%3Afalse%2C%5C%22trackOnly%5C%22%3Afalse%2C%5C%22versionExtractionRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22matchGroupToUse%5C%22%3A%5C%22%5C%22%2C%5C%22versionDetection%5C%22%3Atrue%2C%5C%22releaseDateAsVersion%5C%22%3Afalse%2C%5C%22useVersionCodeAsOSVersion%5C%22%3Afalse%2C%5C%22apkFilterRegEx%5C%22%3A%5C%22%5C%22%2C%5C%22invertAPKFilter%5C%22%3Afalse%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%2C%5C%22appName%5C%22%3A%5C%22%5C%22%2C%5C%22appAuthor%5C%22%3A%5C%22%5C%22%2C%5C%22shizukuPretendToBeGooglePlay%5C%22%3Afalse%2C%5C%22allowInsecure%5C%22%3Afalse%2C%5C%22exemptFromBackgroundUpdates%5C%22%3Afalse%2C%5C%22skipUpdateNotifications%5C%22%3Afalse%2C%5C%22about%5C%22%3A%5C%22%5C%22%2C%5C%22refreshBeforeDownload%5C%22%3Afalse%2C%5C%22includeZips%5C%22%3Afalse%2C%5C%22zippedApkFilterRegEx%5C%22%3A%5C%22%5C%22%7D%22%2C%22overrideSource%22%3A%22GitHub%22%7D)

## Features

- **Microphone input** — voice/mic access for chat
- **Camera access** — photo capture support
- **File & image uploads** — file picker integrated into WebView
- **Native notifications** — Android system notifications via WebView bridge
- **HTTPS-only** — enforced via network security config
- **Back button navigation** — WebView history, exits on empty stack

## Tech

- Kotlin + Gradle
- Android WebView (Chromium-based)
- Target SDK 34 / Min SDK 24

## Build

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## App Structure

| File | Purpose |
|------|---------|
| `MainActivity.kt` | WebView setup, permissions, JS bridges |
| `AndroidManifest.xml` | Permissions & activity config |
| `network_security_config.xml` | HTTPS enforcement |
| `res/layout/activity_main.xml` | WebView + ProgressBar layout |
| `res/values/` | Colors, themes, strings |

## Donations

If you want to support me, it would be appreciated :)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/W7W61L0JLW)
