# SPEC — REQ-2026-08-10-002-enhanced-order-workflow-states: Enhanced Order Workflow with Additional States

> **This file is the single source of truth for the swarm.**
> Agents read and obey this document. They may not invent requirements not declared here.
> To change requirements, edit this file first — never modify source code directly.
>
> **Location:** `specs/REQ-2026-08-10-002-enhanced-order-workflow-states/SPEC.md`
> **Sibling file:** `target.json` declares which project under `workspace/` this SPEC targets.

---

## Motivation

Se requiere expandir el sistema de gestión de pedidos del microservicio Product CRUD con un flujo de trabajo mejorado. 
El objetivo es agregar **4 nuevos estados intermedios** en el ciclo de vida de las órdenes para permitir un seguimiento más granular y realista del proceso de negocio.

Los 4 pasos a agregar son:

1. **PAYMENT_PENDING** — Después de PENDING, esperando confirmación de pago
2. **PAYMENT_CONFIRMED** — Pago confirmado, listo para procesar
3. **PROCESSING** — Preparando el pedido para envío
4. **READY_FOR_SHIPMENT** — Listo para entregar al proveedor logístico

Esto permite un control más preciso del negocio y mejor experiencia del usuario en el seguimiento.

---

## Acceptance Criteria

1. **Nuevos Estados**: Se agregan 4 estados adicionales al enum `OrderStatus`:
   - `PAYMENT_PENDING` (después de PENDING)
   - `PAYMENT_CONFIRMED` (después de PAYMENT_PENDING)
   - `PROCESSING` (después de PAYMENT_CONFIRMED)
   - `READY_FOR_SHIPMENT` (antes de SHIPPED)

2. **Orden de Estados Válidos**: El flujo permite solo las siguientes transiciones:
   ```
   PENDING → PAYMENT_PENDING → PAYMENT_CONFIRMED → PROCESSING → READY_FOR_SHIPMENT → SHIPPED → DELIVERED → CANCELLED
   ```

3. **Validación de Transiciones**: El servicio valida que las transiciones de estado sean válidas:
   - No se puede pasar directamente de PENDING a SHIPPED
   - No se puede volver atrás en los estados (solo hacia adelante)
   - CANCELLED es un estado terminal (solo desde PENDING)

4. **UI Updates**: La interfaz web `orders.html` se actualiza para:
   - Mostrar los 4 nuevos botones de estado en el modal de detalles
   - Mostrar indicador visual del progreso (barra de progreso o steps)
   - Mostrar el estado actual en formato legible (traducción al español)

5. **Tests**: Se agregan tests para validar:
   - Transiciones válidas entre estados
   - Rechazo de transiciones inválidas
   - Permanencia en estado después de transición
   - Fechas de actualización (updatedAt) se actualizan con cada cambio

6. **Auditoría**: Cada cambio de estado registra:
   - Timestamp de cambio
   - Estado anterior
   - Estado nuevo
   - Usuario (si está disponible)

7. **API Response**: El endpoint `PUT /orders/{id}/status` mantiene retrocompatibilidad pero:
   - Valida que el estado nuevo sea válido según el estado actual
   - Retorna 400 si la transición es inválida con mensaje descriptivo
   - Retorna 200 con OrderDTO actualizado si éxito

8. **Database**: Si existe tabla SQL para órdenes, se agrega columna `status_history` JSON para auditoría:
   ```json
   {
     "transitions": [
       { "from": "PENDING", "to": "PAYMENT_PENDING", "timestamp": "2026-08-10T15:00:00Z" },
       { "from": "PAYMENT_PENDING", "to": "PAYMENT_CONFIRMED", "timestamp": "2026-08-10T15:05:00Z" }
     ]
   }
   ```

9. **Deterministic Verification**: Todos los siguientes comandos deben pasar con 0 errores desde la raíz del proyecto:
   - `mvn test` — Nuevos tests de transiciones de estado
   - `mvn compile` — Código compilable sin errores
   - `mvn checkstyle:check` — Cero violaciones de estilo

---

## Data Contracts

### OrderStatus Enum (Enhanced)

```java
public enum OrderStatus {
    PENDING,                // Estado inicial
    PAYMENT_PENDING,        // NUEVO: En espera de confirmación de pago
    PAYMENT_CONFIRMED,      // NUEVO: Pago confirmado
    PROCESSING,             // NUEVO: Preparando pedido
    READY_FOR_SHIPMENT,     // NUEVO: Listo para envío
    SHIPPED,                // Ya existía
    DELIVERED,              // Ya existía
    CANCELLED               // Ya existía
}
```

### OrderStatusTransition Record (Nueva)

```java
public class OrderStatusTransition {
    private String fromStatus;        // Estado anterior
    private String toStatus;          // Estado nuevo
    private Instant timestamp;        // Cuándo ocurrió
    private String reason;            // Motivo del cambio (opcional)
    private String updatedBy;         // Quién lo cambió (opcional)
}
```

### Order Model Update

Agregar a la clase `Order.java`:

```java
@LastModifiedDate
private Instant updatedAt;

@Transient
private List<OrderStatusTransition> statusHistory;  // Auditoría

@JsonIgnore
private String statusHistoryJson;  // Para persistencia en BD
```

### Request/Response DTOs

`UpdateOrderStatusRequest` (opcional, para mejorar seguridad):

```java
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus newStatus;
    
    private String reason;  // Opcional
}
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
| `src/main/java/.../model/OrderStatus.java` | Worker 1 | Agregar 4 nuevos estados al enum existente |
| `src/main/java/.../model/OrderStatusTransition.java` | Worker 1 | Nueva clase para registrar transiciones |
| `src/main/java/.../model/Order.java` | Worker 1 | Agregar campos updatedAt, statusHistory, statusHistoryJson |
| `src/main/java/.../dto/UpdateOrderStatusRequest.java` | Worker 2 | Crear DTO con validación (opcional pero recomendado) |
| `src/main/java/.../service/OrderService.java` | Worker 2 | Agregar lógica de validación de transiciones |
| `src/main/java/.../controller/OrderController.java` | Worker 2 | Mejorar endpoint PUT para validar transiciones |
| `src/main/resources/static/orders.html` | Worker 3 | Actualizar UI con 4 nuevos botones y barra de progreso |
| `src/test/java/.../service/OrderServiceTest.java` | Worker 4 | Agregar tests de transiciones válidas/inválidas |
| `src/test/java/.../controller/OrderControllerTest.java` | Worker 4 | Tests de endpoint con nuevos estados |

## Files Out of Scope

- `specs/REQ-2026-08-08-001-spring-crud-ms/` — Anterior requirement (immutable)
- `.env`, `.env.*` — Secrets (never touch)
- `workspace/legacy-teradata-migration/` — Separate project
- `runs/**` — Swarm history (append-only)

---

## Implementation Notes

### Validación de Transiciones

```java
private static final Map<OrderStatus, List<OrderStatus>> VALID_TRANSITIONS = Map.ofEntries(
    Map.entry(PENDING, List.of(PAYMENT_PENDING, CANCELLED)),
    Map.entry(PAYMENT_PENDING, List.of(PAYMENT_CONFIRMED, PENDING)),
    Map.entry(PAYMENT_CONFIRMED, List.of(PROCESSING, PAYMENT_PENDING)),
    Map.entry(PROCESSING, List.of(READY_FOR_SHIPMENT, PAYMENT_CONFIRMED)),
    Map.entry(READY_FOR_SHIPMENT, List.of(SHIPPED, PROCESSING)),
    Map.entry(SHIPPED, List.of(DELIVERED)),
    Map.entry(DELIVERED, List.of()),  // Terminal
    Map.entry(CANCELLED, List.of())   // Terminal
);
```

### Histórico en MongoDB

Se guardará como documento JSON serializado:

```json
{
  "_id": ObjectId("..."),
  "orderNumber": "ORD-001",
  "statusHistory": [
    {
      "fromStatus": "PENDING",
      "toStatus": "PAYMENT_PENDING",
      "timestamp": ISODate("2026-08-10T15:00:00Z")
    },
    {
      "fromStatus": "PAYMENT_PENDING",
      "toStatus": "PAYMENT_CONFIRMED",
      "timestamp": ISODate("2026-08-10T15:05:00Z")
    }
  ]
}
```

---

## Success Criteria

- ✅ 4 nuevos estados agregados
- ✅ Validación de transiciones funciona
- ✅ UI muestra flujo visual
- ✅ Todos los tests pasan (incluyendo nuevos)
- ✅ 0 violaciones Checkstyle
- ✅ Retrocompatibilidad mantenida
- ✅ Documentación de cambios incluida

---

**Created:** 2026-08-10  
**Status:** Ready for Implementation  
**Target Version:** 1.1.0-SNAPSHOT
