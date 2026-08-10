# Guía de Pruebas - API Product CRUD

## Iniciar el Servidor

### Opción 1: Usando PowerShell
```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

El servidor iniciará en: `http://localhost:8080`

### Opción 2: Usando Maven
```bash
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
mvn spring-boot:run
```

---

## Acceder a la Documentación Swagger

Una vez que el servidor esté corriendo, abre en tu navegador:
```
http://localhost:8080/swagger-ui.html
```

Aquí podrás ver todos los endpoints y probarlos desde la interfaz gráfica.

---

## Pruebas con cURL

### 1. Crear un Producto
```bash
curl -X POST "http://localhost:8080/api/v1/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "description": "Smartphone Apple flagship",
    "price": 1299.99,
    "stock": 20,
    "category": "Smartphones"
  }'
```

**Respuesta esperada (201 Created):**
```json
{
  "id": "66b5a1f2c4b3e2f1a0d9c8b7",
  "name": "iPhone 15 Pro",
  "description": "Smartphone Apple flagship",
  "price": 1299.99,
  "stock": 20,
  "category": "Smartphones",
  "createdAt": "2026-08-10T13:45:00Z",
  "updatedAt": "2026-08-10T13:45:00Z"
}
```

### 2. Listar Todos los Productos
```bash
curl -X GET "http://localhost:8080/api/v1/products?page=0&size=10"
```

**Respuesta esperada (200 OK):**
```json
{
  "content": [
    { ... producto ... }
  ],
  "pageable": { ... },
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### 3. Obtener Producto por ID
```bash
curl -X GET "http://localhost:8080/api/v1/products/{id}"
```

Reemplaza `{id}` con el ID de un producto real.

**Respuesta esperada (200 OK):**
```json
{
  "id": "66b5a1f2c4b3e2f1a0d9c8b7",
  "name": "iPhone 15 Pro",
  ...
}
```

**Si no existe (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found: invalid-id",
  "path": "/api/v1/products/invalid-id",
  "timestamp": "2026-08-10T13:45:00Z"
}
```

### 4. Actualizar Producto Completo (PUT)
```bash
curl -X PUT "http://localhost:8080/api/v1/products/{id}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro Max",
    "description": "Versión Plus del iPhone 15 Pro",
    "price": 1499.99,
    "stock": 10,
    "category": "Smartphones"
  }'
```

### 5. Actualización Parcial (PATCH - Solo Precio y/o Stock)
```bash
curl -X PATCH "http://localhost:8080/api/v1/products/{id}" \
  -H "Content-Type: application/json" \
  -d '{
    "price": 999.99,
    "stock": 25
  }'
```

### 6. Eliminar Producto
```bash
curl -X DELETE "http://localhost:8080/api/v1/products/{id}"
```

**Respuesta esperada (204 No Content)** - Sin body

---

## Pruebas de Validación

### Error de Validación (400 Bad Request)
```bash
curl -X POST "http://localhost:8080/api/v1/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "description": "Falta nombre y otros campos",
    "price": -100,
    "stock": -5,
    "category": ""
  }'
```

**Respuesta esperada:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": {
    "name": "no debe estar en blanco",
    "price": "debe ser mayor que o igual a 0.0",
    "stock": "debe ser mayor que o igual a 0",
    "category": "no debe estar en blanco"
  },
  "path": "/api/v1/products",
  "timestamp": "2026-08-10T13:45:00Z"
}
```

---

## Flujo Completo de Prueba

```bash
# 1. Crear producto
PRODUCT_ID=$(curl -s -X POST "http://localhost:8080/api/v1/products" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","description":"Testing","price":99.99,"stock":5,"category":"Test"}' \
  | grep -o '"id":"[^"]*' | cut -d'"' -f4)

echo "Producto creado con ID: $PRODUCT_ID"

# 2. Obtener producto
curl -s -X GET "http://localhost:8080/api/v1/products/$PRODUCT_ID"

# 3. Actualizar parcialmente
curl -s -X PATCH "http://localhost:8080/api/v1/products/$PRODUCT_ID" \
  -H "Content-Type: application/json" \
  -d '{"price":79.99,"stock":10}'

# 4. Listar productos
curl -s -X GET "http://localhost:8080/api/v1/products?page=0&size=10"

# 5. Eliminar producto
curl -s -X DELETE "http://localhost:8080/api/v1/products/$PRODUCT_ID"

# 6. Verificar que fue eliminado (debe retornar 404)
curl -s -X GET "http://localhost:8080/api/v1/products/$PRODUCT_ID"
```

---

## Información de Conexión a MongoDB

**Embedded MongoDB (Desarrollo):**
- **Host:** localhost
- **Puerto:** Por defecto asignado automáticamente por Flapdoodle
- **Database:** products_db

Los datos se almacenan automáticamente en la colección `products`.

---

## Checklist de Pruebas

- [ ] Crear producto (POST)
- [ ] Listar productos (GET - todos)
- [ ] Obtener producto por ID (GET - uno)
- [ ] Actualizar producto completo (PUT)
- [ ] Actualizar parcialmente (PATCH)
- [ ] Eliminar producto (DELETE)
- [ ] Validar error 404 (producto no existe)
- [ ] Validar error 400 (datos inválidos)
- [ ] Verificar timestamps (createdAt, updatedAt)
- [ ] Verificar paginación

---

## Solución de Problemas

### Puerto 8080 ya está en uso
```powershell
# Encontrar y matar el proceso
Get-Process java | Stop-Process -Force

# Opcionalmente, usar otro puerto
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar --server.port=8081
```

### MongoDB no inicia
El proyecto usa Flapdoodle (MongoDB embebido) que debería iniciarse automáticamente.
Si hay problemas, verifica los logs de la aplicación.

### Error de conexión
Asegúrate de que:
1. El servidor está corriendo en puerto 8080
2. No hay firewall bloqueando localhost:8080
3. La base de datos está disponible

---

Creado: 2026-08-10
Versión del Servidor: Spring Boot 3.2.5
Base de Datos: MongoDB 7.0.2 (Embedded)
