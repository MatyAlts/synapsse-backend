# Script para generar un JWT_SECRET seguro

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Generador de JWT_SECRET" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$guid1 = [Guid]::NewGuid().ToString()
$guid2 = [Guid]::NewGuid().ToString()
$ticks = (Get-Date).Ticks
$combined = "$guid1$guid2$ticks"
$bytes = [Text.Encoding]::UTF8.GetBytes($combined)
$secret = [Convert]::ToBase64String($bytes)

Write-Host "Tu JWT_SECRET:" -ForegroundColor Green
Write-Host $secret -ForegroundColor Yellow
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Copia el texto de arriba y usalo como" -ForegroundColor White
Write-Host "JWT_SECRET en las variables de entorno" -ForegroundColor White
Write-Host "de Render." -ForegroundColor White
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Copiar al clipboard si es posible
try {
    Set-Clipboard -Value $secret
    Write-Host "✓ Copiado al portapapeles!" -ForegroundColor Green
} catch {
    Write-Host "⚠ No se pudo copiar al portapapeles automaticamente" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Presiona Enter para salir"
