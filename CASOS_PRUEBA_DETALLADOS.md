# 🎯 Casos de Prueba Detallados por Módulo

**REQ-2026-08-08-001-spring-crud-ms**

---

## 📦 Módulo 1: ProductController

### Ubicación del código
`src/main/java/.../controller/ProductController.java`

### Responsabilidad
Mapear endpoints HTTP a operaciones CRUD.

### Casos de Prueba

#### **TC-1.1: Crear Producto Válido**
- **Endpoint:** `POST /api/v1/products`
- **Input:**
  ```json
  {
    "name": "Laptop Dell XPS 13",
    "description": "Laptop ultraportátil con SSD 512GB",
    "price": 1299.99,
    "stock": 5,
    "category": "Electronics"
  }
  ```
- **Status Esperado:** 201 Created
- **Validación:**
  - ID generado y único
  - `createdAt` = timestamp actual
  - `updatedAt` = `createdAt`
  - Todos los campos coinciden

#### **TC-1.2: Crear Producto - Nombre Vacío**
- **Endpoint:** `POST /api/v1/products`
- **Input:**
  ```json
  {
    "name": "",
    "price": 100.00,
    "stock": 10,
    "category": "Electronics"
  }
  ```
- **Status Esperado:** 400 Bad Request
- **Validación:**
  - `message.name` contiene "no puede estar en blanco" o similar
  - `status` = 400

#### **TC-1.3: Crear Producto - Falta Categoría**
- **Endpoint:** `POST /api/v1/products`
- **Input:**
  ```json
  {
    "name": "Laptop",
    "price": 100.00,
    "stock": 10
  }
  ```
- **Status Esperado:** 400 Bad Request
- **Validación:**
  - `message.category` indica error

#### **TC-1.4: Crear Producto - Nombre > 100 chars**
- **Endpoint:** `POST /api/v1/products`
- **Input:**
  ```json
  {
    "name": "[101+ caracteres de Lorem Ipsum]",
    "price": 100.00,
    "stock": 10,
    "category": "Test"
  }
  ```
- **Status Esperado:** 400 Bad Request
- **Validación:**
  - `message.name` indica límite de tamaño

#### **TC-1.5: Crear Producto - Descripción > 500 chars**
- **Endpoint:** `POST /api/v1/products`
- **Input:** Descripción con 501+ caracteres
- **Status Esperado:** 400 Bad Request
- **Validación:**
  - `message.description` indica límite

#### **TC-1.6: Crear Producto - Precio Negativo**
- **Endpoint:** `POST /api/v1/products`
- **Input:**
  ```json
  {
    "name": "Test",
    "price": -50.00,
    "stock": 10,
    "category": "Test"
  }
  ```
- **Status Esperado:** 400 Bad Request
- **Validación:**
  - `message.price` indica valor mínimo

#### **TC-1.7: Crear Producto - Stock Negativo**
- **Endpoint:** `POST /api/v1/products`
- **Input:**
  ```json
  {
    "name": "Test",
    "price": 100.00,
    "stock": -5,
    "category": "Test"
  }
  ```
- **Status Esperado:** 400 Bad Request
- **Validación:**
  - `message.stock` indica valor mínimo (0)

#### **TC-1.8: Listar Productos - Sin Paginación**
- **Endpoint:** `GET /api/v1/products`
- **Status Esperado:** 200 OK
- **Validación:**
  - Respuesta es array o Page
  - Si hay productos, estructura es correcta
  - Si no hay, array vacío

#### **TC-1.9: Listar Productos - Con Paginación**
- **Endpoint:** `GET /api/v1/products?page=0&size=5`
- **Status Esperado:** 200 OK
- **Validación:**
  - Máximo 5 items en respuesta
  - `totalElements` contiene total
  - `totalPages` es calculado
  - `currentPage` = 0

#### **TC-1.10: Listar Productos - Página 2**
- **Endpoint:** `GET /api/v1/products?page=1&size=5`
- **Status Esperado:** 200 OK
- **Validación:**
  - Retorna elementos de página 2
  - Si hay menos de 5, los retorna
  - Si no hay página 2, array vacío

#### **TC-1.11: Obtener Producto por ID**
- **Endpoint:** `GET /api/v1/products/{id}`
- **Prerequisito:** Crear producto antes
- **Status Esperado:** 200 OK
- **Validación:**
  - Datos coinciden con creados
  - Todos los campos presentes
  - `id` coincide con el solicitado

#### **TC-1.12: Obtener Producto - ID No Existe**
- **Endpoint:** `GET /api/v1/products/invalid_id_000000000000`
- **Status Esperado:** 404 Not Found
- **Validación:**
  - `error` = "Not Found"
  - `message` contiene "no encontrado"
  - `status` = 404

#### **TC-1.13: Actualizar Producto - PUT Completo**
- **Endpoint:** `PUT /api/v1/products/{id}`
- **Input:**
  ```json
  {
    "name": "Laptop NUEVA",
    "description": "Descripción actualizada",
    "price": 999.99,
    "stock": 20,
    "category": "Premium"
  }
  ```
- **Status Esperado:** 200 OK
- **Validación:**
  - Todos los campos actualizados
  - `createdAt` sin cambios
  - `updatedAt` > `createdAt`

#### **TC-1.14: Actualizar Producto - PUT Validación Falla**
- **Endpoint:** `PUT /api/v1/products/{id}`
- **Input:** Nombre vacío
- **Status Esperado:** 400 Bad Request
- **Validación:**
  - Producto NO se actualiza
  - GET posterior retorna datos originales

#### **TC-1.15: Actualizar Producto - PUT ID No Existe**
- **Endpoint:** `PUT /api/v1/products/invalid_id`
- **Status Esperado:** 404 Not Found
- **Validación:**
  - Error apropiado

#### **TC-1.16: Actualizar Parcial - PATCH Solo Precio**
- **Endpoint:** `PATCH /api/v1/products/{id}`
- **Input:**
  ```json
  {
    "price": 799.99
  }
  ```
- **Status Esperado:** 200 OK
- **Validación:**
  - `price` actualizado
  - Otros campos sin cambios
  - `name` igual a original
  - `stock` igual a original

#### **TC-1.17: Actualizar Parcial - PATCH Solo Stock**
- **Endpoint:** `PATCH /api/v1/products/{id}`
- **Input:**
  ```json
  {
    "stock": 25
  }
  ```
- **Status Esperado:** 200 OK
- **Validación:**
  - `stock` actualizado
  - `price` igual a original

#### **TC-1.18: Actualizar Parcial - PATCH Ambos**
- **Endpoint:** `PATCH /api/v1/products/{id}`
- **Input:**
  ```json
  {
    "price": 599.99,
    "stock": 30
  }
  ```
- **Status Esperado:** 200 OK
- **Validación:**
  - Ambos campos actualizados
  - Otros campos sin cambios

#### **TC-1.19: Actualizar Parcial - PATCH Validación Falla**
- **Endpoint:** `PATCH /api/v1/products/{id}`
- **Input:**
  ```json
  {
    "price": -100
  }
  ```
- **Status Esperado:** 400 Bad Request
- **Validación:**
  - Producto NO se actualiza

#### **TC-1.20: Eliminar Producto**
- **Endpoint:** `DELETE /api/v1/products/{id}`
- **Status Esperado:** 204 No Content
- **Validación:**
  - Cuerpo vacío
  - GET posterior retorna 404

#### **TC-1.21: Eliminar Producto - ID No Existe**
- **Endpoint:** `DELETE /api/v1/products/invalid_id`
- **Status Esperado:** 404 Not Found
- **Validación:**
  - Error apropiado

---

## 🔧 Módulo 2: ProductService

### Ubicación del código
`src/main/java/.../service/ProductService.java`

### Responsabilidad
Lógica de negocio CRUD, mapeo DTO↔Entity.

### Casos de Prueba (Validados a través de API)

#### **TC-2.1: Crear y Mapear DTO a Entity**
- **Test:** `POST /api/v1/products` con datos válidos
- **Validación:**
  - Todos los campos mapeados correctamente
  - `@CreatedDate` y `@LastModifiedDate` asignados
  - ID generado único

#### **TC-2.2: Mapear Entity a ResponseDTO**
- **Test:** `GET /api/v1/products/{id}`
- **Validación:**
  - ResponseDTO contiene todos los campos
  - Valores coinciden con Entity

#### **TC-2.3: Service lanza ProductNotFoundException**
- **Test:** `GET /api/v1/products/invalid_id`
- **Validación:**
  - GlobalExceptionHandler captura excepción
  - Retorna 404

#### **TC-2.4: Service calcula UpdatedAt**
- **Test:** Crear → Esperar 1 segundo → Actualizar → GET
- **Validación:**
  - `updatedAt` > `createdAt`

#### **TC-2.5: Service respeta transacciones**
- **Test:** POST → Error de validación → GET
- **Validación:**
  - Producto NO se crea si falla validación

---

## 📚 Módulo 3: ProductRepository

### Ubicación del código
`src/main/java/.../repository/ProductRepository.java`

### Responsabilidad
Acceso a datos MongoDB.

### Casos de Prueba (Validados a través de API y Tests)

#### **TC-3.1: findById(String id)**
- **Test:** `GET /api/v1/products/{id}`
- **Validación:**
  - Retorna Optional con producto
  - O Optional.empty() si no existe

#### **TC-3.2: findAll()**
- **Test:** `GET /api/v1/products` sin paginación
- **Validación:**
  - Retorna todos los productos

#### **TC-3.3: findAll(Pageable)**
- **Test:** `GET /api/v1/products?page=0&size=10`
- **Validación:**
  - Retorna Page con paginación

#### **TC-3.4: save(Product)**
- **Test:** `POST /api/v1/products`
- **Validación:**
  - Inserta en MongoDB
  - Retorna con ID asignado

#### **TC-3.5: saveAll(List)**
- **Test:** Crear múltiples productos
- **Validación:**
  - Todos se insertan correctamente

#### **TC-3.6: deleteById(String)**
- **Test:** `DELETE /api/v1/products/{id}`
- **Validación:**
  - Elimina de MongoDB
  - findById retorna empty después

#### **TC-3.7: Persistencia en MongoDB**
- **Test:** Crear → Reiniciar servidor → GET
- **Validación:**
  - Datos persisten en base de datos

#### **TC-3.8: Índices MongoDB**
- **Test:** Múltiples operaciones
- **Validación:**
  - Queries son rápidas
  - Sin errores de índice

---

## 🎯 Módulo 4: GlobalExceptionHandler

### Ubicación del código
`src/main/java/.../exception/GlobalExceptionHandler.java`

### Responsabilidad
Mapear excepciones a respuestas HTTP.

### Casos de Prueba

#### **TC-4.1: ProductNotFoundException → 404**
- **Trigger:** `GET /api/v1/products/invalid`
- **Esperado:**
  ```json
  {
    "status": 404,
    "error": "Not Found",
    "message": "Producto no encontrado con ID: invalid",
    "path": "/api/v1/products/invalid",
    "timestamp": "2026-08-08T..."
  }
  ```
- **Validación:**
  - Status = 404
  - Mensaje claro
  - Timestamp en ISO 8601

#### **TC-4.2: MethodArgumentNotValidException → 400**
- **Trigger:** `POST /api/v1/products` con nombre vacío
- **Esperado:**
  ```json
  {
    "status": 400,
    "error": "Bad Request",
    "message": {
      "name": "name debe estar presente"
    },
    "path": "/api/v1/products",
    "timestamp": "2026-08-08T..."
  }
  ```
- **Validación:**
  - Status = 400
  - `message` es Map con campos erróneos
  - Todos los campos inválidos listados

#### **TC-4.3: Múltiples Errores de Validación**
- **Trigger:** POST con nombre vacío, precio negativo, stock negativo
- **Esperado:**
  - `message` contiene 3 entradas
  - Todas las validaciones reportadas

#### **TC-4.4: Excepción Genérica → 500**
- **Trigger:** Forzar error no controlado (si es posible)
- **Esperado:**
  ```json
  {
    "status": 500,
    "error": "Internal Server Error",
    "message": "Error interno del servidor",
    "path": "/api/v1/...",
    "timestamp": "2026-08-08T..."
  }
  ```
- **Validación:**
  - Status = 500
  - Mensaje genérico (sin detalles sensibles)

#### **TC-4.5: Envelope JSON Consistente**
- **Test:** Múltiples requests fallidos
- **Validación:**
  - Todas las respuestas de error tienen misma estructura
  - Campos consistentes: status, error, message, path, timestamp

---

## ✅ Módulo 5: Product (Entidad/DTO)

### Ubicación del código
`src/main/java/.../model/Product.java`
`src/main/java/.../dto/ProductRequestDTO.java`
`src/main/java/.../dto/ProductResponseDTO.java`
`src/main/java/.../dto/ProductPatchDTO.java`

### Casos de Prueba

#### **TC-5.1: Product - Anotaciones @Document**
- **Test:** Crear producto → Verificar en MongoDB
- **Validación:**
  - Documento creado en colección "products"
  - ID es ObjectId válido

#### **TC-5.2: ProductRequestDTO - Validaciones Presentes**
- **Test:** POST con datos inválidos
- **Validación:**
  - Anotaciones @NotBlank, @Size, @Min, @DecimalMin funcionan
  - Errores capturados antes de Service

#### **TC-5.3: ProductResponseDTO - Todos los Campos**
- **Test:** GET endpoint
- **Validación:**
  - id, name, description, price, stock, category presentes
  - createdAt, updatedAt presentes
  - Formato JSON correcto

#### **TC-5.4: ProductPatchDTO - Campos Opcionales**
- **Test:** PATCH con solo price
- **Validación:**
  - Stock no afectado
  - Price actualizado

#### **TC-5.5: Timestamps Auto-gestionados**
- **Test:** Crear → GET → Actualizar → GET
- **Validación:**
  - createdAt no cambia
  - updatedAt se actualiza
  - Formato ISO 8601 correcto

#### **TC-5.6: Price es BigDecimal**
- **Test:** POST con price: 1299.99
- **Validación:**
  - Precisión mantenida
  - No hay redondeos

#### **TC-5.7: Stock es Integer**
- **Test:** POST con stock: 100
- **Validación:**
  - Valor exacto mantenido
  - No hay conversiones

---

## 🗄️ Módulo 6: ProductRepository (Consultas)

### Casos de Prueba Adicionales

#### **TC-6.1: findByCategory (si existe)**
- **Test:** Crear múltiples productos en categorías diferentes
- **Validación:**
  - Query retorna solo productos de categoría solicitada

#### **TC-6.2: Consultas Case-Sensitive**
- **Test:** Crear "Electronics", buscar "electronics"
- **Validación:**
  - MongoDB es case-sensitive por defecto

#### **TC-6.3: Índices Mejoran Performance**
- **Test:** 1000 inserciones → Búsqueda
- **Validación:**
  - Búsquedas son rápidas (< 100ms)

---

## 📋 Matriz de Cobertura

| Módulo | Métodos | Casos Prueba | Cobertura |
|--------|---------|--------------|-----------|
| ProductController | 6 | 21 | 100% |
| ProductService | 6 | 5 | 80% |
| ProductRepository | 6 | 8 | 100% |
| GlobalExceptionHandler | 3 | 5 | 100% |
| Product + DTOs | 5 | 7 | 100% |
| **TOTAL** | **26** | **46** | **96%** |

---

**Última actualización:** 2026-08-08  
**Versión:** 1.0.0  
**Estado:** ✅ Completado
