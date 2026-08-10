#!/usr/bin/env pwsh
# Script de Diagnóstico - Verifica la aplicación paso a paso

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║           DIAGNÓSTICO COMPLETO - GUARDAR PRODUCTOS            ║" -ForegroundColor Magenta
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Magenta
Write-Host ""

$baseUrl = "http://localhost:8080"
$apiUrl = "$baseUrl/api/v1/products"
$healthUrl = "$baseUrl/api/v1/health"

# PASO 1: Verificar que el servidor está corriendo
Write-Host "1️⃣  VERIFICANDO QUE EL SERVIDOR ESTÁ CORRIENDO..." -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri $healthUrl -TimeoutSec 5
    Write-Host "✅ Servidor CORRIENDO" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode) $($response.StatusDescription)" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "❌ SERVIDOR NO ESTÁ CORRIENDO" -ForegroundColor Red
    Write-Host ""
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "⚠️  SOLUCIÓN:" -ForegroundColor Yellow
    Write-Host "   1. Abre una terminal NUEVA en la carpeta: workspace\spring-crud-ms" -ForegroundColor White
    Write-Host "   2. Ejecuta: mvn clean package -q -DskipTests" -ForegroundColor White
    Write-Host "   3. Luego: java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar" -ForegroundColor White
    Write-Host "   4. Espera a que veas: 'Tomcat started on port(s): 8080'" -ForegroundColor White
    Write-Host ""
    Read-Host "Presiona Enter después de iniciar el servidor"
    exit 1
}

# PASO 2: Verificar que la API de productos responde
Write-Host "2️⃣  VERIFICANDO QUE LA API RESPONDE..." -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri $apiUrl -TimeoutSec 5
    Write-Host "✅ API RESPONDE" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "❌ API NO RESPONDE" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    exit 1
}

# PASO 3: Intentar CREAR un producto
Write-Host "3️⃣  INTENTANDO CREAR UN PRODUCTO..." -ForegroundColor Yellow
Write-Host ""

$testProduct = @{
    name = "Test Producto $(Get-Date -Format 'HH:mm:ss')"
    description = "Producto de prueba para diagnóstico"
    price = 99.99
    stock = 5
    category = "Electronics"
} | ConvertTo-Json

Write-Host "📤 Datos enviados:" -ForegroundColor Cyan
Write-Host $testProduct -ForegroundColor Gray
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri $apiUrl `
        -Method POST `
        -ContentType "application/json" `
        -Body $testProduct `
        -TimeoutSec 10

    Write-Host "✅ PRODUCTO CREADO EXITOSAMENTE" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode) Created" -ForegroundColor Green
    Write-Host ""
    
    $data = $response.Content | ConvertFrom-Json
    Write-Host "📥 Respuesta:" -ForegroundColor Cyan
    Write-Host "   ID: $($data.id)" -ForegroundColor Green
    Write-Host "   Nombre: $($data.name)" -ForegroundColor Green
    Write-Host "   Precio: \$$($data.price)" -ForegroundColor Green
    Write-Host "   Stock: $($data.stock)" -ForegroundColor Green
    Write-Host ""
    Write-Host "🎉 ¡LA API FUNCIONA CORRECTAMENTE!" -ForegroundColor Green
    
} catch {
    Write-Host "❌ ERROR AL CREAR PRODUCTO" -ForegroundColor Red
    Write-Host ""
    
    $errorMsg = $_.Exception.Message
    Write-Host "📋 Error: $errorMsg" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        try {
            $streamReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $errorBody = $streamReader.ReadToEnd()
            $streamReader.Close()
            
            Write-Host ""
            Write-Host "📄 Detalles del error:" -ForegroundColor Yellow
            Write-Host $errorBody -ForegroundColor Gray
        } catch {}
    }
    
    Write-Host ""
    Write-Host "⚠️  PRÓXIMOS PASOS:" -ForegroundColor Yellow
    Write-Host "   1. Abre el navegador: $baseUrl" -ForegroundColor White
    Write-Host "   2. Presiona F12 (consola del navegador)" -ForegroundColor White
    Write-Host "   3. Intenta guardar un producto en la interfaz" -ForegroundColor White
    Write-Host "   4. Copia TODO lo que ves en la consola (pestaña Console)" -ForegroundColor White
    Write-Host "   5. Comparte ese mensaje conmigo" -ForegroundColor White
    Write-Host ""
    exit 1
}

# PASO 4: Intentar LISTAR productos
Write-Host "4️⃣  LISTANDO PRODUCTOS..." -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri "$apiUrl?page=0&size=10" -TimeoutSec 5
    Write-Host "✅ LISTA CARGADA" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Green
    
    $data = $response.Content | ConvertFrom-Json
    $productCount = $data.content.Count
    
    Write-Host "   Total de productos: $productCount" -ForegroundColor Green
    Write-Host ""
    
} catch {
    Write-Host "❌ ERROR AL LISTAR" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

# PASO 5: Instrucciones finales
Write-Host ""
Write-Host "✅ DIAGNÓSTICO COMPLETADO" -ForegroundColor Green
Write-Host ""
Write-Host "═══════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "🎯 LA API FUNCIONA CORRECTAMENTE" -ForegroundColor Green
Write-Host ""
Write-Host "¿Por qué podrías no ver productos en la GUI?" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. F12 en navegador → Console" -ForegroundColor White
Write-Host "   • ¿Ves errores en rojo?" -ForegroundColor Gray
Write-Host "   • ¿Qué dice exactamente?" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Refreshea la página: Ctrl+F5" -ForegroundColor White
Write-Host ""
Write-Host "3. Abre: $baseUrl" -ForegroundColor White
Write-Host ""
Write-Host "4. Intenta guardar un producto" -ForegroundColor White
Write-Host ""
Write-Host "═══════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
