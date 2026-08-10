# SPEC — REQ-2026-08-08-001-spring-crud-ms: Microservicio 1 — CRUD básico de Productos con Spring Boot

> **This file is the single source of truth for the swarm.**
> Agents read and obey this document. They may not invent requirements not declared here.
> To change requirements, edit this file first — never modify source code directly.
>
> **Location:** `specs/REQ-2026-08-08-001-spring-crud-ms/SPEC.md`
> **Sibling file:** `target.json` declares which project under `workspace/` this SPEC targets.

---

## Motivation

Se requiere construir el primer microservicio de un programa de formación práctica en Spring Boot.
El objetivo es medir el dominio de: setup de proyecto, estructura de capas, fundamentos Spring Boot/Spring Data,
diseño de API REST, persistencia con MongoDB, validaciones de entrada y manejo uniforme de errores HTTP.
La entidad de dominio elegida es **Product** (productos de catálogo), que ofrece atributos representativos
(nombre, precio, stock, categoría) sin complejidad de negocio que distraiga del aprendizaje técnico.

---

## Acceptance Criteria

1. **Estructura de capas**: El proyecto sigue la arquitectura `controller → service → repository` con clases separadas para cada capa.
2. **Entidad Product**: Existe un documento MongoDB `Product` con campos: `id` (String/ObjectId), `name` (String, requerido, ≤100 chars), `description` (String, opcional, ≤500 chars), `price` (BigDecimal, ≥ 0), `stock` (Integer, ≥ 0), `category` (String, requerido), `createdAt` (Instant, auto-gestionado), `updatedAt` (Instant, auto-gestionado).
3. **CRUD completo**:
   - `POST /api/v1/products` — crea producto, retorna 201 + body con `id` generado.
   - `GET /api/v1/products` — lista todos (soporte paginación via `?page=0&size=10`).
   - `GET /api/v1/products/{id}` — retorna 200 o 404 si no existe.
   - `PUT /api/v1/products/{id}` — actualiza completo, retorna 200 o 404.
   - `PATCH /api/v1/products/{id}` — actualización parcial de `price` y/o `stock`, retorna 200 o 404.
   - `DELETE /api/v1/products/{id}` — elimina, retorna 204 o 404.
4. **Validaciones de entrada**: `@Valid` en el controller; `@NotBlank`, `@Size`, `@Min`, `@DecimalMin` en el DTO de request. Errores de validación retornan 400 con lista de campos inválidos.
5. **Manejo de errores HTTP**: `@RestControllerAdvice` global que mapea:
   - `ProductNotFoundException` → 404 con mensaje descriptivo.
   - `MethodArgumentNotValidException` → 400 con mapa `field → message`.
   - Cualquier `Exception` no controlada → 500 genérico.
   Todos los errores usan el mismo envelope JSON: `{ "status", "error", "message", "path", "timestamp" }`.
6. **Swagger/OpenAPI**: Integración de `springdoc-openapi-starter-webmvc-ui`. Accesible en `GET /swagger-ui.html`. Cada endpoint tiene `@Operation` con summary y `@ApiResponse` para 200/201/400/404.
7. **Persistencia MongoDB**: `spring-boot-starter-data-mongodb` configurado via `application.properties` (`spring.data.mongodb.uri`). Usar `MongoRepository<Product, String>`. Para desarrollo local, configurar `Flapdoodle Embedded MongoDB` como dependencia de `test` scope para los tests de integración.
8. **Tests**:
   - Tests unitarios del `ProductService` con `@ExtendWith(MockitoExtension.class)`, cubriendo todos los métodos CRUD y los casos de `ProductNotFoundException`.
   - Tests de integración del `ProductController` con `@WebMvcTest` + `MockMvc`, verificando status codes y estructura del body JSON para cada operación.
   - Cobertura mínima del 80% en las clases `ProductService` y `ProductController`.
9. **Deterministic Verification**: Todos los siguientes comandos deben pasar con 0 errores desde la raíz del proyecto:
   - `mvn checkstyle:check` — zero style violations (Google Java Style, configurado en `checkstyle.xml`).
   - `mvn test` — zero test failures.
   - `mvn compile` — zero compilation errors.

---

## Data Contracts

### `Product` document (MongoDB)

```java
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stock;

    @NotBlank
    private String category;

    private Instant createdAt;
    private Instant updatedAt;
}
```

### `ProductRequestDTO` (entrada POST/PUT)

```java
public class ProductRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stock;

    @NotBlank
    private String category;
}
```

### `ProductResponseDTO` (salida de todos los endpoints)

```java
public class ProductResponseDTO {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private Instant createdAt;
    private Instant updatedAt;
}
```

### `ErrorResponseDTO` (envelope de errores)

```java
public class ErrorResponseDTO {
    private int status;
    private String error;
    private String message;       // o Map<String,String> violations para 400
    private String path;
    private Instant timestamp;
}
```

### `ProductPatchDTO` (entrada PATCH)

```java
public class ProductPatchDTO {
    @DecimalMin("0.0")
    private BigDecimal price;   // nullable → sólo actualiza si presente

    @Min(0)
    private Integer stock;      // nullable → sólo actualiza si presente
}
```

### Variables de entorno / `application.properties`

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/products_db
spring.application.name=spring-crud-ms
server.port=8080
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

---

## Target Project

This requirement targets the project declared in `target.json`:

```json
{ "project": "spring-crud-ms" }
```

The project root resolves to `workspace/spring-crud-ms/`. All path references below are
**relative to the project root**, not the repository root.

---

## Files in Scope

| File (project-relative) | Jurisdiction | Transformation |
|---|---|---|
| `pom.xml` | Worker 1 | Crear POM Maven con Spring Boot 3.x, spring-boot-starter-web, spring-boot-starter-data-mongodb, spring-boot-starter-validation, springdoc-openapi-starter-webmvc-ui, de.flapdoodle.embed.mongo (test), spring-boot-starter-test, checkstyle plugin |
| `src/main/java/.../model/Product.java` | Worker 2 | Crear entidad MongoDB con todos los campos del contrato, anotaciones @Document, @Id, @CreatedDate, @LastModifiedDate |
| `src/main/java/.../dto/ProductRequestDTO.java` | Worker 2 | Crear DTO de entrada con validaciones Jakarta Bean Validation |
| `src/main/java/.../dto/ProductResponseDTO.java` | Worker 2 | Crear DTO de salida |
| `src/main/java/.../dto/ProductPatchDTO.java` | Worker 2 | Crear DTO para operación PATCH |
| `src/main/java/.../dto/ErrorResponseDTO.java` | Worker 2 | Crear DTO de error con envelope uniforme |
| `src/main/java/.../exception/ProductNotFoundException.java` | Worker 2 | Crear excepción de dominio |
| `src/main/java/.../repository/ProductRepository.java` | Worker 3 | Crear interfaz MongoRepository con queries adicionales (findByCategory) |
| `src/main/java/.../service/ProductService.java` | Worker 3 | Crear interfaz + implementación con todos los métodos CRUD, mapeo DTO↔entity, lógica de negocio, lanzamiento de excepciones |
| `src/main/java/.../controller/ProductController.java` | Worker 4 | Crear @RestController con todos los endpoints, @Valid, @Operation/@ApiResponse Swagger |
| `src/main/java/.../exception/GlobalExceptionHandler.java` | Worker 4 | Crear @RestControllerAdvice manejando ProductNotFoundException, MethodArgumentNotValidException, Exception genérica |
| `src/main/java/.../SpringCrudMsApplication.java` | Worker 1 | Crear clase main con @SpringBootApplication, @EnableMongoAuditing |
| `src/main/resources/application.properties` | Worker 1 | Crear con propiedades de conexión MongoDB, servidor, springdoc |
| `src/test/java/.../service/ProductServiceTest.java` | Worker 5 | Crear tests unitarios con Mockito para todos los métodos y casos de excepción |
| `src/test/java/.../controller/ProductControllerTest.java` | Worker 5 | Crear tests de integración con @WebMvcTest + MockMvc para todos los endpoints |
| `checkstyle.xml` | Worker 1 | Crear configuración Google Java Style para el plugin Maven Checkstyle |

## Files Out of Scope (read-only for all agents)

- `specs/REQ-2026-08-08-001-spring-crud-ms/SPEC.md` — this file; immutable contract
- `AGENTS.md` — project context; do not modify
- `.env`, `.env.*` — secrets; never touch
- `runs/**` — append-only swarm history; never modify
- `workspace/legacy-teradata-migration/**` — separate project; out of scope
