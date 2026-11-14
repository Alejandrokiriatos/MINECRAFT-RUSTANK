# Initial architecture

## Technology baseline
- Language: C++.
- Engine: Unreal Engine 5 (exact minor version pending local/toolchain confirmation).
- Initial module: `SurvivalGame`.
- Targets: game runtime and editor.
- Primary target platform: Windows PC.

## Boundaries
- Engine-facing code belongs in the Unreal module.
- Gameplay systems must be separated into focused modules/classes as they are approved and introduced.
- Server-authoritative multiplayer is the intended design direction; detailed network contracts are not yet defined.
- Large Unreal binary assets are tracked with Git LFS.
- Generated engine output is excluded from Git.

## Deliberately not implemented
No player controller, movement model, inventory, crafting, weapons, building, raiding, NPCs, map, terrain, audio, UI, monetization, or networking behavior is included in this bootstrap. These require explicit scope decisions and tests.

## Screenshot/test boundary
A screenshot from the optional Windows runner proves only that the built application launched and a desktop frame was captured. It does not prove gameplay systems, multiplayer, or performance.
