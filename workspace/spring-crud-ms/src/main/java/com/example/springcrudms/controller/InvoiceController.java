package com.example.springcrudms.controller;

import com.example.springcrudms.dto.InvoiceDTO;
import com.example.springcrudms.dto.CreateInvoiceDTO;
import com.example.springcrudms.model.InvoiceStatus;
import com.example.springcrudms.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para gestión de facturas.
 */
@RestController
@RequestMapping("/invoices")
@CrossOrigin(
    origins = {"http://localhost:8080", "http://127.0.0.1:8080", "http://localhost"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "true"
)
@Tag(name = "Facturas", description = "Endpoints para gestión de facturas y generación de PDFs")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * Crear una nueva factura para una orden.
     * POST /invoices
     */
    @PostMapping
    @Operation(summary = "Crear nueva factura", description = "Creates a new invoice for an order")
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody CreateInvoiceDTO createInvoiceDTO) {
        System.out.println("=== CREANDO FACTURA ===");
        System.out.println("Order ID: " + createInvoiceDTO.getOrderId());
        System.out.println("Tax ID: " + createInvoiceDTO.getTaxId());
        
        // Si no se proporciona taxId, usar uno por defecto
        String taxId = createInvoiceDTO.getTaxId();
        if (taxId == null || taxId.isEmpty()) {
            taxId = "RFC-" + System.currentTimeMillis();
        }
        
        InvoiceDTO invoice = invoiceService.createInvoice(createInvoiceDTO.getOrderId(), taxId);
        System.out.println("=== FACTURA CREADA: " + invoice.getInvoiceNumber() + " ===");
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    /**
     * Obtener factura por ID.
     * GET /invoices/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener factura por ID", description = "Get invoice details by ID")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }

    /**
     * Listar facturas por ID de orden.
     * GET /invoices/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Obtener facturas de una orden", description = "Get all invoices for a specific order")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByOrderId(@PathVariable Long orderId) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByOrderId(orderId);
        return ResponseEntity.ok(invoices);
    }

    /**
     * Listar todas las facturas.
     * GET /invoices
     */
    @GetMapping
    @Operation(summary = "Listar todas las facturas", description = "Get all invoices")
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    /**
     * Cambiar estado de factura.
     * PUT /invoices/{id}/status
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de factura", description = "Update the status of an invoice")
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status) {
        InvoiceDTO updatedInvoice = invoiceService.updateInvoiceStatus(id, status);
        return ResponseEntity.ok(updatedInvoice);
    }

    /**
     * Descargar PDF de factura.
     * GET /invoices/{id}/pdf
     */
    @GetMapping("/{id}/pdf")
    @Operation(summary = "Descargar PDF de factura", description = "Download invoice as PDF")
    public ResponseEntity<byte[]> downloadInvoicePDF(@PathVariable Long id) {
        try {
            InvoiceDTO invoice = invoiceService.getInvoiceById(id);
            
            // Obtener contenido PDF desde el servicio
            byte[] pdfContent = invoiceService.getInvoicePDF(id);
            
            if (pdfContent == null || pdfContent.length == 0) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=Factura_" + invoice.getInvoiceNumber() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (Exception e) {
            System.err.println("Error descargando PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
