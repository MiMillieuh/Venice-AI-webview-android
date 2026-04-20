# Venice AI — Android WebView App

A native Android WebView wrapper for [venice.ai/chat](https://venice.ai/chat).

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
