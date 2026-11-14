# Technical decisions

| Topic | Decision | Status |
|---|---|---|
| Product | Standalone PC survival game, not a Minecraft mod/server | Confirmed |
| Language | C++ | Selected |
| Engine | Unreal Engine 5 | Selected; minor version pending |
| Source control | GitHub | Active |
| Large binary assets | Git LFS | Configured |
| Multiplayer direction | Server-authoritative | Direction selected; protocol/design pending |
| CI | GitHub Actions hosted validation plus optional self-hosted Windows UE runner | Configured; runner not yet verified |
| Screenshot proof | Launch packaged build on interactive Windows runner and upload PNG artifact | Workflow scaffolded; not yet executed |

## Version record
Record the exact UE version, Visual Studio version, Windows SDK, compiler toolset, and target configuration here after checking the actual build machine. Do not infer these from a workflow label.
