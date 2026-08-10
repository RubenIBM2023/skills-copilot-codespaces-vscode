@echo off
REM Script para abrir la interfaz gráfica automáticamente
REM Crea un servidor HTTP local y abre el navegador

setlocal enabledelayedexpansion

echo.
echo ================================
echo  Abriendo Interfaz Gráfica GUI
echo  CRUD Dashboard - Productos
echo ================================
echo.

REM Verificar que Python está instalado
python --version >nul 2>&1
if errorlevel 1 (
    echo.
    echo ERROR: Python no está instalado o no está en PATH
    echo.
    echo Soluciones:
    echo 1. Descarga Python desde: https://www.python.org
    echo 2. Instala con: "Add Python to PATH" activado
    echo 3. Vuelve a ejecutar este script
    echo.
    pause
    exit /b 1
)

echo Iniciando servidor HTTP en puerto 8000...
echo.
echo Servidor disponible en:
echo   http://localhost:8000/index.html
echo.
echo Presiona Ctrl+C para detener el servidor
echo.

REM Esperar un segundo para que el servidor inicie
timeout /t 1 /nobreak

REM Abrir el navegador
start http://localhost:8000/index.html

REM Iniciar servidor Python
python -m http.server 8000
