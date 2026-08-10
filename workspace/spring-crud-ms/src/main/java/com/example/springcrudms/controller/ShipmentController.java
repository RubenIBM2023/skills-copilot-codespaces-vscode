package com.example.springcrudms.controller;

import com.example.springcrudms.dto.ShipmentDTO;
import com.example.springcrudms.model.ShipmentStatus;
import com.example.springcrudms.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para gestión de envíos.
 */
@RestController
@RequestMapping("/shipments")
@CrossOrigin(
    origins = {"http://localhost:8080", "http://127.0.0.1:8080", "http://localhost"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "true"
)
@Tag(name = "Envíos", description = "Endpoints para gestión de envíos y rastreo")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    /**
     * Obtener envío por número de rastreo.
     * GET /shipments/track/{trackingNumber}
     */
    @GetMapping("/track/{trackingNumber}")
    @Operation(summary = "Rastrear envío por número", description = "Get shipment details by tracking number")
    public ResponseEntity<ShipmentDTO> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        ShipmentDTO shipment = shipmentService.getShipmentByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(shipment);
    }

    /**
     * Obtener envío por ID.
     * GET /shipments/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener envío por ID", description = "Get shipment details by ID")
    public ResponseEntity<ShipmentDTO> getShipmentById(@PathVariable Long id) {
        ShipmentDTO shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(shipment);
    }

    /**
     * Listar todos los envíos.
     * GET /shipments
     */
    @GetMapping
    @Operation(summary = "Listar todos los envíos", description = "Get all shipments")
    public ResponseEntity<List<ShipmentDTO>> getAllShipments() {
        List<ShipmentDTO> shipments = shipmentService.getAllShipments();
        return ResponseEntity.ok(shipments);
    }

    /**
     * Actualizar estado de envío.
     * PUT /shipments/{id}/status
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado del envío", description = "Update the status of a shipment")
    public ResponseEntity<ShipmentDTO> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam ShipmentStatus status) {
        ShipmentDTO updatedShipment = shipmentService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(updatedShipment);
    }

    /**
     * Actualizar ubicación del envío.
     * PUT /shipments/{id}/location
     */
    @PutMapping("/{id}/location")
    @Operation(summary = "Actualizar ubicación del envío", description = "Update the current location of a shipment")
    public ResponseEntity<ShipmentDTO> updateShipmentLocation(
            @PathVariable Long id,
            @RequestParam String location) {
        ShipmentDTO updatedShipment = shipmentService.updateShipmentLocation(id, location);
        return ResponseEntity.ok(updatedShipment);
    }
}
