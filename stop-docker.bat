@echo off
REM Script para detener y limpiar los contenedores Docker

echo ==========================================
echo 🛑 Deteniendo Synapsse Backend
echo ==========================================
echo.

echo 🛑 Deteniendo contenedores...
docker-compose down

echo.
echo ✅ Contenedores detenidos
echo.

set /p cleanup="¿Eliminar volúmenes de base de datos? (s/N): "
if /i "%cleanup%"=="s" (
    echo.
    echo 🗑️  Eliminando volúmenes...
    docker-compose down -v
    echo ✅ Volúmenes eliminados
) else (
    echo.
    echo ℹ️  Volúmenes de base de datos conservados
)

echo.
pause
