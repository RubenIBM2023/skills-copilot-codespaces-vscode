# 🔧 SOLUCIÓN FINAL - ERROR "FAILED TO FETCH"

## ❌ Error: "Failed to Fetch"

Este error significa que el navegador no puede conectar con la API. Es un problema de **CORS** (permisos de navegador).

## ✅ SOLUCIÓN IMPLEMENTADA

He configurado **CORS en dos niveles** para máxima compatibilidad:

### 1. **Configuración Global (CorsConfig.java)**
- Permite todos los orígenes especificados
- Todos los métodos HTTP
- Todos los headers

### 2. **Anotación @CrossOrigin en Controllers**
- ProductController - CORS habilitado
- HealthController - CORS habilitado

---

## 🚀 CÓMO USAR LA SOLUCIÓN

### PASO 1: Termina la aplicación anterior

Si la aplicación está corriendo, presiona **Ctrl+C** en la terminal.

### PASO 2: Limpia y recompila (IMPORTANTE)

```powershell
cd "workspace\spring-crud-ms"
mvn clean package -q -DskipTests
```

⏱️ Espera 2-3 minutos hasta ver: `BUILD SUCCESS`

### PASO 3: Inicia la aplicación

```powershell
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

Espera a ver:
```
Tomcat started on port(s): 8080 (http)
SpringCrudMsApplication started
```

### PASO 4: Abre en navegador

```
http://localhost:8080
```

### PASO 5: Prueba guardar

1. Llena el formulario
2. Haz clic en "💾 Guardar"
3. ✅ Debería funcionar ahora

---

## 🔍 SI SIGUE SIN FUNCIONAR

### Opción 1: Ver errores en F12

1. Presiona **F12**
2. Ve a **Console**
3. Haz clic en "Guardar"
4. **Copia el error completo en ROJO**

### Opción 2: Probar con PowerShell

Abre OTRA terminal (no la que corre la app) y ejecuta:

```powershell
# Primero, verifica que el servidor responde
curl http://localhost:8080/api/v1/health

# Luego intenta crear un producto
$body = @{
    name = "Test"
    price = 99.99
    stock = 5
    category = "Electronics"
} | ConvertTo-Json

curl.exe -X POST `
  -H "Content-Type: application/json" `
  -d $body `
  http://localhost:8080/api/v1/products

# O con Invoke-WebRequest:
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body `
  -Verbose
```

---

## 📝 CAMBIOS REALIZADOS

### 1. CorsConfig.java (Mejorado)

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Mapea TODOS los endpoints
                .allowedOrigins("http://localhost:8080", ...)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### 2. ProductController.java (Con @CrossOrigin)

```java
@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(
    origins = {"http://localhost:8080", "http://127.0.0.1:8080", "http://localhost"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, ...},
    allowedHeaders = "*",
    allowCredentials = "true"
)
public class ProductController { ... }
```

### 3. HealthController.java (Con @CrossOrigin)

```java
@RestController
@RequestMapping("/api/v1/health")
@CrossOrigin(...)
public class HealthController { ... }
```

---

## ✅ VERIFICACIÓN

Después de recompilar, verifica:

1. **Servidor corriendo:**
   ```
   Tomcat started on port(s): 8080
   ```

2. **Health check funciona:**
   ```
   http://localhost:8080/api/v1/health
   ```
   Debería retornar: `{"status":"OK",...}`

3. **API responde:**
   ```
   http://localhost:8080/api/v1/products
   ```
   Debería retornar: `{"content":[],...}`

4. **Guardar funciona:**
   - Llena formulario
   - Haz clic en Guardar
   - ✅ Debería aparecer producto

---

## 🎯 PRÓXIMOS PASOS

**Opción A: Script automático**
```powershell
.\EJECUTAR_AHORA.ps1
```

**Opción B: Manual**
```powershell
mvn clean package -q -DskipTests
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

Luego:
```
http://localhost:8080
```

---

## 📞 SI SIGUE FALLANDO

Comparte:
1. El error en F12 Console (en rojo)
2. Resultado de: `curl http://localhost:8080/api/v1/health`
3. Resultado de intentar crear un producto desde PowerShell

Con esa información puedo resolver el problema específico.

---

**¡Intenta de nuevo! Ahora debería funcionar correctamente.** ✅
