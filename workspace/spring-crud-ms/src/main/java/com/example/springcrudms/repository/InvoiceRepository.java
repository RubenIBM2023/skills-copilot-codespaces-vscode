package com.example.springcrudms.repository;

import com.example.springcrudms.model.Invoice;
import com.example.springcrudms.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    Optional<Invoice> findByOrderId(Long orderId);
    
    List<Invoice> findByStatus(InvoiceStatus status);
}
