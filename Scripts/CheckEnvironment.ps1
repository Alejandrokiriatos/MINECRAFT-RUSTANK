$ErrorActionPreference = "Stop"

Write-Host "Checking Windows Unreal development prerequisites..." -ForegroundColor Cyan

if (-not $env:UE_ROOT) {
    throw "UE_ROOT is not set. Set it to the installed Unreal Engine root."
}
$editor = Join-Path $env:UE_ROOT "Engine\Binaries\Win64\UnrealEditor-Cmd.exe"
$uat = Join-Path $env:UE_ROOT "Engine\Build\BatchFiles\RunUAT.bat"
$versionFile = Join-Path $env:UE_ROOT "Engine\Build\Build.version"

foreach ($path in @($editor, $uat, $versionFile)) {
    if (-not (Test-Path $path)) { throw "Required Unreal file not found: $path" }
    Write-Host "OK: $path" -ForegroundColor Green
}

$version = Get-Content $versionFile -Raw | ConvertFrom-Json
Write-Host ("Detected Unreal Engine version: {0}.{1}.{2} (changelist {3})" -f $version.MajorVersion, $version.MinorVersion, $version.PatchVersion, $version.Changelist) -ForegroundColor Cyan

$vswhere = Join-Path $env:ProgramFiles(x86) "Microsoft Visual Studio\Installer\vswhere.exe"
if (Test-Path $vswhere) {
    $vs = & $vswhere -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath
    if (-not $vs) { throw "Visual Studio C++ toolset was not detected." }
    Write-Host "OK: Visual Studio C++ toolset at $vs" -ForegroundColor Green
} else {
    Write-Warning "vswhere.exe not found; verify Visual Studio 2022 C++ workload manually."
}

$git = Get-Command git -ErrorAction SilentlyContinue
if (-not $git) { throw "Git is not installed or not on PATH." }
Write-Host "OK: Git found at $($git.Source)" -ForegroundColor Green

$lfs = & git lfs version 2>$null
if ($LASTEXITCODE -ne 0) { throw "Git LFS is not installed." }
Write-Host "OK: $lfs" -ForegroundColor Green

Write-Host "Environment checks passed. This check does not compile or launch the game." -ForegroundColor Green
