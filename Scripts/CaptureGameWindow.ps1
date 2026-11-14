param(
    [Parameter(Mandatory = $true)][int]$ProcessId,
    [Parameter(Mandatory = $true)][string]$OutputPath,
    [int]$StartupTimeoutSeconds = 45
)
$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing
Add-Type @"
using System;
using System.Runtime.InteropServices;
public static class NativeMethods {
    [StructLayout(LayoutKind.Sequential)]
    public struct RECT { public int Left; public int Top; public int Right; public int Bottom; }
    [DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr hWnd, out RECT rect);
    [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr hWnd);
    [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
}
"@
$process = Get-Process -Id $ProcessId -ErrorAction Stop
$deadline = (Get-Date).AddSeconds($StartupTimeoutSeconds)
do {
    $process.Refresh()
    if ($process.HasExited) { throw "Game process exited before a window appeared." }
    $handle = $process.MainWindowHandle
    if ($handle -ne 0) { break }
    Start-Sleep -Milliseconds 500
} while ((Get-Date) -lt $deadline)
if ($handle -eq 0) { throw "No main game window appeared within $StartupTimeoutSeconds seconds." }
[void][NativeMethods]::ShowWindow($handle, 9)
[void][NativeMethods]::SetForegroundWindow($handle)
Start-Sleep -Seconds 2
$rect = New-Object NativeMethods+RECT
if (-not [NativeMethods]::GetWindowRect($handle, [ref]$rect)) { throw "Could not read the game window bounds." }
$width = $rect.Right - $rect.Left
$height = $rect.Bottom - $rect.Top
if ($width -lt 320 -or $height -lt 200) { throw "Game window bounds are invalid or too small: ${width}x${height}." }
$bitmap = New-Object System.Drawing.Bitmap $width, $height
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
try {
    $graphics.CopyFromScreen($rect.Left, $rect.Top, 0, 0, $bitmap.Size)
    $directory = Split-Path -Parent $OutputPath
    New-Item -ItemType Directory -Force -Path $directory | Out-Null
    $bitmap.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
}
finally { $graphics.Dispose(); $bitmap.Dispose() }
$file = Get-Item $OutputPath
if ($file.Length -lt 10240) { throw "Screenshot is unexpectedly small ($($file.Length) bytes); refusing to treat it as valid evidence." }
Write-Host "Captured game window: $OutputPath ($width x $height, $($file.Length) bytes)"
