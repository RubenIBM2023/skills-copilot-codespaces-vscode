# 🔍 DIAGNÓSTICO - ERROR 500

Tenemos un **Error 500 (Internal Server Error)**, lo que significa:
- ✅ El servidor SÍ está corriendo
- ✅ El navegador SÍ se conecta
- ❌ Pero algo falla en el servidor

## 📋 INFORMACIÓN NECESARIA

Para resolver esto, necesito que me proporciones:

### 1. LOS LOGS DE LA CONSOLA DONDE CORRE JAVA

Abre la terminal donde está corriendo la aplicación (con `java -jar...`).

Busca **MENSAJES EN ROJO** o que digan **"ERROR"**.

**Copia TODO lo que veas, especialmente:**
- Stack trace (líneas con "at com.example...")
- Mensaje de error exacto
- Timestamps

### 2. ¿QUÉ DATOS INTENTASTE GUARDAR?

Dime exactamente qué valores pusiste en cada campo:
- Nombre: _______________
- Descripción: ______________
- Precio: _______________
- Stock: _______________
- Categoría: ______________

### 3. LA CONSOLA DEL NAVEGADOR (F12)

Presiona F12 → Console → Haz clic en Guardar → Copia TODO lo que veas.

---

## 🚀 MIENTRAS TANTO - PRUEBA ESTO

### Opción 1: Probar desde PowerShell

Abre OTRA terminal (no cierres la que corre java) y ejecuta:

```powershell
$body = @{
    name = "Test"
    description = "Descripción de prueba"
    price = 99.99
    stock = 5
    category = "Electronics"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body -Verbose
```

¿Qué resultado ves?

### Opción 2: Ver toda la respuesta de error

```powershell
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
      -Method POST `
      -ContentType "application/json" `
      -Body $body
} catch {
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)"
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $errorBody = $reader.ReadToEnd()
    $reader.Close()
    Write-Host "Error Response:"
    Write-Host $errorBody
}
```

Copia TODO lo que ves.

---

## 📊 POSIBLES CAUSAS (Error 500)

1. **MongoDB no inicializa**
   - Flapdoodle no arranca
   - Conexión rechazada

2. **Validación falla**
   - Datos no cumplen validaciones
   - Tipos de datos incorrectos

3. **Exception no manejada**
   - Bug en el código
   - Null pointer exception

4. **Serialización JSON**
   - Problemas al convertir BigDecimal

---

## 🎯 PRÓXIMOS PASOS

Comparte conmigo:

1. **El error EXACTO de los logs de java** (rojo)
2. **El resultado de probar desde PowerShell**
3. **Los datos exactos que intentaste guardar**
4. **El error de F12 Console**

Con esa información podré identificar y resolver el problema.

