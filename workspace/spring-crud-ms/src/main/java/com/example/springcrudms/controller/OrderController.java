package com.example.springcrudms.controller;

import com.example.springcrudms.dto.CreateOrderDTO;
import com.example.springcrudms.dto.OrderDTO;
import com.example.springcrudms.model.OrderStatus;
import com.example.springcrudms.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para gestión de pedidos.
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin(
    origins = {"http://localhost:8080", "http://127.0.0.1:8080", "http://localhost"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "true"
)
@Tag(name = "Órdenes", description = "Endpoints para gestión de órdenes de compra")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Crear una nueva orden.
     * POST /orders
     */
    @PostMapping
    @Operation(summary = "Crear nueva orden", description = "Crea una nueva orden con los items especificados")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        OrderDTO order = orderService.createOrder(createOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Obtener una orden por ID.
     * GET /orders/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID", description = "Retrieves order details by ID")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Listar todas las órdenes.
     * GET /orders
     */
    @GetMapping
    @Operation(summary = "Listar todas las órdenes", description = "Retrieves all orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Actualizar estado de una orden.
     * PUT /orders/{id}/status
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de orden", description = "Updates the status of an order")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }
}
