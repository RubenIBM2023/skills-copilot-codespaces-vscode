package com.example.springcrudms.repository;

import com.example.springcrudms.model.Shipment;
import com.example.springcrudms.model.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    
    List<Shipment> findByStatus(ShipmentStatus status);
    
    Optional<Shipment> findByOrderId(Long orderId);
}
