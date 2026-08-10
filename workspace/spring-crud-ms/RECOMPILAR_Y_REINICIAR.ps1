#!/usr/bin/env pwsh
# Script para recompilar y reiniciar la aplicación
# Soluciona problemas de JAR bloqueado

Write-Host ""
Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║      RECOMPILAR Y REINICIAR           ║" -ForegroundColor Magenta
Write-Host "║    Solución para Guardar Productos    ║" -ForegroundColor Magenta
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Magenta
Write-Host ""

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "📁 Directorio: $scriptDir" -ForegroundColor Cyan
Write-Host ""

# Paso 1: Limpiar
Write-Host "1️⃣  Limpiando archivos compilados..." -ForegroundColor Yellow
if (Test-Path "$scriptDir\target") {
    Remove-Item "$scriptDir\target" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "   ✅ Carpeta 'target' eliminada" -ForegroundColor Green
}
Write-Host ""

# Paso 2: Compilar
Write-Host "2️⃣  Recompilando proyecto..." -ForegroundColor Yellow
Write-Host "   ⏱️  Esto puede tomar ~90 segundos..." -ForegroundColor Cyan
Write-Host ""

cd $scriptDir
$compileOutput = mvn clean package -q -DskipTests 2>&1
$lastLine = $compileOutput | Select-Object -Last 1

if ($lastLine -match "BUILD SUCCESS") {
    Write-Host "   ✅ Compilación exitosa" -ForegroundColor Green
    Write-Host "   ✅ JAR creado correctamente" -ForegroundColor Green
} else {
    Write-Host "   ❌ Error en compilación" -ForegroundColor Red
    Write-Host ""
    Write-Host "Detalles:" -ForegroundColor Yellow
    $compileOutput | Select-Object -Last 10
    Write-Host ""
    Write-Host "Intenta ejecutar con detalles: mvn clean package" -ForegroundColor Yellow
    Read-Host "Presiona Enter para salir"
    exit 1
}

Write-Host ""

# Paso 3: Iniciar
Write-Host "3️⃣  Iniciando aplicación Spring Boot..." -ForegroundColor Yellow
Write-Host "   🌐 Abriendo navegador en http://localhost:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""

# Abrir navegador
Start-Process "http://localhost:8080" -ErrorAction SilentlyContinue

# Ejecutar
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""
Write-Host "⛔ Servidor detenido" -ForegroundColor Yellow
Write-Host ""
