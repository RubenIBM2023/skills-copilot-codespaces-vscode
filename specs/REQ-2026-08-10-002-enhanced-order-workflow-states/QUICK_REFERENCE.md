# REFERENCIA RÁPIDA: REQ-2026-08-10-002

## 🎯 De un vistazo

| Campo | Valor |
|-------|-------|
| **REQ_ID** | `REQ-2026-08-10-002-enhanced-order-workflow-states` |
| **Título** | Enhanced Order Workflow with Additional States |
| **Proyecto** | `spring-crud-ms` |
| **Versión** | `1.1.0-SNAPSHOT` |
| **Tipo** | Enhancement (No breaking changes) |
| **Prioridad** | HIGH |
| **Estado** | Ready for Implementation |
| **Anterior** | REQ-2026-08-08-001 (intacto) |

---

## ⚡ Quick Facts

✅ **4 Nuevos Estados Agregados:**
- `PAYMENT_PENDING` — Esperando pago
- `PAYMENT_CONFIRMED` — Pago OK
- `PROCESSING` — Preparando envío
- `READY_FOR_SHIPMENT` — Listo para logística

✅ **Validación de Transiciones:**
- No permite saltos inválidos
- Orden rigurosa: PENDING → PAYMENT_PENDING → ... → SHIPPED
- Estados terminales: DELIVERED, CANCELLED

✅ **Auditoría Incluida:**
- Histórico de cambios de estado
- Timestamp de cada transición
- Recuperable en JSON

✅ **UI Mejorada:**
- 8 botones (4 nuevos)
- Barra de progreso visual
- Estados legibles en español

✅ **Tests Agregados:**
- Validación de transiciones válidas
- Rechazo de transiciones inválidas
- Persistencia del histórico

---

## 📁 Estructura

```
specs/
└── REQ-2026-08-10-002-enhanced-order-workflow-states/
    ├── SPEC.md                    ← Especificación completa (200+ líneas)
    └── target.json                ← Apunta a: spring-crud-ms
```

---

## 🔧 Archivos a Modificar

**Worker 1 - Modelo (3 archivos):**
- `src/main/java/.../model/OrderStatus.java` — Agregar 4 enums
- `src/main/java/.../model/OrderStatusTransition.java` — NUEVO
- `src/main/java/.../model/Order.java` — Agregar statusHistory

**Worker 2 - Servicio (3 archivos):**
- `src/main/java/.../dto/UpdateOrderStatusRequest.java` — NUEVO
- `src/main/java/.../service/OrderService.java` — Validación
- `src/main/java/.../controller/OrderController.java` — Endpoint mejorado

**Worker 3 - UI (1 archivo):**
- `src/main/resources/static/orders.html` — UI con nuevos botones

**Worker 4 - Tests (2 archivos):**
- `src/test/java/.../service/OrderServiceTest.java` — Nuevos tests
- `src/test/java/.../controller/OrderControllerTest.java` — Tests API

---

## 🎯 Acceptance Criteria (Resumen)

- [ ] 4 nuevos estados en enum OrderStatus
- [ ] Validación de transiciones implementada
- [ ] Histórico de cambios persistido
- [ ] UI muestra 8 botones (4 nuevos) con barra de progreso
- [ ] Tests: transiciones válidas e inválidas validadas
- [ ] Retrocompatibilidad mantenida
- [ ] Todos los tests pasan
- [ ] 0 violaciones checkstyle

---

## 🚀 Pasos para Implementar

### Paso 1: Revisar
```bash
cat specs/REQ-2026-08-10-002-enhanced-order-workflow-states/SPEC.md
```

### Paso 2: Planificar
- Leer SPEC.md completamente
- Entender las 4 transiciones nuevas
- Revisar archivos afectados

### Paso 3: Implementar (cuando esté listo)
```bash
# Ejecutar con OPF swarm
python infraestructura_ia/swarm.py --req-id REQ-2026-08-10-002-enhanced-order-workflow-states
```

### Paso 4: Verificar
```bash
cd workspace/spring-crud-ms
mvn clean test
mvn compile
mvn checkstyle:check
```

### Paso 5: Commit & PR
```bash
# Los cambios se commitean en rama separada
# Se crea PR automáticamente
# Se revierten los cambios del anterior REQ
```

---

## 💡 Ejemplos de Flujo

### Caso Normal:
```
Usuario crea orden
  ↓ Sistema asigna: PENDING
  ↓ Usuario paga
  ↓ Sistema: PAYMENT_PENDING → PAYMENT_CONFIRMED
  ↓ Sistema procesa
  ↓ Sistema: PROCESSING → READY_FOR_SHIPMENT
  ↓ Logística recibe
  ↓ Sistema: SHIPPED
  ↓ Cliente recibe
  ↓ Sistema: DELIVERED ✅
```

### Intento de Salto (Rechazado):
```
Sistema intenta: PENDING → SHIPPED
  ↗ Validación: ❌ INVALIDO
  ✓ Se rechaza la transición
  ✓ Se retorna error 400 con mensaje
```

---

## 🔐 Protección de Anterior REQ

✅ REQ-2026-08-08-001 **NO es afectado**
- Commit intacto (856e350)
- Specs en read-only
- Versión 1.0.0 preservada
- Rama separada para cambios

---

## 📊 Validación de Transiciones

```java
PENDING → [PAYMENT_PENDING, CANCELLED]
PAYMENT_PENDING → [PAYMENT_CONFIRMED, PENDING]
PAYMENT_CONFIRMED → [PROCESSING, PAYMENT_PENDING]
PROCESSING → [READY_FOR_SHIPMENT, PAYMENT_CONFIRMED]
READY_FOR_SHIPMENT → [SHIPPED, PROCESSING]
SHIPPED → [DELIVERED]
DELIVERED → []  (terminal)
CANCELLED → []  (terminal)
```

---

## ✅ Verificación Post-Implementación

```bash
# Debe pasar SIN ERRORES:
mvn clean compile          # 0 errores
mvn test                   # X/X tests PASSED
mvn checkstyle:check       # 0 violations

# Debe contener:
- 4 nuevos estados en OrderStatus
- Clase OrderStatusTransition
- Validación en OrderService
- UI con barra de progreso
- Tests de transiciones
```

---

## 🎓 Documentación Relacionada

- `SPEC.md` — Especificación completa
- `target.json` — Metadatos del REQ
- `COMPARISON-REQ-001-vs-002.md` — Comparativa con anterior REQ
- `GUIA_PRUEBAS_API.md` — Cómo probar los nuevos estados

---

**Última actualización:** 2026-08-10  
**REQ Status:** ✅ Listo para implementación  
**Documento:** Referencia Rápida v1.0
