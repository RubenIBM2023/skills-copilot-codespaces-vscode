@echo off
REM Script de Pruebas Automatizadas para Spring CRUD Microservices
REM REQ-2026-08-08-001-spring-crud-ms
REM ============================================================

setlocal enabledelayedexpansion
REM Configuración
set BASE_URL=http://localhost:8080
set API_BASE=%BASE_URL%/api/v1/products
set LOG_FILE=pruebas_resultado.log
set CREATED_IDS=

REM Limpiar log anterior
if exist %LOG_FILE% del %LOG_FILE%

REM Colores (simulados con echo)
cls
echo.
echo ========================================================
echo   PRUEBAS AUTOMATIZADAS - Spring CRUD Microservices
echo   REQ-2026-08-08-001-spring-crud-ms
echo ========================================================
echo.
echo Iniciando pruebas en: %API_BASE%
echo Resultado guardado en: %LOG_FILE%
echo.

REM ========================================================
REM 1. VERIFICAR CONECTIVIDAD
REM ========================================================
echo.
echo [TEST 1] Verificando conectividad con servidor...
echo [TEST 1] Verificando conectividad con servidor... >> %LOG_FILE%

curl -s -o nul -w "HTTP Status: %%{http_code}\n" %BASE_URL%/swagger-ui.html >> %LOG_FILE%

if errorlevel 1 (
    echo ERROR: No se puede conectar al servidor en %BASE_URL%
    echo ERROR: No se puede conectar al servidor en %BASE_URL% >> %LOG_FILE%
    pause
    exit /b 1
)

echo Conexion exitosa!
echo Conexion exitosa! >> %LOG_FILE%

REM ========================================================
REM 2. PRUEBA: Crear Producto 1 (Laptop)
REM ========================================================
echo.
echo [TEST 2] Creando Producto 1: Laptop...
echo [TEST 2] Creando Producto 1: Laptop... >> %LOG_FILE%

curl -s -X POST %API_BASE% ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Laptop Dell XPS 13\",\"description\":\"Laptop ultraportable\",\"price\":1299.99,\"stock\":5,\"category\":\"Electronics\"}" ^
  >> %LOG_FILE%

echo.
echo Respuesta guardada en log

REM ========================================================
REM 3. PRUEBA: Crear Producto 2 (Smartphone)
REM ========================================================
echo.
echo [TEST 3] Creando Producto 2: Smartphone...
echo [TEST 3] Creando Producto 2: Smartphone... >> %LOG_FILE%

curl -s -X POST %API_BASE% ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"iPhone 15 Pro\",\"description\":\"Smartphone flagship\",\"price\":999.99,\"stock\":15,\"category\":\"Electronics\"}" ^
  >> %LOG_FILE%

REM ========================================================
REM 4. PRUEBA: Crear Producto 3 (T-Shirt)
REM ========================================================
echo.
echo [TEST 4] Creando Producto 3: T-Shirt...
echo [TEST 4] Creando Producto 3: T-Shirt... >> %LOG_FILE%

curl -s -X POST %API_BASE% ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"T-Shirt Basica\",\"description\":\"Camiseta de algodon\",\"price\":24.99,\"stock\":100,\"category\":\"Clothing\"}" ^
  >> %LOG_FILE%

REM ========================================================
REM 5. PRUEBA: Obtener Todos los Productos
REM ========================================================
echo.
echo [TEST 5] Obteniendo lista de todos los productos...
echo [TEST 5] Obteniendo lista de todos los productos... >> %LOG_FILE%

curl -s %API_BASE% >> %LOG_FILE%

REM ========================================================
REM 6. PRUEBA: Obtener con Paginacion
REM ========================================================
echo.
echo [TEST 6] Obteniendo productos con paginacion (page=0, size=10)...
echo [TEST 6] Obteniendo productos con paginacion (page=0, size=10)... >> %LOG_FILE%

curl -s "%API_BASE%?page=0&size=10" >> %LOG_FILE%

REM ========================================================
REM 7. PRUEBA: Validación - Nombre Vacio
REM ========================================================
echo.
echo [TEST 7] Prueba Validacion: Nombre vacio (debe fallar con 400)...
echo [TEST 7] Prueba Validacion: Nombre vacio (debe fallar con 400)... >> %LOG_FILE%

curl -s -X POST %API_BASE% ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"\",\"price\":100.00,\"stock\":10,\"category\":\"Test\"}" ^
  >> %LOG_FILE%

REM ========================================================
REM 8. PRUEBA: Validación - Precio Negativo
REM ========================================================
echo.
echo [TEST 8] Prueba Validacion: Precio negativo (debe fallar con 400)...
echo [TEST 8] Prueba Validacion: Precio negativo (debe fallar con 400)... >> %LOG_FILE%

curl -s -X POST %API_BASE% ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test\",\"price\":-50.00,\"stock\":10,\"category\":\"Test\"}" ^
  >> %LOG_FILE%

REM ========================================================
REM 9. PRUEBA: Validación - Stock Negativo
REM ========================================================
echo.
echo [TEST 9] Prueba Validacion: Stock negativo (debe fallar con 400)...
echo [TEST 9] Prueba Validacion: Stock negativo (debe fallar con 400)... >> %LOG_FILE%

curl -s -X POST %API_BASE% ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test\",\"price\":100.00,\"stock\":-5,\"category\":\"Test\"}" ^
  >> %LOG_FILE%

REM ========================================================
REM 10. PRUEBA: ID No Existe (404)
REM ========================================================
echo.
echo [TEST 10] Prueba Error: Obtener producto inexistente (debe fallar con 404)...
echo [TEST 10] Prueba Error: Obtener producto inexistente (debe fallar con 404)... >> %LOG_FILE%

curl -s %API_BASE%/invalid_id_000000000000 >> %LOG_FILE%

REM ========================================================
REM 11. PRUEBA: Swagger UI
REM ========================================================
echo.
echo [TEST 11] Verificando Swagger UI...
echo [TEST 11] Verificando Swagger UI... >> %LOG_FILE%

curl -s -o nul -w "HTTP Status: %%{http_code}\n" %BASE_URL%/swagger-ui.html >> %LOG_FILE%

REM ========================================================
REM RESUMEN
REM ========================================================
echo.
echo ========================================================
echo   PRUEBAS COMPLETADAS
echo ========================================================
echo.
echo Resultados guardados en: %LOG_FILE%
echo.
echo Para ver los resultados completos:
echo   type %LOG_FILE%
echo.
echo PROXIMOS PASOS:
echo 1. Abrir Postman
echo 2. Importar coleccion: workspace\spring-crud-ms\Postman_Collection.json
echo 3. Ejecutar pruebas interactivas desde Swagger UI:
echo    %BASE_URL%/swagger-ui.html
echo.
pause
