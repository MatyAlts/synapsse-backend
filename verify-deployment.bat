@echo off
REM Script de verificación pre-despliegue

echo ==========================================
echo 🔍 Verificación Pre-Despliegue
echo ==========================================
echo.

set ERROR_COUNT=0

REM Verificar Dockerfile
echo [1/8] Verificando Dockerfile...
if exist Dockerfile (
    echo ✅ Dockerfile encontrado
) else (
    echo ❌ Dockerfile NO encontrado
    set /a ERROR_COUNT+=1
)
echo.

REM Verificar docker-compose.yml
echo [2/8] Verificando docker-compose.yml...
if exist docker-compose.yml (
    echo ✅ docker-compose.yml encontrado
) else (
    echo ❌ docker-compose.yml NO encontrado
    set /a ERROR_COUNT+=1
)
echo.

REM Verificar docker-compose.prod.yml
echo [3/8] Verificando docker-compose.prod.yml...
if exist docker-compose.prod.yml (
    echo ✅ docker-compose.prod.yml encontrado
) else (
    echo ❌ docker-compose.prod.yml NO encontrado
    set /a ERROR_COUNT+=1
)
echo.

REM Verificar .env.example
echo [4/8] Verificando .env.example...
if exist .env.example (
    echo ✅ .env.example encontrado
) else (
    echo ❌ .env.example NO encontrado
    set /a ERROR_COUNT+=1
)
echo.

REM Verificar .gitignore
echo [5/8] Verificando .gitignore...
if exist .gitignore (
    findstr /C:".env" .gitignore >nul
    if errorlevel 1 (
        echo ⚠️  .gitignore existe pero no incluye .env
        set /a ERROR_COUNT+=1
    ) else (
        echo ✅ .gitignore configurado correctamente
    )
) else (
    echo ❌ .gitignore NO encontrado
    set /a ERROR_COUNT+=1
)
echo.

REM Verificar pom.xml
echo [6/8] Verificando pom.xml...
if exist pom.xml (
    echo ✅ pom.xml encontrado
) else (
    echo ❌ pom.xml NO encontrado
    set /a ERROR_COUNT+=1
)
echo.

REM Verificar archivos de configuración Spring
echo [7/8] Verificando archivos de configuración Spring...
if exist src\main\resources\application.yml (
    echo ✅ application.yml encontrado
) else (
    echo ❌ application.yml NO encontrado
    set /a ERROR_COUNT+=1
)

if exist src\main\resources\application-production.yml (
    echo ✅ application-production.yml encontrado
) else (
    echo ⚠️  application-production.yml NO encontrado (recomendado)
)
echo.

REM Verificar que .env NO esté commiteado
echo [8/8] Verificando que .env no esté en Git...
git ls-files .env >nul 2>&1
if errorlevel 1 (
    echo ✅ .env NO está en Git (correcto)
) else (
    echo ❌ PELIGRO: .env está commiteado en Git!
    echo    Elimínalo con: git rm --cached .env
    set /a ERROR_COUNT+=1
)
echo.

REM Resumen
echo ==========================================
echo 📊 Resumen
echo ==========================================
if %ERROR_COUNT%==0 (
    echo ✅ Todos los checks pasaron correctamente
    echo ✅ Listo para desplegar en EasyPanel
) else (
    echo ❌ Se encontraron %ERROR_COUNT% problema(s^)
    echo ⚠️  Corrige los errores antes de desplegar
)
echo.

REM Checklist adicional
echo ==========================================
echo 📋 Checklist Manual
echo ==========================================
echo.
echo ¿Has completado lo siguiente?
echo.
echo [ ] Generaste JWT_SECRET con generate-jwt-secret.ps1
echo [ ] Creaste archivo .env desde .env.example
echo [ ] Configuraste DB_PASSWORD en .env
echo [ ] Probaste localmente con docker-compose
echo [ ] Pusheaste el código a GitHub
echo [ ] Tienes acceso a EasyPanel
echo [ ] Conoces el dominio que usarás (api.tudominio.com^)
echo.

echo ==========================================
echo 📚 Siguiente Paso
echo ==========================================
echo.
echo Si todos los checks pasaron, sigue la guía:
echo    backend\EASYPANEL_DEPLOYMENT.md
echo.
echo O el quick start:
echo    backend\QUICKSTART.md
echo.

pause
