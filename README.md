# VoxelLive Android 2.0.2-alpha2 (test candidate)

VoxelLive runs its Python backend inside the Android APK through Chaquopy. Termux is **not required at runtime** for this APK. Minecraft Bedrock still needs to be open and connected to the local WebSocket address shown in the dashboard.

## Included in this source update

- TikTok LIVE gift/follow event handlers are retained in the Python backend.
- TikTok connection attempts retry with an increasing delay after disconnects.
- Gift rules and Follow/cooldown settings autosave while editing, and are persisted by the local backend.
- The Android foreground service has an ongoing notification and a bounded partial wake lock while it runs.
- Python is updated to 3.14 with an Android `pydantic-core` wheel candidate for TikTokLive 7.0.1.

## Important test status

This is a **build/test candidate, not a verified release APK**. The `pydantic-core` Android wheel at `https://eutalix.github.io/android-pydantic-core/` is published for Android/Termux and its publisher says other Android Python runtimes such as Chaquopy are expected to work but are not CI-tested. Therefore the Chaquopy import and TikTok login-free public LIVE connection must be tested on a real ARM64 Android device before using it for a paid or scheduled LIVE. TikTokLive is an unofficial reverse-engineered integration and can stop working if TikTok changes its service.

## Build environment

- Android Studio / Gradle with JDK 17
- Android Gradle Plugin 8.9.1
- Chaquopy 17.2.0
- Python 3.14
- Android device ABI: `arm64-v8a` (this alpha build is arm64-only)

Open the project folder in Android Studio and sync Gradle. Build with **Build > Build Bundle(s) / APK(s) > Build APK(s)**. If Gradle reports that it cannot find the Android `pydantic-core` wheel or fails loading its native extension, stop and send the full build/runtime log; do not replace it with a Linux wheel or install packages into Termux.

## Android runtime notes

- Keep the VoxelLive foreground-service notification visible during LIVE.
- Disable battery optimization for VoxelLive if Android keeps stopping it.
- The wake lock increases battery use. Stop VoxelLive when the LIVE session is finished.
- For Android target compatibility this sideload candidate uses targetSdk 34. Do not publish it to Play Store without reviewing current Play policy and foreground-service requirements.

## Cloud URL

The local backend reads `cloud_api_url.txt`, which contains the HTTPS VoxelCloud Worker URL.
