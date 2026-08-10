package com.example.springcrudms.service;

import com.example.springcrudms.dto.CreateOrderDTO;
import com.example.springcrudms.dto.OrderDTO;
import com.example.springcrudms.dto.OrderItemDTO;
import com.example.springcrudms.model.*;
import com.example.springcrudms.repository.OrderRepository;
import com.example.springcrudms.repository.OrderItemRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de pedidos.
 * Responsable de crear, actualizar y listar pedidos.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final StockService stockService;
    private final ShipmentService shipmentService;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductService productService,
                        StockService stockService,
                        ShipmentService shipmentService,
                        RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
        this.stockService = stockService;
        this.shipmentService = shipmentService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Crear una nueva orden.
     * 1. Validar disponibilidad de stock
     * 2. Crear la orden
     * 3. Crear items de orden
     * 4. Actualizar stock
     * 5. Crear shipment automático
     * 6. Publicar evento
     */
    public OrderDTO createOrder(CreateOrderDTO createOrderDTO) {
        System.out.println("=== INICIANDO CREACIÓN DE ORDEN ===");
        System.out.println("Cliente: " + createOrderDTO.getCustomerName());
        System.out.println("Items: " + (createOrderDTO.getItems() != null ? createOrderDTO.getItems().size() : 0));

        // Validar que haya items
        if (createOrderDTO.getItems() == null || createOrderDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos 1 item");
        }

        System.out.println("✓ Validación de items pasada");

        // Crear orden
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerName(createOrderDTO.getCustomerName());
        order.setCustomerEmail(createOrderDTO.getCustomerEmail());
        order.setCustomerPhone(createOrderDTO.getCustomerPhone());
        order.setShippingAddress(createOrderDTO.getShippingAddress());
        order.setCity(createOrderDTO.getCity());
        order.setState(createOrderDTO.getState());
        order.setZipCode(createOrderDTO.getZipCode());
        order.setStatus(OrderStatus.PENDING);

        System.out.println("✓ Orden inicializada: " + order.getOrderNumber());

        // Calcular total
        Double totalAmount = 0.0;

        // Crear items de orden
        for (var itemDTO : createOrderDTO.getItems()) {
            System.out.println("Procesando item: " + itemDTO.getProductName() + " x" + itemDTO.getQuantity());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemDTO.getProductId());
            item.setProductName(itemDTO.getProductName());
            item.setProductPrice(itemDTO.getProductPrice());
            item.setQuantity(itemDTO.getQuantity());
            item.setSubtotal(itemDTO.getProductPrice() * itemDTO.getQuantity());

            order.getItems().add(item);
            totalAmount += item.getSubtotal();
        }

        order.setTotalAmount(totalAmount);
        System.out.println("✓ Total calculado: $" + totalAmount);

        // GUARDAR LA ORDEN
        Order savedOrder = null;
        try {
            savedOrder = orderRepository.save(order);
            System.out.println("✓ Orden guardada en BD con ID: " + savedOrder.getId());
        } catch (Exception e) {
            System.err.println("ERROR guardando orden: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar orden: " + e.getMessage(), e);
        }

        // Actualizar stock en MongoDB (OPCIONAL - no debe fallar la orden)
        try {
            for (var item : savedOrder.getItems()) {
                stockService.updateStock(item.getProductId(), -item.getQuantity());
            }
            System.out.println("✓ Stock actualizado");
        } catch (Exception e) {
            System.err.println("⚠ Advertencia: No se pudo actualizar stock: " + e.getMessage());
        }

        // Crear shipment automático (OPCIONAL - no debe fallar la orden)
        try {
            Shipment shipment = shipmentService.createShipmentForOrder(savedOrder);
            System.out.println("✓ Shipment creado: " + shipment.getTrackingNumber());
        } catch (Exception e) {
            System.err.println("⚠ Advertencia: No se pudo crear shipment: " + e.getMessage());
        }

        // Publicar evento (OPCIONAL - no debe fallar la orden)
        try {
            rabbitTemplate.convertAndSend(
                "order.exchange",
                "order.created",
                new OrderEvent(savedOrder.getId(), savedOrder.getOrderNumber(), OrderStatus.PENDING)
            );
            System.out.println("✓ Evento publicado");
        } catch (Exception e) {
            System.err.println("⚠ Advertencia: No se pudo publicar evento: " + e.getMessage());
        }

        System.out.println("=== ORDEN CREADA EXITOSAMENTE ===");
        return mapToDTO(savedOrder);
    }

    /**
     * Obtener pedido por ID.
     */
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
        return mapToDTO(order);
    }

    /**
     * Listar todos los pedidos.
     */
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Actualizar estado de la orden.
     */
    public OrderDTO updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);

        // Publicar evento de cambio de estado (opcional)
        try {
            rabbitTemplate.convertAndSend(
                "order.exchange",
                "order.status." + newStatus.name().toLowerCase(),
                new OrderEvent(updatedOrder.getId(), updatedOrder.getOrderNumber(), newStatus)
            );
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo publicar evento: " + e.getMessage());
        }

        return mapToDTO(updatedOrder);
    }

    /**
     * Generar número de orden único.
     */
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    /**
     * Mapear Order a OrderDTO.
     */
    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCity(order.getCity());
        dto.setState(order.getState());
        dto.setZipCode(order.getZipCode());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setInvoiced(order.getInvoiced());
        
        // Mapear items
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            var items = order.getItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setProductName(item.getProductName());
                    itemDTO.setProductPrice(item.getProductPrice());
                    itemDTO.setQuantity(item.getQuantity());
                    return itemDTO;
                })
                .collect(Collectors.toList());
            dto.setItems(items);
        }
        
        return dto;
    }

    /**
     * Evento simple para RabbitMQ.
     */
    public static class OrderEvent {
        public Long orderId;
        public String orderNumber;
        public OrderStatus status;

        public OrderEvent(Long orderId, String orderNumber, OrderStatus status) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.status = status;
        }

         // Getters
         public Long getOrderId() {
             return orderId;
         }
 
         public String getOrderNumber() {
             return orderNumber;
         }
 
         public OrderStatus getStatus() {
             return status;
         }
    }
}
