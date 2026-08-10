#!/usr/bin/env pwsh
# Script para iniciar Spring Boot y abrir la Interfaz Gráfica
# Autor: OpenCode Agent
# Fecha: 2026-08-10

Write-Host ""
Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║  INICIADOR DE APLICACIÓN SPRING BOOT  ║" -ForegroundColor Magenta
Write-Host "║   CRUD Dashboard - Productos           ║" -ForegroundColor Magenta
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Magenta
Write-Host ""

# Obtener el directorio actual
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Write-Host "📁 Directorio de trabajo: $scriptDir" -ForegroundColor Cyan
Write-Host ""

# Verificar que el JAR existe
$jarPath = "$scriptDir\target\spring-crud-ms-1.0.0-SNAPSHOT.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "❌ ERROR: No se encontró el JAR compilado" -ForegroundColor Red
    Write-Host ""
    Write-Host "🔧 Solución: Compilar el proyecto primero" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Ejecuta:" -ForegroundColor White
    Write-Host "  cd '$scriptDir'" -ForegroundColor Cyan
    Write-Host "  mvn clean package -q" -ForegroundColor Cyan
    Write-Host ""
    Read-Host "Presiona Enter para salir"
    exit 1
}

Write-Host "✅ JAR encontrado: spring-crud-ms-1.0.0-SNAPSHOT.jar" -ForegroundColor Green
Write-Host ""

# Verificar Java
Write-Host "Verificando Java..." -ForegroundColor Cyan
try {
    $javaVersion = java -version 2>&1
    if ($javaVersion -match "17") {
        Write-Host "✅ Java 17 detectado" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Versión: $($javaVersion[0])" -ForegroundColor Yellow
    }
}
catch {
    Write-Host "❌ ERROR: Java no está instalado" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🚀 Iniciando Spring Boot..." -ForegroundColor Cyan
Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""

# Esperar 3 segundos y luego abrir el navegador
Start-Process "http://localhost:8080" -ErrorAction SilentlyContinue

# Ejecutar el JAR
cd $scriptDir
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""
Write-Host "⛔ Servidor detenido" -ForegroundColor Yellow
Write-Host ""
