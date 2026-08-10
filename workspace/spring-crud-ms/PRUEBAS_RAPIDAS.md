# ⚡ PRUEBAS RÁPIDAS - SPRING BOOT CRUD

## 🚀 INICIA EN 30 SEGUNDOS

### 1️⃣ Compila (primera vez)
```powershell
cd workspace\spring-crud-ms
mvn clean package -q
```

### 2️⃣ Inicia el servidor
```powershell
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

### 3️⃣ Abre en navegador
```
http://localhost:8080
```

---

## 📝 PRUEBAS CON CURL (Línea de comandos)

Si prefieres probar directamente desde PowerShell sin GUI:

### Crear Producto
```powershell
$body = @{
    name = "Laptop Dell"
    description = "Laptop potente"
    price = 1299.99
    stock = 5
    category = "Electronics"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

### Ver Todos los Productos
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
  -Method GET | Select-Object -ExpandProperty Content
```

### Ver un Producto por ID
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products/ID_AQUI" `
  -Method GET | Select-Object -ExpandProperty Content
```

### Actualizar Producto (PUT)
```powershell
$body = @{
    name = "Laptop Dell XPS"
    description = "Laptop ultraportátil"
    price = 1199.99
    stock = 8
    category = "Electronics"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products/ID_AQUI" `
  -Method PUT `
  -ContentType "application/json" `
  -Body $body
```

### Actualización Parcial (PATCH)
```powershell
$body = @{
    price = 999.99
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products/ID_AQUI" `
  -Method PATCH `
  -ContentType "application/json" `
  -Body $body
```

### Eliminar Producto
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products/ID_AQUI" `
  -Method DELETE
```

---

## 🧪 PRUEBAS DE VALIDACIÓN

### ❌ Sin nombre (debe fallar)
```powershell
$body = @{
    price = 100.00
    stock = 5
    category = "Electronics"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

Esperado: **400 Bad Request**

### ❌ Precio negativo (debe fallar)
```powershell
$body = @{
    name = "Producto"
    price = -50.00
    stock = 5
    category = "Electronics"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

Esperado: **400 Bad Request**

### ❌ Stock negativo (debe fallar)
```powershell
$body = @{
    name = "Producto"
    price = 100.00
    stock = -5
    category = "Electronics"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/products" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

Esperado: **400 Bad Request**

---

## 📊 RESPUESTAS ESPERADAS

### ✅ Crear Producto (201 Created)
```json
{
  "id": "66b58a1f2e4b1a2b3c4d5e6f",
  "name": "Laptop Dell",
  "description": "Laptop potente",
  "price": 1299.99,
  "stock": 5,
  "category": "Electronics",
  "createdAt": "2026-08-10T10:12:34Z",
  "updatedAt": "2026-08-10T10:12:34Z"
}
```

### ✅ Listar Productos (200 OK)
```json
{
  "content": [
    {
      "id": "66b58a1f2e4b1a2b3c4d5e6f",
      "name": "Laptop Dell",
      "description": "Laptop potente",
      "price": 1299.99,
      "stock": 5,
      "category": "Electronics",
      "createdAt": "2026-08-10T10:12:34Z",
      "updatedAt": "2026-08-10T10:12:34Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0
}
```

### ❌ No Encontrado (404 Not Found)
```json
{
  "status": 404,
  "error": "ProductNotFoundException",
  "message": "Producto no encontrado con ID: 66b58a1f2e4b1a2b3c4d5e70",
  "path": "/api/v1/products/66b58a1f2e4b1a2b3c4d5e70",
  "timestamp": "2026-08-10T10:12:34Z"
}
```

### ❌ Validación Fallida (400 Bad Request)
```json
{
  "status": 400,
  "error": "MethodArgumentNotValidException",
  "message": "Validation failed",
  "violations": {
    "name": "debe estar presente",
    "price": "debe ser mayor o igual a 0.0"
  },
  "path": "/api/v1/products",
  "timestamp": "2026-08-10T10:12:34Z"
}
```

---

## 🎯 CHECKLIST DE PRUEBAS

- [ ] **Crear producto** - Status 201, contiene ID
- [ ] **Listar productos** - Status 200, devuelve lista
- [ ] **Ver producto existente** - Status 200, datos correctos
- [ ] **Ver producto no existente** - Status 404
- [ ] **Actualizar completo** - Status 200, campos actualizados
- [ ] **Actualizar parcial** - Status 200, solo campos modificados
- [ ] **Eliminar producto** - Status 204, producto removido
- [ ] **Eliminar no existente** - Status 404
- [ ] **Crear sin nombre** - Status 400, validación fallida
- [ ] **Crear precio negativo** - Status 400, validación fallida
- [ ] **Paginación** - ?page=0&size=10 funciona
- [ ] **Swagger UI** - Accesible en /swagger-ui.html
- [ ] **Tests Maven** - `mvn test` pasa todos
- [ ] **Checkstyle** - `mvn checkstyle:check` sin errores

---

## 🔗 URLS ÚTILES

```
Aplicación:    http://localhost:8080
GUI Dashboard: http://localhost:8080
API Docs:      http://localhost:8080/api-docs
Swagger UI:    http://localhost:8080/swagger-ui.html

API Base:      http://localhost:8080/api/v1/products
```

---

**¡Listo para pruebas! ✅**
