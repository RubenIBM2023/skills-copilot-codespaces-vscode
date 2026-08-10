# 🗺️ DIAGRAMA DE FLUJO - Pruebas GUI

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                     CLIENTE HTTP                                │
│  (Swagger UI / Postman / Insomnia / Browser)                    │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                    REQUEST HTTP  │  RESPONSE HTTP
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                  SPRING BOOT 3.2.5                              │
│                   Port 8080                                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │             ProductController                           │   │
│  │  (Mapeo HTTP → Métodos)                                 │   │
│  │  ✓ POST   /api/v1/products                              │   │
│  │  ✓ GET    /api/v1/products                              │   │
│  │  ✓ GET    /api/v1/products/{id}                         │   │
│  │  ✓ PUT    /api/v1/products/{id}                         │   │
│  │  ✓ PATCH  /api/v1/products/{id}                         │   │
│  │  ✓ DELETE /api/v1/products/{id}                         │   │
│  └──────────────┬──────────────────────────────────────────┘   │
│                 │                                               │
│                 ▼                                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │             ProductService                             │   │
│  │  (Lógica de Negocio)                                    │   │
│  │  ✓ Validación de entrada                                │   │
│  │  ✓ Mapeo DTO ↔ Entity                                   │   │
│  │  ✓ Manejo de excepciones                                │   │
│  │  ✓ Gestión de transacciones                             │   │
│  └──────────────┬──────────────────────────────────────────┘   │
│                 │                                               │
│                 ▼                                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │             ProductRepository                          │   │
│  │  (Acceso a Datos - MongoRepository)                    │   │
│  │  ✓ findById, findAll                                    │   │
│  │  ✓ save, delete                                         │   │
│  │  ✓ Paginación                                           │   │
│  └──────────────┬──────────────────────────────────────────┘   │
│                 │                                               │
│                 ▼                                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │             GlobalExceptionHandler                     │   │
│  │  (Mapeo Excepciones → Respuestas HTTP)                │   │
│  │  ✓ ProductNotFoundException → 404                       │   │
│  │  ✓ MethodArgumentNotValidException → 400              │   │
│  │  ✓ Exception genérica → 500                            │   │
│  └──────────────┬──────────────────────────────────────────┘   │
│                 │                                               │
│                 ▼                                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │             ResponseDTO                                │   │
│  │  (Serialización JSON)                                  │   │
│  │  {id, name, price, stock, ...}                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────┬────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    MONGODB                                      │
│                  localhost:27017                                │
│                   products_db                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Collection: products                                  │   │
│  │  ┌──────────────────────────────────────────────────┐  │   │
│  │  │ { _id, name, price, stock, category,          │  │   │
│  │  │   description, createdAt, updatedAt }          │  │   │
│  │  └──────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Flujo de Operaciones CRUD

### 1️⃣ CREATE (POST)

```
┌──────────────┐
│  Postman     │
│ POST Request │
└──────┬───────┘
       │
       ▼
   JSON Body: {
     name: "Laptop",
     price: 1299.99,
     stock: 5,
     category: "Electronics"
   }
       │
       ▼
  ProductController
    @Valid validate
       │
       ▼
  ProductService
    mapDTOtoEntity()
    generateId()
       │
       ▼
  ProductRepository
    save(product)
       │
       ▼
  MongoDB Insert
       │
       ▼
  Response 201
  JSON: {
    id: "66b5f...",
    name: "Laptop",
    createdAt: "2026-08-08T...",
    updatedAt: "2026-08-08T..."
  }
```

### 2️⃣ READ (GET)

```
┌──────────────┐
│  Postman     │
│ GET Request  │
└──────┬───────┘
       │
       ▼
   URL: /api/v1/products/66b5f...
       │
       ▼
  ProductController
    @PathVariable id
       │
       ▼
  ProductService
    getProductById(id)
       │
       ▼
  ProductRepository
    findById(id) → Optional<Product>
       │
       ▼
  MongoDB Query
    collection.find({_id: ObjectId})
       │
       ▼
  Response 200 (or 404)
  JSON: { id, name, price, ... }
```

### 3️⃣ UPDATE (PUT/PATCH)

```
┌──────────────────┐
│  Postman         │
│ PUT/PATCH Request│
└──────┬───────────┘
       │
       ▼
   JSON Body: { price: 999.99, ... }
       │
       ▼
  ProductController
    @PathVariable id
    @RequestBody update
    @Valid validate
       │
       ▼
  ProductService
    updateProduct(id, update)
    setUpdatedAt(now)
       │
       ▼
  ProductRepository
    save(updatedProduct)
       │
       ▼
  MongoDB Update
    collection.updateOne({_id: ...})
       │
       ▼
  Response 200
  JSON: { id, name, price: 999.99, ... }
```

### 4️⃣ DELETE (DELETE)

```
┌──────────────┐
│  Postman     │
│ DELETE Request
└──────┬───────┘
       │
       ▼
   URL: /api/v1/products/66b5f...
       │
       ▼
  ProductController
    @PathVariable id
       │
       ▼
  ProductService
    deleteProduct(id)
       │
       ▼
  ProductRepository
    deleteById(id)
       │
       ▼
  MongoDB Delete
    collection.deleteOne({_id: ...})
       │
       ▼
  Response 204 No Content
```

---

## Flujo de Validación

```
Request Input
     │
     ▼
┌──────────────────────────┐
│ Validación en DTO       │
│ @NotBlank name          │
│ @Size(max=100)          │
│ @DecimalMin("0.0")      │
│ @Min(0) stock           │
└────────┬─────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
   OK      ERROR
    │         │
    │         ▼
    │    MethodArgumentNotValidException
    │         │
    │         ▼
    │    GlobalExceptionHandler
    │         │
    │         ▼
    │    Response 400
    │    {
    │      status: 400,
    │      error: "Bad Request",
    │      message: {
    │        name: "must not be blank",
    │        price: "must be positive"
    │      }
    │    }
    │
    ▼
ProductService
     │
     ▼
ProductRepository
     │
     ▼
MongoDB Insert/Update
     │
     ▼
Response 201/200
```

---

## Manejo de Excepciones

```
Operación en Spring
       │
    ┌──┴──┐
    ▼     ▼
ProductNotFoundException  MethodArgumentNotValidException  Otros Errores
    │                               │                         │
    ▼                               ▼                         ▼
GlobalExceptionHandler
    │
    ├─ @ExceptionHandler(ProductNotFoundException.class)
    │  → Response 404
    │  → message: "Producto no encontrado"
    │
    ├─ @ExceptionHandler(MethodArgumentNotValidException.class)
    │  → Response 400
    │  → message: Map<field, error>
    │
    └─ @ExceptionHandler(Exception.class)
       → Response 500
       → message: "Internal Server Error"
```

---

## Estado de HTTP Responses

```
┌──────────────────────────────────────────┐
│           Request → Response             │
├──────────────────────────────────────────┤
│                                          │
│  POST /api/v1/products                  │
│  ✓ Válido → 201 Created + Body          │
│  ✗ Inválido → 400 Bad Request + Errors  │
│                                          │
│  GET /api/v1/products                   │
│  ✓ Siempre → 200 OK + Array/Page       │
│                                          │
│  GET /api/v1/products/{id}              │
│  ✓ Existe → 200 OK + Product            │
│  ✗ No existe → 404 Not Found            │
│                                          │
│  PUT /api/v1/products/{id}              │
│  ✓ Válido + Existe → 200 OK             │
│  ✗ Inválido → 400 Bad Request           │
│  ✗ No existe → 404 Not Found            │
│                                          │
│  PATCH /api/v1/products/{id}            │
│  ✓ Válido + Existe → 200 OK             │
│  ✗ Inválido → 400 Bad Request           │
│  ✗ No existe → 404 Not Found            │
│                                          │
│  DELETE /api/v1/products/{id}           │
│  ✓ Existe → 204 No Content              │
│  ✗ No existe → 404 Not Found            │
│                                          │
└──────────────────────────────────────────┘
```

---

## Matriz de Pruebas

```
┌────────────────────────────────────────────────────────────────┐
│                    PRUEBAS CRUZADAS                            │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  Módulo              Endpoint      Entrada    Salida          │
│  ──────────────────────────────────────────────────────────── │
│  Controller    POST /products      DTO → Valid  201 + Body    │
│  Service       → createProduct()   Entity      DTO             │
│  Repository    → save()            Mongo Cmd   ObjectId        │
│  Handler       → SUCCESS           No Error    Respuesta OK    │
│                                                                │
│  ──────────────────────────────────────────────────────────── │
│  Controller    POST /products      DTO → Invalid 400          │
│  Validator     → @Valid            Errors     Map<err>        │
│  Handler       → MethodArg...Ex    Exception  Error JSON      │
│  Repository    (No se ejecuta)                                │
│                                                                │
│  ──────────────────────────────────────────────────────────── │
│  Controller    GET /products/{id}  Id         200/404         │
│  Service       → getById()         Entity     DTO/Null        │
│  Repository    → findById()        Mongo Cmd  Optional        │
│  Handler       → catch Exception   Null       404 JSON        │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## Ciclo de Vida de una Request

```
Timeline de Request
═══════════════════════════════════════════════════════════════

T0: Postman → Envía request HTTP
    POST /api/v1/products
    Content-Type: application/json
    Body: {...}

T1: DispatcherServlet
    Enruta a ProductController

T2: ProductController
    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO req)

T3: @Valid Trigger
    → Bean Validation annotations en DTO
    → Si falla → MethodArgumentNotValidException
    → Si ok → Continúa

T4: ProductService
    productService.create(requestDTO)
    → Mapea DTO a Entity
    → Genera ID
    → Asigna createdAt/updatedAt

T5: ProductRepository
    productRepository.save(entity)
    → MongoDB insert
    → Retorna con ID asignado

T6: Mapeo Inverso
    → Entity → ResponseDTO
    → Incluye timestamps

T7: ResponseEntity
    HttpStatus.CREATED (201)
    Headers + Body JSON

T8: Postman ← Recibe respuesta
    Status: 201
    Body: {...id, createdAt, updatedAt...}

═══════════════════════════════════════════════════════════════
Total Time: 50-100ms típicamente
```

---

## Verificación de Datos

```
┌─ Antes de Operación
│
├─ MongoDB tiene:
│  Collection 'products': [doc1, doc2]
│
├─ POST /api/v1/products (nuevo producto)
│
└─ Después de Operación
   │
   ├─ MongoDB tiene:
   │  Collection 'products': [doc1, doc2, doc3_NEW]
   │
   ├─ Verificación:
   │  GET /api/v1/products
   │  → Retorna 3 elementos
   │  → Nuevo tiene ID único
   │  → Timestamps correctos
   │
   └─ Si DELETE:
      MongoDB tiene:
      Collection 'products': [doc1, doc2]
      → Vuelve a 2 elementos
```

---

**Diagrama versión:** 1.0.0  
**Última actualización:** 2026-08-08
