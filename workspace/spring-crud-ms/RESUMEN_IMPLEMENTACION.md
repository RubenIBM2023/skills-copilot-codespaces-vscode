# Resumen de Implementación - Sistema de Seguimiento de Pedidos

## Archivos Creados

### 1. Servicios (service/)
- **OrderService.java** - Gestión de órdenes (crear, actualizar estado, listar)
- **ShipmentService.java** - Gestión de envíos y rastreo
- **InvoiceService.java** - Gestión de facturas y generación de PDF
- **StockService.java** - Actualización de stock en MongoDB

### 2. Controladores (controller/)
- **OrderController.java** - REST endpoints para órdenes
- **ShipmentController.java** - REST endpoints para envíos
- **InvoiceController.java** - REST endpoints para facturas

### 3. Configuración (config/)
- **RabbitMQConfig.java** - Configuración de exchanges, queues y bindings

## Archivos Modificados

### 1. application.properties
- Configurado H2 embebido (jdbc:h2:mem:ordersdb)
- Configurado Hibernate DDL (create-drop)
- Configurado RabbitMQ
- Agregado logging DEBUG para desarrollo

### 2. DTOs
- **ShipmentDTO.java** - Agregado orderId
- **InvoiceDTO.java** - Agregado orderId

## Tecnologías Utilizadas

- Spring Boot 3.2.5
- MongoDB (productos) - Embebido con Flapdoodle
- PostgreSQL (órdenes) - H2 embebido para desarrollo
- RabbitMQ - Eventos de cambio de estado
- iText 5.5.13.3 - Generación de PDF de facturas
- Lombok - Simplificación de código (opcional)

## Flujos de Negocio Implementados

### 1. Crear Orden
```
POST /orders
```

Pasos:
1. Valida stock disponible
2. Crea la orden
3. Crea items de orden
4. Actualiza stock en MongoDB
5. Crea shipment automático
6. Publica evento en RabbitMQ

### 2. Rastrear Envío
```
GET /shipments/track/{trackingNumber}
```

### 3. Actualizar Estado de Envío
```
PUT /shipments/{id}/status?status=DELIVERED
```

Cuando cambia a DELIVERED:
- Actualiza la ubicación a "Entregado"
- Cambia estado de orden a DELIVERED

### 4. Crear Factura
```
POST /invoices?orderId=1&taxId=RFC123456
```

Pasos:
1. Valida que la orden exista
2. Calcula impuestos (16% IVA)
3. Genera PDF con datos de cliente, items y totales
4. Guarda PDF en base de datos
5. Publica evento en RabbitMQ

## Endpoints REST

### Órdenes
- POST /orders - Crear orden
- GET /orders - Listar todas las órdenes
- GET /orders/{id} - Obtener orden por ID
- PUT /orders/{id}/status - Actualizar estado de orden

### Envíos
- GET /shipments/track/{trackingNumber} - Rastrear por número
- GET /shipments/{id} - Obtener envío por ID
- GET /shipments - Listar todos los envíos
- PUT /shipments/{id}/status - Actualizar estado de envío
- PUT /shipments/{id}/location - Actualizar ubicación

### Facturas
- POST /invoices - Crear factura
- GET /invoices/{id} - Obtener factura por ID
- GET /invoices/order/{orderId} - Listar facturas de una orden
- GET /invoices - Listar todas las facturas
- PUT /invoices/{id}/status - Cambiar estado de factura

## Base de Datos

### H2 (Desarrollo)
- URL: jdbc:h2:mem:ordersdb
- Console: http://localhost:8080/h2-console
- DDL: create-drop (se crea en cada inicio)

### Tablas
- orders - Órdenes principales
- order_items - Ítems de cada orden
- shipments - Información de envío
- invoices - Facturas generadas

### MongoDB (Productos)
- Base: products_db
- Colección: products
- Embebido con Flapdoodle (sin instalación requerida)

## Compilación y Ejecución

### Compilar
```bash
cd workspace/spring-crud-ms
mvn clean package -DskipTests
```

### Ejecutar
```bash
java -jar target/spring-crud-ms-1.0.0-SNAPSHOT.jar
```

### Acceder a la aplicación
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## Ejemplo de cURL para Crear Orden

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
        "productId": "mongo-product-id-here",
        "quantity": 2
      }
    ]
  }'
```

## Notas Importantes

1. RabbitMQ: Configurado pero sin conexión real requerida
2. H2 Embebido: No requiere instalación de PostgreSQL
3. MongoDB Embebido: Flapdoodle maneja la instancia automáticamente
4. PDFs: Almacenados en tabla invoices.pdf_content como LONGBLOB
5. Stock: Decrementado automáticamente al crear una orden
