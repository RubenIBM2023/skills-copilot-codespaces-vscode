#!/usr/bin/env pwsh
# Script para iniciar el servidor Spring Boot

Set-Location "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Iniciando Microservicio Product CRUD" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Compilar y ejecutar con Maven
Write-Host "Compilando el proyecto..." -ForegroundColor Yellow
mvn clean package -DskipTests -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error en la compilación" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Compilación exitosa" -ForegroundColor Green
Write-Host ""
Write-Host "Iniciando servidor..." -ForegroundColor Yellow
Write-Host "Accede a: http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
Write-Host ""

# Ejecutar la aplicación
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
