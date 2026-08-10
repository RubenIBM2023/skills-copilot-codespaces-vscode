#!/usr/bin/env pwsh
# Script para diagnosticar el error "failed to fetch"

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║          DIAGNÓSTICO - ERROR 'FAILED TO FETCH'                ║" -ForegroundColor Magenta
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Magenta
Write-Host ""

$baseUrl = "http://localhost:8080"
$healthUrl = "$baseUrl/api/v1/health"
$productsUrl = "$baseUrl/api/v1/products"

# PASO 1: ¿Está corriendo el servidor?
Write-Host "1️⃣  VERIFICANDO QUE EL SERVIDOR ESTÁ CORRIENDO..." -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri $healthUrl -TimeoutSec 3 -SkipHttpErrorCheck
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ SERVIDOR CORRIENDO" -ForegroundColor Green
        Write-Host "   Status Code: $($response.StatusCode)" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Servidor responde pero con error" -ForegroundColor Yellow
        Write-Host "   Status Code: $($response.StatusCode)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ SERVIDOR NO ESTÁ CORRIENDO" -ForegroundColor Red
    Write-Host ""
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "⚠️  SOLUCIÓN:" -ForegroundColor Yellow
    Write-Host "   1. Abre PowerShell NUEVA" -ForegroundColor White
    Write-Host "   2. Navega a: workspace\spring-crud-ms" -ForegroundColor White
    Write-Host "   3. Ejecuta: mvn clean package -q -DskipTests" -ForegroundColor White
    Write-Host "   4. Luego: java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar" -ForegroundColor White
    Write-Host ""
    exit 1
}

Write-Host ""

# PASO 2: Verificar CORS
Write-Host "2️⃣  VERIFICANDO CORS (Permisos de navegador)..." -ForegroundColor Yellow
Write-Host ""

try {
    $headers = @{
        'Origin' = 'http://localhost:8080'
        'Access-Control-Request-Method' = 'POST'
        'Access-Control-Request-Headers' = 'content-type'
    }
    
    $response = Invoke-WebRequest -Uri $productsUrl `
        -Method OPTIONS `
        -Headers $headers `
        -TimeoutSec 3 `
        -SkipHttpErrorCheck

    Write-Host "✅ CORS PERMITIDO" -ForegroundColor Green
    Write-Host "   Access-Control-Allow-Origin: $($response.Headers['Access-Control-Allow-Origin'])" -ForegroundColor Green
    Write-Host "   Access-Control-Allow-Methods: $($response.Headers['Access-Control-Allow-Methods'])" -ForegroundColor Green
    
} catch {
    Write-Host "⚠️  Error en verificación CORS" -ForegroundColor Yellow
    Write-Host "   Esto podría causar 'failed to fetch'" -ForegroundColor Yellow
}

Write-Host ""

# PASO 3: Intentar GET (Listar productos)
Write-Host "3️⃣  INTENTANDO GET (Listar productos)..." -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri $productsUrl -TimeoutSec 3
    Write-Host "✅ GET FUNCIONA" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Green
    
} catch {
    Write-Host "❌ GET FALLÓ" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# PASO 4: Intentar POST (Crear producto)
Write-Host "4️⃣  INTENTANDO POST (Crear producto)..." -ForegroundColor Yellow
Write-Host ""

$testProduct = @{
    name = "Test Diagnóstico $(Get-Date -Format 'HH:mm:ss')"
    description = "Prueba"
    price = 99.99
    stock = 5
    category = "Electronics"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $productsUrl `
        -Method POST `
        -ContentType "application/json" `
        -Body $testProduct `
        -TimeoutSec 3

    Write-Host "✅ POST FUNCIONA" -ForegroundColor Green
    Write-Host "   Status: $($response.StatusCode) Created" -ForegroundColor Green
    
    $data = $response.Content | ConvertFrom-Json
    Write-Host "   ID del producto: $($data.id)" -ForegroundColor Green
    Write-Host ""
    Write-Host "🎉 ¡LA API FUNCIONA CORRECTAMENTE!" -ForegroundColor Green
    Write-Host ""
    Write-Host "El problema es probablemente:" -ForegroundColor Yellow
    Write-Host "  1. Navegador no actualizado - Recarga con Ctrl+F5" -ForegroundColor White
    Write-Host "  2. Cache del navegador - Borra historial/cookies" -ForegroundColor White
    Write-Host "  3. Navegador antiguo - Usa Chrome/Firefox/Edge moderno" -ForegroundColor White
    Write-Host ""
    
} catch {
    Write-Host "❌ POST FALLÓ" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        try {
            $errorStream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($errorStream)
            $errorBody = $reader.ReadToEnd()
            $reader.Close()
            
            Write-Host ""
            Write-Host "📄 Detalles del error:" -ForegroundColor Yellow
            Write-Host $errorBody -ForegroundColor Gray
        } catch {}
    }
}

Write-Host ""
Write-Host "═══════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "📝 PRÓXIMOS PASOS:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Si la API funciona pero el navegador no:" -ForegroundColor White
Write-Host "   • Abre navegador en: $baseUrl" -ForegroundColor Cyan
Write-Host "   • Presiona: Ctrl+F5 (reload sin cache)" -ForegroundColor Cyan
Write-Host "   • Intenta guardar de nuevo" -ForegroundColor Cyan
Write-Host ""
Write-Host "2. Si quieres ver los logs de la aplicación:" -ForegroundColor White
Write-Host "   • Terminal donde corre java muestra los logs" -ForegroundColor Cyan
Write-Host "   • Busca líneas con ERROR o WARN" -ForegroundColor Cyan
Write-Host ""
Write-Host "═══════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
