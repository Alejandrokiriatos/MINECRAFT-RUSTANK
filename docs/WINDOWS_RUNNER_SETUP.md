# Windows runner setup for real game builds and screenshots

This connects GitHub Actions to a real Windows desktop capable of running the Unreal-built game. It does not create a virtual machine or install Unreal automatically.

## Machine prerequisites
- Windows 10/11 x64.
- A compatible Unreal Engine 5 installation (install through Epic Games Launcher).
- Visual Studio 2022 with Desktop development with C++, Game development with C++, MSVC toolset and Windows SDK.
- Git and Git LFS.
- Sufficient disk space for the engine, DerivedDataCache and packaged build.
- A logged-in interactive desktop session for screenshot capture.

## Configure GitHub runner
1. Open repository Settings → Actions → Runners → New self-hosted runner.
2. Select Windows x64 and follow GitHub's current download/configuration commands.
3. Register it with the label `UnrealEngine` in addition to default labels `self-hosted`, `Windows`, and `X64`.
4. Keep it dedicated to this repository; do not run untrusted pull-request code on it.
5. Start the runner interactively from the logged-in desktop session for screenshot capture. A Windows service in Session 0 generally cannot capture the visible desktop.
6. Set environment variable `UE_ROOT` to the actual installed engine root, e.g. `C:\\Program Files\\Epic Games\\UE_5.x` (example only).
7. Restart the runner process after changing environment variables.

## Run the smoke test
1. Open the repository Actions tab.
2. Select **Project validation and Windows smoke test**.
3. Choose **Run workflow** on `main`.
4. Enable `run_unreal_smoke_test`.
5. Start the workflow and wait for the Windows job.
6. Download artifact `windows-smoke-test-evidence`. It should contain the packaged build, build log and `Artifacts/Evidence/game-launch.png`.

## Important
- Do not pin an engine version until the installed version is confirmed.
- The environment checker reports the actual engine version from `Engine/Build/Build.version`; it does not install engine components.
- The first launch verifies only that the app starts and stays alive long enough for capture; it does not prove gameplay is complete.
- The screenshot step requires a visible desktop session, not a Windows service running in Session 0.
- If the runner is offline, UE_ROOT is wrong, the build fails, or the desktop is unavailable, preserve logs and report the exact failing step. Never fabricate a screenshot.
