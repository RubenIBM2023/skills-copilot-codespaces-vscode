package com.example.springcrudms.repository;

import com.example.springcrudms.model.Order;
import com.example.springcrudms.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByCustomerEmail(String customerEmail);
    
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}
