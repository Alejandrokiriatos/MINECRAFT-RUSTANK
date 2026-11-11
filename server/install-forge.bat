@echo off
setlocal
cd /d "%~dp0"

set "FORGE_VERSION=26.1.2-64.1.5"
set "INSTALLER=forge-%FORGE_VERSION%-installer.jar"
set "URL=https://maven.minecraftforge.net/net/minecraftforge/forge/%FORGE_VERSION%/%INSTALLER%"

where java >nul 2>nul
if errorlevel 1 (
  echo ERROR: Java 25 is required but was not found in PATH.
  pause
  exit /b 1
)

echo Downloading official Forge installer %FORGE_VERSION%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%URL%' -OutFile '%INSTALLER%'"
if errorlevel 1 (
  echo ERROR: Could not download Forge installer.
  pause
  exit /b 1
)

java -jar "%INSTALLER%" --installServer
if errorlevel 1 (
  echo ERROR: Forge server installation failed.
  pause
  exit /b 1
)

del "%INSTALLER%"
echo.
echo Forge server files installed. Copy the Rustank mod JAR into the server mods folder.
echo Review eula.txt and server.properties before starting.
pause
