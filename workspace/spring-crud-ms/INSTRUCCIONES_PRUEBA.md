# Instrucciones para Probar la Aplicación

## Requisitos Previos

- Java 17 o superior instalado
- No se requiere instalar MongoDB ni PostgreSQL (están embebidos)
- No se requiere RabbitMQ (con fines de demostración)

## Paso 1: Compilar

```bash
cd workspace/spring-crud-ms
mvn clean package -DskipTests
```

**Resultado esperado:** Build SUCCESS

## Paso 2: Ejecutar la Aplicación

```bash
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

**Resultado esperado:**
```
Started SpringCrudMsApplication in X.XXX seconds
Tomcat started on port(s): 8080
```

## Paso 3: Verificar Salud de la Aplicación

```bash
curl http://localhost:8080/health
```

O acceder a: http://localhost:8080/health

**Resultado esperado:**
```json
{
  "status": "UP"
}
```

## Paso 4: Acceder a Swagger UI

http://localhost:8080/swagger-ui.html

Aquí se pueden explorar y probar todos los endpoints interactivamente.

## Paso 5: Acceder a H2 Console

http://localhost:8080/h2-console

- Driver: org.h2.Driver
- JDBC URL: jdbc:h2:mem:ordersdb
- User: sa
- Password: (vacío)

Hacer clic en "Connect" para ver las tablas y datos.

## Paso 6: Crear un Producto (Prerequisito)

Primero, necesitas crear un producto en MongoDB. Usa la interfaz Swagger o:

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell",
    "description": "Laptop de alta performance",
    "price": 1299.99,
    "stock": 10,
    "category": "Electrónica"
  }'
```

**Guarda el ID del producto retornado (ej: 507f1f77bcf86cd799439011)**

## Paso 7: Crear una Orden

Reemplaza `PRODUCT_ID` con el ID obtenido en el paso anterior:

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Juan Pérez",
    "customerEmail": "juan@example.com",
    "customerPhone": "555-1234",
    "shippingAddress": "Calle 123 Apt 45",
    "city": "México",
    "state": "CDMX",
    "zipCode": "06600",
    "items": [
      {
        "productId": "PRODUCT_ID",
        "quantity": 2
      }
    ]
  }'
```

**Respuesta esperada:**
```json
{
  "id": 1,
  "orderNumber": "ORD-1723311658123",
  "customerName": "Juan Pérez",
  "customerEmail": "juan@example.com",
  "status": "PENDING",
  "totalAmount": 2599.98,
  "createdAt": "2026-08-10T11:27:38.123Z",
  "updatedAt": "2026-08-10T11:27:38.123Z",
  "invoiced": false
}
```

**Guarda el ID de la orden (1 en este ejemplo)**

## Paso 8: Listar Órdenes

```bash
curl http://localhost:8080/orders
```

Deberías ver la orden creada en la respuesta.

## Paso 9: Obtener Detalles de la Orden

Reemplaza `ORDER_ID` con el ID de la orden:

```bash
curl http://localhost:8080/orders/1
```

## Paso 10: Obtener Tracking del Envío

El shipment se crea automáticamente. Busca el número de rastreo en la orden y úsalo:

```bash
curl http://localhost:8080/shipments/track/TRK-1723311658123-XXXX
```

## Paso 11: Cambiar Estado del Envío

Reemplaza `SHIPMENT_ID` (normalmente 1 para el primer envío):

```bash
curl -X PUT "http://localhost:8080/shipments/1/status?status=PICKED_UP"
```

## Paso 12: Actualizar Ubicación del Envío

```bash
curl -X PUT "http://localhost:8080/shipments/1/location?location=Centro de Distribución México"
```

## Paso 13: Cambiar Estado a Entregado

```bash
curl -X PUT "http://localhost:8080/shipments/1/status?status=DELIVERED"
```

Esto automáticamente cambiará el estado de la orden a DELIVERED.

## Paso 14: Crear Factura

Reemplaza `ORDER_ID` con 1 y `TAX_ID` con un RFC válido:

```bash
curl -X POST "http://localhost:8080/invoices?orderId=1&taxId=ABC123456XYZ"
```

**Respuesta esperada:**
```json
{
  "id": 1,
  "orderId": 1,
  "invoiceNumber": "INV-1723311658345",
  "subtotal": 2599.98,
  "taxAmount": 415.9968,
  "totalAmount": 3015.9768,
  "status": "DRAFT",
  "createdAt": "2026-08-10T11:28:45.345Z"
}
```

## Paso 15: Listar Facturas

```bash
curl http://localhost:8080/invoices
```

## Paso 16: Obtener Factura por ID

```bash
curl http://localhost:8080/invoices/1
```

## Verificaciones Finales

### Verificar H2 Console

1. Ir a http://localhost:8080/h2-console
2. Conectar con los datos por defecto
3. Ejecutar:
   - SELECT * FROM ORDERS;
   - SELECT * FROM ORDER_ITEMS;
   - SELECT * FROM SHIPMENTS;
   - SELECT * FROM INVOICES;

### Verificar Logs

En la consola donde ejecutaste la aplicación, deberías ver:
- Inicialización de H2
- Inicialización de MongoDB embebido
- Inicialización de RabbitMQ
- Logs de cada operación

## Troubleshooting

### Error de puerto en uso
Si el puerto 8080 está en uso:
```bash
java -Dserver.port=8081 -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

### Error de memoria
```bash
java -Xmx1024m -Xms512m -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

### Ver logs detallados
```bash
java -Dlogging.level.com.example.springcrudms=DEBUG -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

## Arquitectura de Base de Datos

### Orders
- id (Long, PK)
- orderNumber (String)
- customerName (String)
- customerEmail (String)
- customerPhone (String)
- shippingAddress (String)
- city (String)
- state (String)
- zipCode (String)
- status (OrderStatus: PENDING, PROCESSING, SHIPPED, DELIVERED)
- totalAmount (Double)
- invoiced (Boolean)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

### OrderItems
- id (Long, PK)
- orderId (Long, FK)
- productId (String)
- productName (String)
- productPrice (Double)
- quantity (Integer)
- subtotal (Double)

### Shipments
- id (Long, PK)
- orderId (Long, FK)
- trackingNumber (String)
- status (ShipmentStatus: PENDING, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, DELAYED, FAILED)
- carrier (String)
- currentLocation (String)
- shippedDate (LocalDateTime)
- deliveredDate (LocalDateTime)
- estimatedDelivery (LocalDateTime)
- notes (String)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

### Invoices
- id (Long, PK)
- orderId (Long, FK)
- invoiceNumber (String)
- taxId (String)
- subtotal (Double)
- taxAmount (Double)
- totalAmount (Double)
- discount (Double)
- status (InvoiceStatus: DRAFT, ISSUED, PAID, CANCELLED)
- pdfContent (byte[])
- notes (String)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

## Éxito

Una vez completados todos los pasos, la aplicación estará funcionando completamente con:
- Órdenes creadas y rastreables
- Envíos automáticos con seguimiento
- Facturas generadas en PDF
- Stock actualizado automáticamente
- Base de datos embebida sin configuración
- API REST completa y documentada en Swagger
