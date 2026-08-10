#!/usr/bin/env pwsh
# Script para abrir la interfaz gráfica automáticamente
# Requisito: Python instalado

Write-Host ""
Write-Host "================================" -ForegroundColor Magenta
Write-Host " Abriendo Interfaz Gráfica GUI " -ForegroundColor Magenta
Write-Host " CRUD Dashboard - Productos    " -ForegroundColor Magenta
Write-Host "================================" -ForegroundColor Magenta
Write-Host ""

# Verificar Python
Write-Host "Verificando Python..." -ForegroundColor Cyan
try {
    $pythonVersion = python --version 2>&1
    Write-Host "✅ $pythonVersion" -ForegroundColor Green
}
catch {
    Write-Host "❌ ERROR: Python no está instalado" -ForegroundColor Red
    Write-Host ""
    Write-Host "Solución:" -ForegroundColor Yellow
    Write-Host "1. Descarga Python desde: https://www.python.org" -ForegroundColor White
    Write-Host "2. Instala con 'Add Python to PATH' activado" -ForegroundColor White
    Write-Host "3. Vuelve a ejecutar este script" -ForegroundColor White
    Write-Host ""
    Read-Host "Presiona Enter para salir"
    exit 1
}

# Iniciar servidor
Write-Host ""
Write-Host "Iniciando servidor HTTP en puerto 8000..." -ForegroundColor Cyan
Write-Host ""
Write-Host "🌐 Servidor disponible en:" -ForegroundColor Green
Write-Host "   http://localhost:8000/index.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "📝 Para detener el servidor: Presiona Ctrl+C" -ForegroundColor Yellow
Write-Host ""

# Esperar y abrir navegador
Start-Sleep -Seconds 1
Write-Host "Abriendo navegador..." -ForegroundColor Cyan
Start-Process "http://localhost:8000/index.html"

# Iniciar servidor Python
Write-Host ""
python -m http.server 8000
