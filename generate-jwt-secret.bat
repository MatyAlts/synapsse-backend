@echo off
REM Script para generar un JWT_SECRET seguro

echo ========================================
echo Generador de JWT_SECRET
echo ========================================
echo.

REM Generar un UUID aleatorio y codificarlo en Base64
powershell -Command "[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes((New-Guid).ToString() + (New-Guid).ToString() + (Get-Date).Ticks))"

echo.
echo ========================================
echo Copia el texto de arriba y usalo como
echo JWT_SECRET en las variables de entorno
echo de Render.
echo ========================================
echo.
pause
