# Standalone Survival Game

A new standalone PC survival game built with **C++ and Unreal Engine 5**.

## Project status
- Phase 1 — repository and development foundation: in progress.
- No gameplay systems, world art, weapons, items, sounds, or third-party assets have been added yet.
- The project deliberately starts with a minimal C++ module so that requested gameplay scope can be implemented without silently inventing features.

## Open locally
1. Install a compatible Unreal Engine 5 version through Epic Games Launcher.
2. Clone this repository with Git LFS installed.
3. Right-click `SurvivalGame.uproject` and generate project files, or open it with Unreal Editor.
4. Build the `SurvivalGameEditor` target in Development Editor / Win64.
5. Open the project in Unreal Editor.

The engine version is intentionally not hard-pinned in the descriptor yet. Record the exact installed UE version in `docs/TECHNICAL_DECISIONS.md` before the first team build.

## Repository guide
- `docs/ROADMAP.md` — phases and completion gates.
- `docs/ARCHITECTURE.md` — initial technical boundaries.
- `docs/TECHNICAL_DECISIONS.md` — decisions and version record.
- `docs/TESTING.md` — build, smoke-test and screenshot strategy.
- `docs/ASSETS.md` — asset provenance and licensing rules.
- `.github/workflows/validate.yml` — hosted repository checks and optional Unreal runner build/capture.

## Screenshot proof
The workflow has an optional manual job for a **self-hosted Windows runner** with Unreal Engine installed and an interactive desktop session. It builds, launches the packaged game and stores a desktop screenshot as a GitHub Actions artifact. GitHub-hosted runners do not include Unreal Engine or a usable interactive game desktop by default.

## Scope discipline
Only implement gameplay and content explicitly approved by the project owner. Never claim a game screenshot, build, or test exists unless the corresponding artifact or run has been verified.
