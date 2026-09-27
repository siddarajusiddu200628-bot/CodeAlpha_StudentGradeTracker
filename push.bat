@echo off
title Push to GitHub - CodeAlpha Student Grade Tracker
echo ========================================================
echo        Pushing to GitHub: CodeAlpha_StudentGradeTracker
echo ========================================================
echo.

git remote set-url origin https://github.com/siddarajusiddu200628-bot/CodeAlpha_StudentGradeTracker.git
echo Remote URL set to: https://github.com/siddarajusiddu200628-bot/CodeAlpha_StudentGradeTracker.git
echo.
echo Pushing branch 'main' to GitHub...
echo (If a GitHub login dialog appears, click 'Sign in with your browser')
echo.

git push -u origin main

echo.
if %ERRORLEVEL% equ 0 (
    echo ========================================================
    echo  [SUCCESS] Code successfully pushed to GitHub!
    echo ========================================================
) else (
    echo ========================================================
    echo  [NOTICE] Push finished with status %ERRORLEVEL%.
    echo ========================================================
)

pause
