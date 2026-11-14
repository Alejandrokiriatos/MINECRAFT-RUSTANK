# Development roadmap

## Phase 1 — development foundation
Status: **in progress**

- [x] Clean repository baseline.
- [x] Unreal C++ project descriptor and minimal runtime/editor targets.
- [x] Git ignore rules and Git LFS tracking for common Unreal binary assets.
- [x] Project documentation and decision log.
- [x] Hosted validation workflow.
- [x] Optional self-hosted Windows Unreal build-and-screenshot workflow.
- [ ] Select and record the exact Unreal Engine 5 version.
- [ ] Confirm a Windows build machine with Unreal Engine installed.
- [ ] Run the first real editor build and attach its log.\n- [x] Hosted GitHub validation passed on commit c897abcc6142ade71c56d1ab36bcd8980d785b28 (repository checks only).\n- [x] Added Windows runner setup instructions and a local prerequisite-check script.
- [ ] Launch the packaged application and capture a verified screenshot.
- [ ] Review the screenshot and build output before closing Phase 1.

**Exit gate:** project opens in the selected UE version, editor target compiles, packaged game launches on Windows, and a screenshot artifact is available from a successful workflow run.

## Phase 2 — core playable foundation
Not started. Define exact player, camera, input, movement, and test-map requirements with the owner before implementation.

## Phase 3 — world foundation
Not started. Define world dimensions, boundaries, terrain approach, streaming, and performance budgets before implementation.

## Phase 4 — survival and interaction systems
Not started. Implement only approved survival and interaction requirements.

## Phase 5 — multiplayer and persistence
Not started. Define authority model, session model, replication, persistence, and server deployment before implementation.

## Phase 6 — content, optimization and testable release
Not started. Use verified, properly licensed community assets only when approved; profile and test on target hardware.

Progress is measured against verified checklist items and successful test evidence, not estimated effort.
