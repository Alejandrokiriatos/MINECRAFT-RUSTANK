# Build and test strategy

## Current checks
The hosted validation job checks project descriptor JSON and required C++ bootstrap files. It does not compile Unreal Engine code.

## Real Unreal build
The optional workflow job requires a self-hosted Windows x64 runner labeled:
- `self-hosted`
- `Windows`
- `X64`
- `UnrealEngine`

The runner must have the chosen Unreal Engine installation, Visual Studio C++ workload, Windows SDK, Git LFS, and an interactive desktop session. Configure a protected runner dedicated to this repository; do not expose an unrestricted runner to untrusted pull requests.

## Screenshot capture
The manual workflow builds/cooks/stages a Windows package, launches it in windowed mode, captures the runner desktop to PNG, and uploads the screenshot and build logs as artifacts. The capture is a launch smoke-test, not evidence of completed gameplay. If the runner is headless, locked, or has no interactive desktop, capture may fail and must be reported as such.

## Acceptance evidence
- Workflow URL and commit SHA.
- Build job result and log.
- Packaged executable presence.
- Screenshot artifact from the same successful run.
- Any failure must include the exact failing step and relevant log excerpt.
