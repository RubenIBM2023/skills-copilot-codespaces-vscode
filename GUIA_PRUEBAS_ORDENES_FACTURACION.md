# GUÍA DE PRUEBAS - FLUJO DE PEDIDOS Y FACTURACIÓN

## 🎯 Descripción General

Esta interfaz permite:
1. **Crear Pedidos** - Agregar productos y datos del cliente
2. **Gestionar Estado de Pedidos** - Confirmar, enviar, entregar, cancelar
3. **Seguimiento de Envíos** - Ver timeline del pedido
4. **Generar Facturas** - Crear facturas PDF con impuestos
5. **Descargar Facturas** - Obtener PDFs de facturas

---

## 🚀 Acceso a la Interfaz

**URL:** http://localhost:8080/orders.html

La interfaz contiene 6 pestañas principales:
1. **PRODUCTOS** - Listar productos disponibles
2. **CREAR PEDIDO** - Formulario para crear nuevo pedido
3. **MIS PEDIDOS** - Ver todos los pedidos
4. **SEGUIMIENTO** - Rastrear pedidos por número
5. **FACTURAS** - Ver todas las facturas generadas
6. **PENDIENTES** - Resumen de tareas pendientes

---

## 📝 PRUEBA 1: CREAR UN NUEVO PEDIDO

### Paso 1: Acceder a la pestaña "CREAR PEDIDO"
1. Abre http://localhost:8080/orders.html
2. Haz clic en la pestaña **"CREAR PEDIDO"**

### Paso 2: Agregar Productos al Carrito
1. Haz clic en **"AGREGAR PRODUCTOS"**
2. Se abre la pestaña de PRODUCTOS
3. Selecciona un producto de la lista (ej: "Laptop")
4. Ingresa cantidad
5. Haz clic en **"Agregar al Carrito"**

**Resultado esperado:** 
- El producto aparece en la sección "Productos del Pedido"
- El total se actualiza automáticamente

### Paso 3: Rellenar Datos del Cliente
En el formulario "Crear Nuevo Pedido", completa:
- **Nombre del Cliente:** "Juan García"
- **Email:** "juan@example.com"
- **Teléfono:** "+34 612 345 678"
- **Dirección:** "Calle Principal 123"
- **Ciudad:** "Madrid"
- **Estado/Provincia:** "Madrid"
- **Código Postal:** "28001"

### Paso 4: Enviar Pedido
1. Haz clic en **"CREAR PEDIDO"**

**Resultado esperado:**
- ✅ Mensaje de éxito con número de pedido
- ✅ El formulario se limpia
- ✅ Total se reinicia a 0.00

---

## 📋 PRUEBA 2: VER LISTA DE PEDIDOS

### Paso 1: Abrir pestaña "MIS PEDIDOS"
1. Haz clic en **"MIS PEDIDOS"**

**Resultado esperado:**
- Tabla con todos los pedidos creados
- Columnas: Número de Pedido, Cliente, Total, Estado, Fecha, Acciones
- El pedido recién creado aparece con estado **"PENDING"**

### Paso 2: Verificar Información
- Nombre del cliente es correcto
- Total es la suma de (cantidad × precio)
- Fecha es la actual

---

## 👁️ PRUEBA 3: VER DETALLES DEL PEDIDO

### Paso 1: Abrir Detalles
1. En la tabla de "MIS PEDIDOS", haz clic en **"Ver"** en el pedido recién creado

**Resultado esperado:**
Se abre un modal con:
- **Información del Cliente:**
  - Nombre, Email, Teléfono
  - Dirección completa
- **Productos del Pedido:**
  - Lista de items con cantidad, precio unitario, subtotal
- **Total:** $X.XX
- **Botones para cambiar estado**

### Paso 2: Verificar Información
- Todos los datos del cliente son correctos
- Los productos y cantidades coinciden
- Total es correcto

---

## 🔄 PRUEBA 4: CAMBIAR ESTADO DEL PEDIDO

### Paso 1: Cambiar a CONFIRMED
1. En el modal de detalles, haz clic en **"✓ Confirmar"**

**Resultado esperado:**
- ✅ Confirmación de cambio de estado
- ✅ Estado cambia de "PENDING" a "CONFIRMED"
- ✅ Se muestra en naranja/azul

### Paso 2: Cambiar a SHIPPED
1. Haz clic en **"📦 Enviar"**

**Resultado esperado:**
- ✅ Estado cambia a "SHIPPED"

### Paso 3: Cambiar a IN_TRANSIT
1. Haz clic en **"🚚 En Tránsito"**

**Resultado esperado:**
- ✅ Estado cambia a "IN_TRANSIT"

### Paso 4: Cambiar a DELIVERED
1. Haz clic en **"✓ Entregado"**

**Resultado esperado:**
- ✅ Estado cambia a "DELIVERED"
- ✅ Muestra badge en verde

---

## 📦 PRUEBA 5: SEGUIMIENTO DE PEDIDO

### Paso 1: Abrir Pestaña "SEGUIMIENTO"
1. Haz clic en **"SEGUIMIENTO"**

### Paso 2: Buscar Pedido
1. Ingresa el número de pedido en el campo "Búsqueda"
   - Ej: "ORD-20260810-001"
2. Haz clic en **"BUSCAR"**

**Resultado esperado:**
Se muestra información del pedido con:
- **Timeline Visual:**
  - Pedido Creado ✓ (verde)
  - Confirmado ✓ (verde)
  - Enviado ✓ (verde)
  - En Tránsito ✓ (verde)
  - Entregado ✓ (verde)

- **Información del Envío:**
  - Tracking Number
  - Carrier (Transportista)
  - Ubicación Actual
  - Notas

---

## 💰 PRUEBA 6: GENERAR FACTURA

### Paso 1: Abrir Detalles del Pedido
1. Vuelve a la pestaña **"MIS PEDIDOS"**
2. Haz clic en **"Ver"** en el pedido

### Paso 2: Generar Factura
1. En el modal de detalles, haz clic en **"Facturar"**

**Resultado esperado:**
- ✅ Mensaje de éxito "Factura generada exitosamente"
- ✅ En el modal se muestra sección **"📄 Factura"** con:
  - Número de Factura
  - Estado: "PENDING" (se cambiará después)
  - Subtotal: $X.XX
  - Impuestos (16%): $X.XX
  - Total: $X.XX

### Paso 3: Verificar Cálculo de Impuestos
- Impuestos = Subtotal × 0.16
- Total = Subtotal + Impuestos

**Ejemplo:**
- Subtotal: $100.00
- Impuestos (16%): $16.00
- Total: $116.00

---

## 📥 PRUEBA 7: VER LISTA DE FACTURAS

### Paso 1: Abrir Pestaña "FACTURAS"
1. Haz clic en **"FACTURAS"**

**Resultado esperado:**
Tabla con todas las facturas:
- Número de Factura
- Orden (número de pedido asociado)
- Subtotal
- Impuestos
- Total (en negrita)
- Estado: "PENDING"
- Botón: "📥 Descargar PDF"

### Paso 2: Verificar Factura Creada
- Aparece la factura recién generada
- Los montos coinciden con el pedido

---

## 📄 PRUEBA 8: DESCARGAR FACTURA PDF

### Paso 1: Descargar
1. En la tabla de FACTURAS, haz clic en **"📥 Descargar PDF"**

**Resultado esperado:**
- Se abre/descarga un PDF con:
  - Número de factura
  - Datos del cliente
  - Listado de productos
  - Subtotal
  - Impuestos (16%)
  - Total
  - Fecha

---

## ✅ PRUEBA 9: VALIDACIONES

### Test 9.1: Crear pedido sin productos
1. Ve a **"CREAR PEDIDO"**
2. Completa datos del cliente
3. Haz clic en **"CREAR PEDIDO"** SIN agregar productos

**Resultado esperado:**
- Error: "Debes agregar al menos un producto"

### Test 9.2: Datos de cliente incompletos
1. En **"CREAR PEDIDO"**, deja un campo requerido vacío
2. Intenta crear pedido

**Resultado esperado:**
- Error: El navegador valida los campos requeridos

### Test 9.3: Email inválido
1. Ingresa un email sin formato válido: "notanemail"
2. Intenta crear pedido

**Resultado esperado:**
- Error de validación de email

---

## 🔄 PRUEBA 10: FLUJO COMPLETO (E2E)

### Paso a Paso:
1. Crear producto (en PRODUCTOS)
2. Crear pedido (CREAR PEDIDO)
3. Cambiar estado: PENDING → CONFIRMED
4. Cambiar estado: CONFIRMED → SHIPPED
5. Ver en SEGUIMIENTO
6. Cambiar estado: SHIPPED → DELIVERED
7. Generar factura
8. Ver factura en lista
9. Descargar PDF

**Resultado esperado:**
- Todo funciona sin errores
- Los datos se persisten correctamente
- La factura PDF se puede descargar

---

## 🐛 CASOS DE ERROR A PROBAR

| Caso | Pasos | Resultado Esperado |
|------|-------|-------------------|
| Buscar pedido inexistente | SEGUIMIENTO → "XYZ123" | Error "Pedido no encontrado" |
| Cancelar pedido entregado | MIS PEDIDOS → Cambiar a CANCELLED | Debería funcionar |
| Generar factura 2x | Generar factura 2 veces | Se actualiza o da error |

---

## 📊 API ENDPOINTS UTILIZADOS

| Función | Method | Endpoint |
|---------|--------|----------|
| Crear Pedido | POST | /api/v1/orders |
| Listar Pedidos | GET | /api/v1/orders |
| Ver Detalles | GET | /api/v1/orders/{id} |
| Cambiar Estado | PUT | /api/v1/orders/{id}/status |
| Crear Factura | POST | /api/v1/invoices |
| Listar Facturas | GET | /api/v1/invoices |
| Descargar PDF | GET | /api/v1/invoices/{id}/pdf |
| Rastrear Envío | GET | /api/v1/shipments/track/{number} |

---

## 📱 RESPUESTAS ESPERADAS

### Crear Pedido (201 Created)
```json
{
  "id": 1,
  "orderNumber": "ORD-20260810-001",
  "customerName": "Juan García",
  "customerEmail": "juan@example.com",
  "totalAmount": 1299.99,
  "status": "PENDING",
  "items": []
}
```

### Generar Factura (201 Created)
```json
{
  "id": 1,
  "invoiceNumber": "INV-20260810-001",
  "subtotal": 1299.99,
  "taxAmount": 207.99,
  "totalAmount": 1507.98,
  "status": "PENDING"
}
```

---

## ✨ RESUMEN DE PRUEBAS

| Funcionalidad | Estado |
|---------------|--------|
| Crear Pedido | A Probar |
| Listar Pedidos | A Probar |
| Ver Detalles | A Probar |
| Cambiar Estado | A Probar |
| Seguimiento | A Probar |
| Generar Factura | A Probar |
| Ver Facturas | A Probar |
| Descargar PDF | A Probar |
| Validaciones | A Probar |
| Flujo E2E | A Probar |

---

## 🎯 Notas Finales

- El servidor debe estar ejecutando en http://localhost:8080
- MongoDB embebido se reinicia con la aplicación (datos transitorios)
- Los PDFs se generan dinámicamente
- Los impuestos se aplican automáticamente (16%)
- Los datos se almacenan en la base de datos

---

**Servidor activo en http://localhost:8080**
**Interfaz de Pedidos en http://localhost:8080/orders.html**
