package com.example.springcrudms.service;

import com.example.springcrudms.dto.ShipmentDTO;
import com.example.springcrudms.model.Order;
import com.example.springcrudms.model.Shipment;
import com.example.springcrudms.model.ShipmentStatus;
import com.example.springcrudms.model.OrderStatus;
import com.example.springcrudms.repository.ShipmentRepository;
import com.example.springcrudms.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de envíos.
 * Responsable de crear, actualizar y rastrear envíos.
 */
@Service
@Transactional
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public ShipmentService(ShipmentRepository shipmentRepository,
                          OrderRepository orderRepository,
                          RabbitTemplate rabbitTemplate) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Crear shipment automático para una orden.
     */
    public Shipment createShipmentForOrder(Order order) {
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setCarrier("FedEx"); // Valor por defecto
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(5));

        Shipment savedShipment = shipmentRepository.save(shipment);
        order.setShipment(savedShipment);
        orderRepository.save(order);

        // Publicar evento
        rabbitTemplate.convertAndSend(
            "shipment.exchange",
            "shipment.created",
            new ShipmentEvent(savedShipment.getId(), savedShipment.getTrackingNumber(), ShipmentStatus.PENDING)
        );

        return savedShipment;
    }

    /**
     * Obtener shipment por tracking number.
     */
    public ShipmentDTO getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado con número de rastreo: " + trackingNumber));
        return mapToDTO(shipment);
    }

    /**
     * Obtener shipment por ID.
     */
    public ShipmentDTO getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado con ID: " + id));
        return mapToDTO(shipment);
    }

    /**
     * Listar todos los envíos.
     */
    public List<ShipmentDTO> getAllShipments() {
        return shipmentRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Actualizar estado de envío.
     * Si el estado cambia a DELIVERED, también actualizar el estado de la orden.
     */
    public ShipmentDTO updateShipmentStatus(Long id, ShipmentStatus newStatus) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado con ID: " + id));

        ShipmentStatus oldStatus = shipment.getStatus();
        shipment.setStatus(newStatus);
        shipment.setUpdatedAt(LocalDateTime.now());

        // Registrar fechas según el estado
        if (newStatus == ShipmentStatus.PICKED_UP) {
            shipment.setShippedDate(LocalDateTime.now());
            shipment.setCurrentLocation("En tránsito");
        } else if (newStatus == ShipmentStatus.DELIVERED) {
            shipment.setDeliveredDate(LocalDateTime.now());
            shipment.setCurrentLocation("Entregado");

            // Actualizar orden a DELIVERED
            Order order = shipment.getOrder();
            order.setStatus(OrderStatus.DELIVERED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        Shipment updatedShipment = shipmentRepository.save(shipment);

        // Publicar evento
        rabbitTemplate.convertAndSend(
            "shipment.exchange",
            "shipment.status." + newStatus.name().toLowerCase(),
            new ShipmentEvent(updatedShipment.getId(), updatedShipment.getTrackingNumber(), newStatus)
        );

        return mapToDTO(updatedShipment);
    }

    /**
     * Actualizar ubicación del envío.
     */
    public ShipmentDTO updateShipmentLocation(Long id, String location) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Envío no encontrado con ID: " + id));

        shipment.setCurrentLocation(location);
        shipment.setUpdatedAt(LocalDateTime.now());
        Shipment updatedShipment = shipmentRepository.save(shipment);

        return mapToDTO(updatedShipment);
    }

    /**
     * Generar número de rastreo único.
     */
    private String generateTrackingNumber() {
        return "TRK-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }

    /**
     * Mapear Shipment a ShipmentDTO.
     */
    private ShipmentDTO mapToDTO(Shipment shipment) {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(shipment.getId());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setStatus(shipment.getStatus());
        dto.setCarrier(shipment.getCarrier());
        dto.setCurrentLocation(shipment.getCurrentLocation());
        dto.setShippedDate(shipment.getShippedDate());
        dto.setDeliveredDate(shipment.getDeliveredDate());
        dto.setEstimatedDelivery(shipment.getEstimatedDelivery());
        dto.setNotes(shipment.getNotes());
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setUpdatedAt(shipment.getUpdatedAt());
        return dto;
    }

    /**
     * Evento simple para RabbitMQ.
     */
    public static class ShipmentEvent {
        public Long shipmentId;
        public String trackingNumber;
        public ShipmentStatus status;

        public ShipmentEvent(Long shipmentId, String trackingNumber, ShipmentStatus status) {
            this.shipmentId = shipmentId;
            this.trackingNumber = trackingNumber;
            this.status = status;
        }

         // Getters
         public Long getShipmentId() {
             return shipmentId;
         }
 
         public String getTrackingNumber() {
             return trackingNumber;
         }
 
         public ShipmentStatus getStatus() {
             return status;
         }
    }
}
