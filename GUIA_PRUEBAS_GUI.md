# GUÍA COMPLETA DE PRUEBAS CON INTERFAZ GRÁFICA
## REQ-2026-08-08-001-spring-crud-ms: Microservicio CRUD de Productos

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Ejecución del Servidor](#ejecución-del-servidor)
3. [Herramientas de Prueba GUI](#herramientas-de-prueba-gui)
4. [Pruebas de Módulos](#pruebas-de-módulos)
5. [Casos de Prueba Completos](#casos-de-prueba-completos)
6. [Validación de Respuestas](#validación-de-respuestas)
7. [Troubleshooting](#troubleshooting)

---

## 🔧 Requisitos Previos

### Software Necesario
- **Java 17+** (para ejecutar Spring Boot)
- **MongoDB** (base de datos local)
- **Postman** o **Insomnia** (cliente HTTP con GUI)
- **Git** (control de versiones)

### Verificar Instalaciones

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version
```

### Instalación de MongoDB (Windows)

```powershell
# Usar Windows Package Manager (recomendado)
winget install MongoDB.Community

# O descargar directamente desde: https://www.mongodb.com/try/download/community
```

---

## 🚀 Ejecución del Servidor

### Paso 1: Navegar al Directorio del Proyecto

```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
```

### Paso 2: Compilar el Proyecto

```bash
mvn clean compile
```

### Paso 3: Ejecutar Tests (Opcional)

```bash
mvn test
```

### Paso 4: Ejecutar la Aplicación

**Opción A: Desde Maven (Desarrollo)**
```bash
mvn spring-boot:run
```

**Opción B: JAR Compilado**
```bash
mvn clean package
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

### Paso 5: Verificar que el Servidor Esté Activo

Cuando veas este mensaje, el servidor está listo:
```
2026-08-08 11:40:00.123  INFO ... : Tomcat started on port(s): 8080
2026-08-08 11:40:00.456  INFO ... : Started SpringCrudMsApplication in 2.345 seconds
```

---

## 🎨 Herramientas de Prueba GUI

### Opción 1: Postman Desktop (Recomendado)

**Ventajas:**
- Interfaz intuitiva y potente
- Collections reutilizables
- Autenticación y tests automatizados
- Documentación incluida

**Descarga:**
```
https://www.postman.com/downloads/
```

**Primeros Pasos en Postman:**
1. Crear nueva Collection: `Spring CRUD Products`
2. Crear nuevas Requests para cada endpoint
3. Guardar respuestas en ejemplos
4. Ejecutar Tests automáticos

### Opción 2: Insomnia

**Ventajas:**
- Ligero y rápido
- Buena gestión de variables
- UI moderna

**Descarga:**
```
https://insomnia.rest/
```

### Opción 3: Swagger UI Integrada (Browser)

**Acceso Directo:**
```
http://localhost:8080/swagger-ui.html
```

**Características:**
- Documentación interactiva
- Pruebas directas desde el navegador
- Esquema OpenAPI 3.0

---

## 🧪 Pruebas de Módulos

### Módulo 1: ProductController (Endpoints REST)

#### Endpoint 1.1: Crear Producto (POST)

**URL:**
```
POST http://localhost:8080/api/v1/products
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "name": "Laptop Dell XPS 13",
  "description": "Laptop ultraportátil con procesador Intel Core i7",
  "price": 1299.99,
  "stock": 5,
  "category": "Electronics"
}
```

**Respuesta Esperada (201 Created):**
```json
{
  "id": "66b5f8c3d4e5a1b2c3d4e5f6",
  "name": "Laptop Dell XPS 13",
  "description": "Laptop ultraportátil con procesador Intel Core i7",
  "price": 1299.99,
  "stock": 5,
  "category": "Electronics",
  "createdAt": "2026-08-08T11:40:00.000Z",
  "updatedAt": "2026-08-08T11:40:00.000Z"
}
```

**Casos de Prueba Adicionales:**

**Caso 1.1.1: Validación - Nombre vacío**
```json
{
  "name": "",
  "description": "Sin nombre",
  "price": 100.00,
  "stock": 10,
  "category": "Test"
}
```
**Respuesta Esperada (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": {
    "name": "name debe estar presente"
  },
  "path": "/api/v1/products",
  "timestamp": "2026-08-08T11:40:15.000Z"
}
```

**Caso 1.1.2: Validación - Precio negativo**
```json
{
  "name": "Producto Inválido",
  "description": "Precio negativo",
  "price": -50.00,
  "stock": 10,
  "category": "Test"
}
```
**Respuesta Esperada (400 Bad Request)**

**Caso 1.1.3: Validación - Stock negativo**
```json
{
  "name": "Producto Inválido",
  "description": "Stock negativo",
  "price": 100.00,
  "stock": -5,
  "category": "Test"
}
```
**Respuesta Esperada (400 Bad Request)**

---

#### Endpoint 1.2: Obtener Todos los Productos (GET)

**URL:**
```
GET http://localhost:8080/api/v1/products
```

**Parámetros de Paginación (Opcionales):**
```
GET http://localhost:8080/api/v1/products?page=0&size=10
```

**Respuesta Esperada (200 OK):**
```json
{
  "content": [
    {
      "id": "66b5f8c3d4e5a1b2c3d4e5f6",
      "name": "Laptop Dell XPS 13",
      "description": "Laptop ultraportátil con procesador Intel Core i7",
      "price": 1299.99,
      "stock": 5,
      "category": "Electronics",
      "createdAt": "2026-08-08T11:40:00.000Z",
      "updatedAt": "2026-08-08T11:40:00.000Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10
}
```

**Casos de Prueba Adicionales:**

**Caso 1.2.1: Paginación - Primera página**
```
GET http://localhost:8080/api/v1/products?page=0&size=5
```
**Verificar:** Retorna hasta 5 elementos

**Caso 1.2.2: Paginación - Segunda página**
```
GET http://localhost:8080/api/v1/products?page=1&size=5
```
**Verificar:** Retorna siguiente lote

---

#### Endpoint 1.3: Obtener Producto por ID (GET)

**URL:**
```
GET http://localhost:8080/api/v1/products/{id}
```

**Ejemplo:**
```
GET http://localhost:8080/api/v1/products/66b5f8c3d4e5a1b2c3d4e5f6
```

**Respuesta Esperada (200 OK):**
```json
{
  "id": "66b5f8c3d4e5a1b2c3d4e5f6",
  "name": "Laptop Dell XPS 13",
  "description": "Laptop ultraportátil con procesador Intel Core i7",
  "price": 1299.99,
  "stock": 5,
  "category": "Electronics",
  "createdAt": "2026-08-08T11:40:00.000Z",
  "updatedAt": "2026-08-08T11:40:00.000Z"
}
```

**Casos de Prueba Adicionales:**

**Caso 1.3.1: ID no existe**
```
GET http://localhost:8080/api/v1/products/id_invalido_000000000000
```
**Respuesta Esperada (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con ID: id_invalido_000000000000",
  "path": "/api/v1/products/id_invalido_000000000000",
  "timestamp": "2026-08-08T11:40:30.000Z"
}
```

---

#### Endpoint 1.4: Actualizar Producto Completo (PUT)

**URL:**
```
PUT http://localhost:8080/api/v1/products/{id}
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "name": "Laptop Dell XPS 13 Actualizada",
  "description": "Laptop ultraportátil con procesador Intel Core i9",
  "price": 1499.99,
  "stock": 8,
  "category": "Premium Electronics"
}
```

**Respuesta Esperada (200 OK):**
```json
{
  "id": "66b5f8c3d4e5a1b2c3d4e5f6",
  "name": "Laptop Dell XPS 13 Actualizada",
  "description": "Laptop ultraportátil con procesador Intel Core i9",
  "price": 1499.99,
  "stock": 8,
  "category": "Premium Electronics",
  "createdAt": "2026-08-08T11:40:00.000Z",
  "updatedAt": "2026-08-08T11:42:00.000Z"
}
```

**Casos de Prueba Adicionales:**

**Caso 1.4.1: ID no existe**
```
PUT http://localhost:8080/api/v1/products/id_invalido_000000000000
```
**Respuesta Esperada (404 Not Found)**

**Caso 1.4.2: Validación incompleta**
```json
{
  "name": "",
  "description": "Sin nombre",
  "price": 100.00,
  "stock": 10,
  "category": "Test"
}
```
**Respuesta Esperada (400 Bad Request)**

---

#### Endpoint 1.5: Actualización Parcial (PATCH)

**URL:**
```
PATCH http://localhost:8080/api/v1/products/{id}
```

**Headers:**
```
Content-Type: application/json
```

**Body - Actualizar solo precio (JSON):**
```json
{
  "price": 1199.99
}
```

**Respuesta Esperada (200 OK):**
```json
{
  "id": "66b5f8c3d4e5a1b2c3d4e5f6",
  "name": "Laptop Dell XPS 13 Actualizada",
  "description": "Laptop ultraportátil con procesador Intel Core i9",
  "price": 1199.99,
  "stock": 8,
  "category": "Premium Electronics",
  "createdAt": "2026-08-08T11:40:00.000Z",
  "updatedAt": "2026-08-08T11:42:30.000Z"
}
```

**Casos de Prueba Adicionales:**

**Caso 1.5.1: Actualizar solo stock**
```json
{
  "stock": 15
}
```
**Verificar:** Solo cambia stock, otros campos permanecen igual

**Caso 1.5.2: Actualizar ambos**
```json
{
  "price": 999.99,
  "stock": 20
}
```
**Verificar:** Ambos campos se actualizan correctamente

**Caso 1.5.3: Precio negativo en PATCH**
```json
{
  "price": -100.00
}
```
**Respuesta Esperada (400 Bad Request)**

---

#### Endpoint 1.6: Eliminar Producto (DELETE)

**URL:**
```
DELETE http://localhost:8080/api/v1/products/{id}
```

**Respuesta Esperada (204 No Content):**
```
[Sin contenido en el body]
Status: 204
```

**Casos de Prueba Adicionales:**

**Caso 1.6.1: Eliminar producto no existente**
```
DELETE http://localhost:8080/api/v1/products/id_invalido_000000000000
```
**Respuesta Esperada (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con ID: id_invalido_000000000000",
  "path": "/api/v1/products/id_invalido_000000000000",
  "timestamp": "2026-08-08T11:42:45.000Z"
}
```

**Caso 1.6.2: Verificar que fue eliminado (GET)**
```
GET http://localhost:8080/api/v1/products/{id}
```
**Respuesta Esperada (404 Not Found)**

---

### Módulo 2: ProductService (Lógica de Negocio)

**Pruebas de Servicio (Sin interfaz directa - se prueban a través de los endpoints)**

#### Verificar Operaciones CRUD Completas

**Flujo de Prueba Integrada:**

1. **Crear 3 productos:**
   - Electrónica (Laptop)
   - Ropa (Camiseta)
   - Libros (Programación)

2. **Listar todos** → Verificar 3 productos

3. **Obtener cada uno** → Verificar datos correctos

4. **Actualizar cada uno** → Cambiar precio/stock

5. **Verificar actualización** → GET nuevamente

6. **Eliminar 2 productos** → Verificar 204

7. **Listar finales** → Verificar solo 1 producto

---

### Módulo 3: ProductController (Validaciones)

#### Pruebas de Validaciones de Entrada

**Validación 3.1: Campos Requeridos**

```json
{
  "name": "Producto",
  "price": 100.00,
  "stock": 10,
  "category": "Test"
}
```
**Resultado:** ✓ Acepta (descripción es opcional)

```json
{
  "name": "Producto",
  "description": "Desc",
  "stock": 10,
  "category": "Test"
}
```
**Resultado:** ✗ Falla (price requerido)

**Validación 3.2: Tamaño de Campos**

**Nombre > 100 caracteres:**
```json
{
  "name": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam",
  "description": "Test",
  "price": 100.00,
  "stock": 10,
  "category": "Test"
}
```
**Resultado:** ✗ Falla (400 Bad Request)

**Descripción > 500 caracteres:**
```json
{
  "name": "Producto",
  "description": "[500+ caracteres de texto]",
  "price": 100.00,
  "stock": 10,
  "category": "Test"
}
```
**Resultado:** ✗ Falla (400 Bad Request)

**Validación 3.3: Valores Numéricos**

**Precio negativo:**
```json
{
  "name": "Producto",
  "price": -50.00,
  "stock": 10,
  "category": "Test"
}
```
**Resultado:** ✗ Falla (400 Bad Request)

**Stock negativo:**
```json
{
  "name": "Producto",
  "price": 100.00,
  "stock": -5,
  "category": "Test"
}
```
**Resultado:** ✗ Falla (400 Bad Request)

---

### Módulo 4: GlobalExceptionHandler (Manejo de Errores)

#### Pruebas de Manejo de Excepciones

**Caso 4.1: ProductNotFoundException**
```
GET http://localhost:8080/api/v1/products/id_inexistente
```
**Respuesta esperada:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con ID: id_inexistente",
  "path": "/api/v1/products/id_inexistente",
  "timestamp": "2026-08-08T11:45:00.000Z"
}
```

**Caso 4.2: MethodArgumentNotValidException (Validaciones)**
```
POST http://localhost:8080/api/v1/products
Body: { "name": "", "price": "invalid" }
```
**Respuesta esperada:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": {
    "name": "name no puede estar en blanco",
    "price": "price debe ser un número"
  },
  "path": "/api/v1/products",
  "timestamp": "2026-08-08T11:45:15.000Z"
}
```

**Caso 4.3: Excepción Genérica**
Acceso a endpoint inexistente:
```
POST http://localhost:8080/api/v1/invalid
```
**Respuesta esperada:** 404 o 500 según comportamiento de Spring

---

### Módulo 5: ProductRepository (Persistencia)

#### Pruebas de Consultas MongoDB

**Caso 5.1: Búsqueda por Categoría**

1. Crear 2 productos en categoría "Electronics"
2. Crear 1 producto en categoría "Clothing"
3. Hacer GET y verificar filtrado por categoría

*(Nota: Esta prueba requiere exposición del endpoint en el controller o uso directo de API)*

---

## 📊 Casos de Prueba Completos

### Escenario 1: Flujo CRUD Completo

| Paso | Operación | URL | Método | Body | Resultado Esperado |
|------|-----------|-----|--------|------|-------------------|
| 1 | Crear Producto | `/api/v1/products` | POST | Laptop | 201 + ID generado |
| 2 | Listar Todos | `/api/v1/products` | GET | - | 200 + Array |
| 3 | Obtener por ID | `/api/v1/products/{id}` | GET | - | 200 + Detalles |
| 4 | Actualizar | `/api/v1/products/{id}` | PUT | Datos nuevos | 200 + Actualizado |
| 5 | Actualizar Parcial | `/api/v1/products/{id}` | PATCH | {price: 999} | 200 + Price actualizado |
| 6 | Eliminar | `/api/v1/products/{id}` | DELETE | - | 204 |
| 7 | Verificar Eliminación | `/api/v1/products/{id}` | GET | - | 404 |

### Escenario 2: Validaciones

| Caso | Body | Resultado |
|------|------|----------|
| Nombre vacío | `{"name":""}` | 400 |
| Precio negativo | `{"price":-100}` | 400 |
| Stock negativo | `{"stock":-5}` | 400 |
| Nombre > 100 chars | Nombre largo | 400 |
| Todos los campos | Datos válidos | 201 |

### Escenario 3: Manejo de Errores

| Caso | Endpoint | Resultado |
|------|----------|----------|
| ID no existe | GET `/api/v1/products/invalid` | 404 |
| Validación inválida | POST con campos vacíos | 400 |
| Método no permitido | GET `/api/v1/products` (no existe) | 404 o 405 |

---

## ✅ Validación de Respuestas

### Estructura de Respuesta Exitosa (2xx)

```json
{
  "id": "String (ObjectId MongoDB)",
  "name": "String",
  "description": "String (nullable)",
  "price": "Number (BigDecimal)",
  "stock": "Number (Integer)",
  "category": "String",
  "createdAt": "ISO 8601 Timestamp",
  "updatedAt": "ISO 8601 Timestamp"
}
```

### Estructura de Respuesta de Error (4xx/5xx)

```json
{
  "status": "HTTP Status Code",
  "error": "Error Type",
  "message": "String o Map<String, String>",
  "path": "Request Path",
  "timestamp": "ISO 8601 Timestamp"
}
```

### Validar en Postman

1. **Crear Test en Postman:**
   ```javascript
   pm.test("Status code is 200", function () {
     pm.response.to.have.status(200);
   });

   pm.test("Response has id", function () {
     var jsonData = pm.response.json();
     pm.expect(jsonData).to.have.property('id');
   });

   pm.test("Price is positive", function () {
     var jsonData = pm.response.json();
     pm.expect(jsonData.price).to.be.above(0);
   });
   ```

2. **Ejecutar Tests:**
   - Click en "Tests" tab
   - Enviar request
   - Ver resultados en "Test Results"

---

## 🔍 Troubleshooting

### Problema: "Connection refused" en puerto 8080

**Solución:**
```bash
# Verificar si algo está usando el puerto
netstat -ano | findstr :8080

# Cambiar puerto en application.properties
server.port=8081
```

### Problema: MongoDB no se conecta

**Solución:**
```bash
# Verificar que MongoDB esté corriendo
mongosh

# Si no funciona, iniciar MongoDB
net start MongoDB

# O en PowerShell (como Admin)
Start-Service MongoDB
```

### Problema: Respuesta 500 sin mensaje claro

**Solución:**
```bash
# Revisar logs en consola de Maven
mvn spring-boot:run

# O ver archivo de log
tail -f server.log
```

### Problema: Paginación retorna array vacío

**Verificar:**
- ¿Hay productos creados?
- Parámetros `page` y `size` son válidos
- Usar `?page=0&size=10` (no `page=1`)

### Problema: Validación no funciona

**Verificar:**
```bash
# Validar que las anotaciones están en el DTO
grep -n "@NotBlank\|@Size\|@Min\|@DecimalMin" src/main/java/*/dto/*.java

# Recompilar
mvn clean compile
```

---

## 📝 Checklist de Pruebas

### Antes de Comenzar
- [ ] MongoDB está corriendo
- [ ] Proyecto compiló sin errores
- [ ] Servidor está en puerto 8080
- [ ] Swagger UI accesible en `/swagger-ui.html`

### Pruebas Básicas
- [ ] POST /api/v1/products → 201
- [ ] GET /api/v1/products → 200
- [ ] GET /api/v1/products/{id} → 200
- [ ] PUT /api/v1/products/{id} → 200
- [ ] PATCH /api/v1/products/{id} → 200
- [ ] DELETE /api/v1/products/{id} → 204

### Pruebas de Validación
- [ ] Nombre vacío → 400
- [ ] Precio negativo → 400
- [ ] Stock negativo → 400
- [ ] Nombre > 100 chars → 400
- [ ] Descripción > 500 chars → 400

### Pruebas de Errores
- [ ] ID no existe → 404
- [ ] Campos requeridos faltantes → 400
- [ ] Tipo de dato incorrecto → 400

### Pruebas de Paginación
- [ ] page=0&size=5 → retorna 5 o menos
- [ ] page=1&size=5 → retorna siguiente lote
- [ ] page=99 → retorna vacío o error

### Pruebas de Timestamps
- [ ] createdAt se asigna al crear
- [ ] updatedAt se actualiza en cambios
- [ ] Formato ISO 8601

---

## 📞 Soporte

Para consultas o problemas:
1. Revisar logs: `server.log` en la carpeta del proyecto
2. Verificar MongoDB: `mongosh` desde terminal
3. Comprobar Spring Boot versión: `mvn --version`
4. Reporte de issues: https://github.com/anomalyco/opencode

---

**Última actualización:** 2026-08-08
**Versión:** 1.0.0
**Estado:** Completado
