@echo off
REM Script para iniciar Spring Boot y abrir la Interfaz Gráfica
REM Autor: OpenCode Agent
REM Fecha: 2026-08-10

setlocal enabledelayedexpansion

cls
echo.
echo ╔════════════════════════════════════════╗
echo ║  INICIADOR DE APLICACIÓN SPRING BOOT  ║
echo ║   CRUD Dashboard - Productos           ║
echo ╚════════════════════════════════════════╝
echo.

REM Obtener el directorio actual
set scriptDir=%~dp0
echo 📁 Directorio: %scriptDir%
echo.

REM Verificar que el JAR existe
if not exist "%scriptDir%target\spring-crud-ms-1.0.0-SNAPSHOT.jar" (
    echo ❌ ERROR: No se encontró el JAR compilado
    echo.
    echo 🔧 Solución: Compilar el proyecto primero
    echo.
    echo Ejecuta:
    echo   cd "%scriptDir%"
    echo   mvn clean package -q
    echo.
    pause
    exit /b 1
)

echo ✅ JAR encontrado
echo.

REM Verificar Java
echo Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ ERROR: Java no está instalado
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('java -version 2^>^&1') do (
    set javaVer=%%i
    goto :gotJavaVersion
)

:gotJavaVersion
echo ✅ %javaVer%
echo.

echo 🚀 Iniciando Spring Boot...
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.

REM Abrir navegador (sin esperar)
start http://localhost:8080

REM Esperar 2 segundos
timeout /t 2 /nobreak

REM Ejecutar el JAR
cd /d "%scriptDir%"
java -jar target\spring-crud-ms-1.0.0-SNAPSHOT.jar

echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
echo ⛔ Servidor detenido
echo.
