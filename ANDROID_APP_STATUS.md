# Android app scaffold status

This repository now includes an initial Android application project in the `app/` directory.

## Included now
- Kotlin activity with a phone-friendly, scrollable interface.
- Inputs for CPU thread count, distinguished-point bits, and puzzle configuration text.
- Prepare and reset controls with basic input validation and an activity log.
- Android NDK/CMake native bridge scaffold targeting ARM64-capable Android builds.
- GitHub Actions workflow that attempts to build and upload a debug APK.

## Important limitation
The original upstream solver is **not yet linked into the Android app**. The upstream Makefile uses x86-specific `-mssse3` flags and includes CUDA headers, while Android phones use ARM64. The current native bridge reports this honestly; **Prepare run validates UI fields only and does not start a search**. A successful Android project build would not prove the solver itself works.

## Build
The GitHub Actions workflow runs `gradle --no-daemon assembleDebug` on pushes to `master` and uploads `app/build/outputs/apk/debug/app-debug.apk` if successful. Check the Actions tab for the actual result. No successful build is claimed until the workflow completes successfully.

## Next engineering work
1. Port or replace x86-specific arithmetic paths for `arm64-v8a`.
2. Add the engine through a JNI interface with cancellable background execution and progress callbacks.
3. Add safe local file selection, checkpoint/resume, and tests on an actual Android ARM64 device.
