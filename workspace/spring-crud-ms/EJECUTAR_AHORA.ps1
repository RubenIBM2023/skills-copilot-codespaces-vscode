#!/usr/bin/env pwsh
# SOLUCIÓN DEFINITIVA - Recompila, inicia diagnóstico y prueba completa

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║      SOLUCIÓN DEFINITIVA - GUARDAR PRODUCTOS              ║" -ForegroundColor Magenta
Write-Host "║    Pasos automáticos para resolver el problema            ║" -ForegroundColor Magenta
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Magenta
Write-Host ""

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$baseUrl = "http://localhost:8080"

Write-Host "📁 Carpeta: $scriptDir" -ForegroundColor Cyan
Write-Host ""

# PASO 1: Limpiar y compilar
Write-Host "📦 PASO 1: Compilando la aplicación..." -ForegroundColor Yellow
Write-Host "   ⏱️  Esto puede tomar ~2 minutos..." -ForegroundColor Cyan
Write-Host ""

cd $scriptDir

# Limpiar
if (Test-Path "target") {
    Write-Host "   🧹 Limpiando archivos viejos..." -ForegroundColor Gray
    Remove-Item "target" -Recurse -Force -ErrorAction SilentlyContinue | Out-Null
}

# Compilar
$output = mvn clean package -q -DskipTests 2>&1
$success = $LASTEXITCODE -eq 0

if ($success) {
    Write-Host "   ✅ Compilación exitosa" -ForegroundColor Green
} else {
    Write-Host "   ❌ Error en compilación" -ForegroundColor Red
    Write-Host "   Intenta con más detalles: mvn clean package -DskipTests" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# PASO 2: Iniciar la aplicación
Write-Host "🚀 PASO 2: Iniciando la aplicación..." -ForegroundColor Yellow
Write-Host ""
Write-Host "   🌐 Se abrirá navegador en: $baseUrl" -ForegroundColor Cyan
Write-Host ""

# Abrir navegador
Start-Process $baseUrl -ErrorAction SilentlyContinue

Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""

# Iniciar aplicación
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
