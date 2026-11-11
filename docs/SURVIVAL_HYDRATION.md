# Survival system: hydration

## Implemented core

The first survival-domain component is `com.rustank.survival.HydrationState`.

- Hydration is represented on a 0–100 scale.
- Normal activity drains one point every 1,200 server ticks (60 seconds at 20 TPS).
- Sprinting drains one point every 600 server ticks (30 seconds at 20 TPS).
- Drinking restores a configurable amount and never exceeds 100.
- Loading persisted values clamps them to the valid range.
- Creative and spectator players do not drain hydration.
- The model has no client or Minecraft dependencies, so its rules remain independent of HUD rendering and player networking.

## Automated verification

`HydrationStateTest` exercises normal and sprint drain intervals, creative/spectator exclusion, drink clamping, persisted-value clamping, and the zero-hydration floor. The GitHub Actions build runs these tests as part of `gradle build`.

## Not yet wired into gameplay

This is the domain model, not yet the complete player feature. The next integration work is to attach it to server-side player lifecycle/tick events, persist it with player data, synchronize values to clients, and render the HUD. Dehydration penalties will be added only after those foundations are in place.

## Initial balance values

These are development defaults and will be tuned during in-game tests:
- Normal: 1 hydration/minute.
- Sprinting: 2 hydration/minute.
- Maximum hydration: 100.
