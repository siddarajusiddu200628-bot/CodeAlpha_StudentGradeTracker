@echo off
title CodeAlpha - Student Grade Tracker
echo ========================================================
echo        CodeAlpha - Student Grade Tracker Launcher
echo ========================================================
echo.

if not exist "bin" mkdir "bin"

echo [1/2] Compiling Java source files...
javac -encoding UTF-8 -d bin src\com\codealpha\gradetracker\model\*.java src\com\codealpha\gradetracker\service\*.java src\com\codealpha\gradetracker\ui\*.java src\com\codealpha\gradetracker\*.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed. Please ensure JDK 8+ is installed and on your PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching Student Grade Tracker GUI...
start javaw -cp bin com.codealpha.gradetracker.Main
echo [SUCCESS] Application launched.
