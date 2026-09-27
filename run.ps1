# CodeAlpha - Student Grade Tracker PowerShell Runner
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "       CodeAlpha - Student Grade Tracker Launcher" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "`n[1/2] Compiling Java source files..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Path "src" -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac -encoding UTF-8 -d "bin" $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[ERROR] Compilation failed. Please check JDK installation." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[2/2] Launching Student Grade Tracker GUI..." -ForegroundColor Green
Start-Process javaw -ArgumentList "-cp", "bin", "com.codealpha.gradetracker.Main"
Write-Host "[SUCCESS] Application launched successfully!`n" -ForegroundColor Cyan
