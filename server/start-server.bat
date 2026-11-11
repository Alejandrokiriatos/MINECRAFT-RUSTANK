@echo off
setlocal
cd /d "%~dp0"

if not exist "run.bat" (
  echo ERROR: Forge server is not installed. Run install-forge.bat first.
  pause
  exit /b 1
)

if not exist "eula.txt" (
  echo eula=false>eula.txt
  echo Created eula.txt. Read and accept the Minecraft EULA yourself before running again.
  pause
  exit /b 1
)

findstr /i /c:"eula=true" eula.txt >nul
if errorlevel 1 (
  echo EULA not accepted. Open eula.txt, read the terms, and set eula=true only if you agree.
  pause
  exit /b 1
)

call run.bat nogui
