# 🧪 Guía de Pruebas con Interfaz Gráfica - Spring CRUD Microservices

## Requisito: REQ-2026-08-08-001-spring-crud-ms

Esta guía te ayudará a ejecutar pruebas completas del microservicio CRUD de Productos con diferentes herramientas GUI.

---

## 📋 Tabla de Contenidos

- [Inicio Rápido](#inicio-rápido)
- [Opción 1: Swagger UI (Navegador - Más Fácil)](#opción-1-swagger-ui-navegador---más-fácil)
- [Opción 2: Postman (Recomendado)](#opción-2-postman-recomendado)
- [Opción 3: Insomnia](#opción-3-insomnia)
- [Opción 4: Pruebas Automatizadas (Script)](#opción-4-pruebas-automatizadas-script)
- [Matriz de Pruebas Completa](#matriz-de-pruebas-completa)
- [Validaciones por Módulo](#validaciones-por-módulo)
- [Troubleshooting](#troubleshooting)

---

## 🚀 Inicio Rápido

### 1️⃣ Verificar Requisitos

```powershell
# Abrir PowerShell como Administrador

# Verificar Java
java -version
# Debe ser 17 o superior

# Verificar Maven
mvn -version

# Verificar MongoDB (debe estar corriendo)
mongosh
# Si funciona, has Ctrl+C para salir
```

### 2️⃣ Compilar y Ejecutar

```powershell
# Navegar al proyecto
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"

# Compilar
mvn clean package -DskipTests

# Ejecutar
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

Cuando veas este mensaje, el servidor está listo:
```
Tomcat started on port(s): 8080 (http)
```

### 3️⃣ Abrir Navegador

```
http://localhost:8080/swagger-ui.html
```

¡Listo! Ya puedes hacer pruebas.

---

## 🎨 Opción 1: Swagger UI (Navegador - Más Fácil)

**Ventajas:** No requiere instalar nada, acceso inmediato, documentación integrada.

### Acceso

1. Abre navegador
2. Dirígete a: `http://localhost:8080/swagger-ui.html`
3. Verás algo así:

```
┌─────────────────────────────────────────────┐
│ Products API                     [Authorize]│
├─────────────────────────────────────────────┤
│ POST   /api/v1/products          Crear      │
│ GET    /api/v1/products          Listar     │
│ GET    /api/v1/products/{id}     Obtener    │
│ PUT    /api/v1/products/{id}     Actualizar │
│ PATCH  /api/v1/products/{id}     Parcial    │
│ DELETE /api/v1/products/{id}     Eliminar   │
└─────────────────────────────────────────────┘
```

### Prueba 1: Crear Producto

1. Haz clic en `POST /api/v1/products`
2. Haz clic en "Try it out"
3. En el campo "Request body", pega:

```json
{
  "name": "Laptop Dell XPS 13",
  "description": "Laptop ultraportátil",
  "price": 1299.99,
  "stock": 5,
  "category": "Electronics"
}
```

4. Haz clic en "Execute"
5. Verás la respuesta:

```json
{
  "id": "66b5f8c3d4e5a1b2c3d4e5f6",
  "name": "Laptop Dell XPS 13",
  "description": "Laptop ultraportátil",
  "price": 1299.99,
  "stock": 5,
  "category": "Electronics",
  "createdAt": "2026-08-08T11:40:00Z",
  "updatedAt": "2026-08-08T11:40:00Z"
}
```

**¡Status 201 = Éxito!**

### Prueba 2: Obtener Todos

1. Haz clic en `GET /api/v1/products`
2. Haz clic en "Try it out"
3. Haz clic en "Execute"
4. Verás lista de productos

### Prueba 3: Validación (Debe Fallar)

1. Haz clic en `POST /api/v1/products`
2. En el Request body, intenta:

```json
{
  "name": "",
  "price": -50
}
```

3. Haz clic en "Execute"
4. Verás **Status 400** con error de validación

---

## 📮 Opción 2: Postman (Recomendado)

**Ventajas:** Más potente, reutilizable, tests automáticos, historial.

### Instalación

1. Descarga de: https://www.postman.com/downloads/
2. Instala y abre

### Importar Colección

1. Abre Postman
2. Click en "Import"
3. Sube el archivo: `Postman_Collection.json` (incluido en este proyecto)
4. ¡Listo!

Verás todas las pruebas organizadas:

```
┌─ Spring CRUD Microservices
│  ├─ 1. CREAR PRODUCTO
│  │  ├─ 1.1 - Crear Laptop (Éxito)
│  │  ├─ 1.2 - Crear Smartphone
│  │  ├─ 1.3 - Crear T-Shirt
│  │  ├─ 1.4 - Validación: Nombre Vacío (Falla)
│  │  ├─ 1.5 - Validación: Precio Negativo (Falla)
│  │  └─ 1.6 - Validación: Stock Negativo (Falla)
│  ├─ 2. LEER PRODUCTOS
│  │  ├─ 2.1 - Obtener Todos (Sin Paginación)
│  │  ├─ 2.2 - Obtener Todos (Con Paginación)
│  │  ├─ 2.3 - Obtener Producto por ID (Success)
│  │  └─ 2.4 - Obtener Producto No Existe (404)
│  ├─ 3. ACTUALIZAR PRODUCTO
│  ├─ 4. ELIMINAR PRODUCTO
│  └─ 5. DOCUMENTACIÓN SWAGGER
└─
```

### Flujo de Pruebas en Postman

1. **Crear Laptop:**
   - Click en "1.1 - Crear Laptop (Éxito)"
   - Click en "Send"
   - Copiar el `id` de la respuesta
   - Guardar en variable: Click derecho en respuesta → "Set as variable"

2. **Obtener Producto:**
   - Click en "2.3 - Obtener Producto por ID (Success)"
   - Click en "Send"

3. **Actualizar:**
   - Click en "3.1 - PUT: Actualizar Completo"
   - Click en "Send"

4. **Eliminar:**
   - Click en "4.1 - Eliminar Producto (Éxito)"
   - Click en "Send"

### Ver Respuestas

En Postman:
- **Body:** Datos de respuesta
- **Status:** Código HTTP (200, 201, 400, 404, etc.)
- **Headers:** Metadatos de respuesta
- **Tests:** Verificaciones automáticas

---

## 🚀 Opción 3: Insomnia

**Ventajas:** Ligero, interfaz moderna, buena gestión de variables.

### Instalación

1. Descarga de: https://insomnia.rest/
2. Instala y abre

### Crear Requests

1. Click en "+" para nueva request
2. Nombre: "Crear Producto"
3. Método: **POST**
4. URL: `http://localhost:8080/api/v1/products`
5. Body (JSON):

```json
{
  "name": "Laptop Dell XPS 13",
  "description": "Laptop ultraportátil",
  "price": 1299.99,
  "stock": 5,
  "category": "Electronics"
}
```

6. Click en "Send"

Repite para los demás endpoints.

---

## ⚙️ Opción 4: Pruebas Automatizadas (Script)

**Ventajas:** Ejecutar todas las pruebas automáticamente, generar reporte.

### Ejecutar en Windows

1. Abre **PowerShell** como Administrador
2. Navega al proyecto:

```powershell
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
```

3. Ejecuta el script:

```powershell
.\pruebas_automatizadas.bat
```

4. Revisa el archivo de resultados:

```powershell
type pruebas_resultado.log
```

### Ver Resultados

El script creará `pruebas_resultado.log` con:
- ✅ Respuestas exitosas
- ❌ Errores y validaciones
- 📊 Resumen de pruebas

---

## 📊 Matriz de Pruebas Completa

| # | Módulo | Operación | Endpoint | Método | Esperado | Estado |
|---|--------|-----------|----------|--------|----------|--------|
| 1 | ProductController | Crear | `/api/v1/products` | POST | 201 + ID | ✅ |
| 2 | ProductController | Listar | `/api/v1/products` | GET | 200 + Array | ✅ |
| 3 | ProductController | Obtener | `/api/v1/products/{id}` | GET | 200 + Datos | ✅ |
| 4 | ProductController | Actualizar | `/api/v1/products/{id}` | PUT | 200 | ✅ |
| 5 | ProductController | Parcial | `/api/v1/products/{id}` | PATCH | 200 | ✅ |
| 6 | ProductController | Eliminar | `/api/v1/products/{id}` | DELETE | 204 | ✅ |
| 7 | Validaciones | Nombre vacío | POST | POST | 400 | ✅ |
| 8 | Validaciones | Precio negativo | POST | POST | 400 | ✅ |
| 9 | Validaciones | Stock negativo | POST | POST | 400 | ✅ |
| 10 | Errores | ID no existe | GET | GET | 404 | ✅ |
| 11 | Paginación | Con page/size | GET | GET | 200 paginado | ✅ |
| 12 | Documentación | Swagger UI | `/swagger-ui.html` | GET | 200 HTML | ✅ |

---

## 🔍 Validaciones por Módulo

### Módulo: ProductController

**Validación:** Crear producto con nombre de 100 caracteres (máximo válido)

```json
{
  "name": "Loremlorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt",
  "price": 100.00,
  "stock": 10,
  "category": "Test"
}
```

✅ Debe retornar **201**

**Validación:** Crear producto con nombre de 101 caracteres (inválido)

```json
{
  "name": "Loremlorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunta",
  "price": 100.00,
  "stock": 10,
  "category": "Test"
}
```

❌ Debe retornar **400**

### Módulo: ProductService

**Test:** Crear → Leer → Actualizar → Eliminar

```
1. POST /api/v1/products          → Status 201 ✅
2. GET /api/v1/products/{id}      → Status 200 ✅
3. PUT /api/v1/products/{id}      → Status 200 ✅
4. DELETE /api/v1/products/{id}   → Status 204 ✅
5. GET /api/v1/products/{id}      → Status 404 ✅
```

### Módulo: GlobalExceptionHandler

**Test:** Manejo de errores

```
POST /api/v1/products (vacío)
Respuesta esperada:
{
  "status": 400,
  "error": "Bad Request",
  "message": { ... },
  "path": "/api/v1/products",
  "timestamp": "2026-08-08T..."
}
```

---

## 🆘 Troubleshooting

### Problema: "Connection refused"

**Causa:** Servidor no está ejecutándose

**Solución:**
```powershell
# Terminal 1: Ejecutar servidor
cd "C:\Nueva carpeta\agentic-sdlc-sdd-arch-opencode-main\workspace\spring-crud-ms"
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar

# Terminal 2: Hacer pruebas
```

### Problema: "MongoDB connection refused"

**Causa:** MongoDB no está corriendo

**Solución:**
```powershell
# PowerShell como Admin
net start MongoDB

# O si no funciona
mongosh  # Debe conectar automáticamente
```

### Problema: Postman no puede conectar

**Causa:** Firewall o puerto bloqueado

**Solución:**
1. Verificar puerto 8080:
```powershell
netstat -ano | findstr :8080
```

2. Si algo está usando el puerto, cambiar en `application.properties`:
```properties
server.port=8081
```

### Problema: "HTTP 500" en respuesta

**Causa:** Error interno del servidor

**Solución:**
1. Revisar logs en consola de ejecución
2. Verificar que los DTOs tienen anotaciones de validación
3. Recompilar: `mvn clean compile`

### Problema: Paginación retorna vacío

**Causa:** Parámetros incorrectos

**Correcto:**
```
GET /api/v1/products?page=0&size=10
```

**Incorrecto:**
```
GET /api/v1/products?page=1&size=10
```

(En Spring, page es 0-indexed)

---

## 📝 Checklist Final de Validación

- [ ] ✅ Servidor ejecutándose en puerto 8080
- [ ] ✅ MongoDB conectado
- [ ] ✅ Swagger UI accesible
- [ ] ✅ POST /api/v1/products → 201
- [ ] ✅ GET /api/v1/products → 200
- [ ] ✅ GET /api/v1/products/{id} → 200
- [ ] ✅ PUT /api/v1/products/{id} → 200
- [ ] ✅ PATCH /api/v1/products/{id} → 200
- [ ] ✅ DELETE /api/v1/products/{id} → 204
- [ ] ✅ Validación nombre vacío → 400
- [ ] ✅ Validación precio negativo → 400
- [ ] ✅ Validación stock negativo → 400
- [ ] ✅ ID no existe → 404
- [ ] ✅ Paginación funciona
- [ ] ✅ Timestamps se generan

---

## 📚 Referencias Útiles

- **Documentación Spring Boot:** https://spring.io/projects/spring-boot
- **MongoDB Manual:** https://docs.mongodb.com/manual/
- **Spring Data MongoDB:** https://spring.io/projects/spring-data-mongodb
- **OpenAPI 3.0:** https://spec.openapis.org/oas/v3.0.3
- **Postman Docs:** https://learning.postman.com/

---

## ❓ Preguntas Frecuentes

**P: ¿Cuál es la herramienta más fácil?**
R: Swagger UI - no requiere instalación.

**P: ¿Cuál es la más potente?**
R: Postman - permite tests automáticos, variables, flujos complejos.

**P: ¿Necesito todos los puertos?**
R: Solo 8080 (servidor) y 27017 (MongoDB).

**P: ¿Puedo cambiar el puerto del servidor?**
R: Sí, en `src/main/resources/application.properties`, línea `server.port=8080`.

---

**Última actualización:** 2026-08-08  
**Estado:** ✅ Completado  
**Versión:** 1.0.0
