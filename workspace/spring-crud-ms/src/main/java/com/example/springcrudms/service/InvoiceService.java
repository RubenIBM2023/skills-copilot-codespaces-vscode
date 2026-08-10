package com.example.springcrudms.service;

import com.example.springcrudms.dto.InvoiceDTO;
import com.example.springcrudms.model.*;
import com.example.springcrudms.repository.InvoiceRepository;
import com.example.springcrudms.repository.OrderRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de facturas.
 * Responsable de crear, gestionar y generar PDFs de facturas.
 */
@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final Double DEFAULT_TAX_RATE = 0.16; // 16% IVA

    public InvoiceService(InvoiceRepository invoiceRepository,
                         OrderRepository orderRepository,
                         RabbitTemplate rabbitTemplate) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Crear factura para una orden.
     */
    public InvoiceDTO createInvoice(Long orderId, String taxId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + orderId));

        // Validar que no exista factura previa
        if (order.getInvoiced()) {
            throw new RuntimeException("La orden ya tiene factura: " + order.getOrderNumber());
        }

        // Calcular totales
        Double subtotal = order.getTotalAmount();
        Double taxAmount = subtotal * DEFAULT_TAX_RATE;
        Double totalAmount = subtotal + taxAmount;

        // Crear factura
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setTaxId(taxId);
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setDiscount(0.0);
        invoice.setStatus(InvoiceStatus.DRAFT);

        // Generar PDF
        byte[] pdfContent = generatePDF(invoice, order);
        invoice.setPdfContent(pdfContent);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Marcar orden como facturada
        order.setInvoiced(true);
        order.setInvoice(savedInvoice);
        orderRepository.save(order);

        // Publicar evento (opcional - no debe fallar)
        try {
            rabbitTemplate.convertAndSend(
                "invoice.exchange",
                "invoice.created",
                new InvoiceEvent(savedInvoice.getId(), savedInvoice.getInvoiceNumber(), InvoiceStatus.DRAFT)
            );
            System.out.println("✓ Evento de factura publicado");
        } catch (Exception e) {
            System.err.println("⚠ Advertencia: No se pudo publicar evento de factura: " + e.getMessage());
        }

        return mapToDTO(savedInvoice);
    }

    /**
     * Obtener factura por ID.
     */
    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
        return mapToDTO(invoice);
    }

    /**
     * Obtener facturas por ID de orden.
     */
    public List<InvoiceDTO> getInvoicesByOrderId(Long orderId) {
        return invoiceRepository.findByOrderId(orderId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Listar todas las facturas.
     */
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Cambiar estado de factura.
     */
    public InvoiceDTO updateInvoiceStatus(Long id, InvoiceStatus newStatus) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));

        invoice.setStatus(newStatus);
        invoice.setUpdatedAt(LocalDateTime.now());
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        return mapToDTO(updatedInvoice);
    }

    /**
     * Generar PDF de factura.
     */
    private byte[] generatePDF(Invoice invoice, Order order) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Encabezado
            Paragraph title = new Paragraph("FACTURA", new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph invoiceNum = new Paragraph(
                "Número: " + invoice.getInvoiceNumber(),
                new Font(Font.FontFamily.HELVETICA, 12)
            );
            invoiceNum.setAlignment(Element.ALIGN_RIGHT);
            document.add(invoiceNum);

            Paragraph date = new Paragraph(
                "Fecha: " + invoice.getCreatedAt(),
                new Font(Font.FontFamily.HELVETICA, 10)
            );
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            document.add(new Paragraph(" ")); // Espacio

            // Información del cliente
             Paragraph clientHeader =
                 new Paragraph("DATOS DEL CLIENTE",
                     new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
             document.add(clientHeader);
 
             PdfPTable clientTable = new PdfPTable(2);
             clientTable.setWidthPercentage(100);
             clientTable.addCell(
                 new PdfPCell(new Phrase("Nombre:",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             clientTable.addCell(new PdfPCell(new Phrase(order.getCustomerName())));
             clientTable.addCell(
                 new PdfPCell(new Phrase("Email:",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             clientTable.addCell(new PdfPCell(new Phrase(order.getCustomerEmail())));
             clientTable.addCell(
                 new PdfPCell(new Phrase("Teléfono:",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             clientTable.addCell(new PdfPCell(new Phrase(order.getCustomerPhone())));
             clientTable.addCell(
                 new PdfPCell(new Phrase("Dirección:",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             String address = order.getShippingAddress() + ", " + order.getCity()
                 + ", " + order.getState();
             clientTable.addCell(new PdfPCell(new Phrase(address)));
            document.add(clientTable);

            document.add(new Paragraph(" ")); // Espacio

            // Tabla de items
            Paragraph itemsHeader = new Paragraph("ARTÍCULOS", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
            document.add(itemsHeader);

            PdfPTable itemsTable = new PdfPTable(5);
            itemsTable.setWidthPercentage(100);

             // Encabezados
             itemsTable.addCell(
                 new PdfPCell(new Phrase("Producto",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             itemsTable.addCell(
                 new PdfPCell(new Phrase("Cantidad",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             itemsTable.addCell(
                 new PdfPCell(new Phrase("Precio Unit.",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             itemsTable.addCell(
                 new PdfPCell(new Phrase("Subtotal",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             itemsTable.addCell(
                 new PdfPCell(new Phrase("",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));

            // Filas de items
            for (OrderItem item : order.getItems()) {
                itemsTable.addCell(item.getProductName());
                itemsTable.addCell(item.getQuantity().toString());
                itemsTable.addCell(String.format("$%.2f", item.getProductPrice()));
                itemsTable.addCell(String.format("$%.2f", item.getSubtotal()));
                itemsTable.addCell("");
            }

            document.add(itemsTable);
            document.add(new Paragraph(" ")); // Espacio

            // Resumen de totales
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(50);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

             totalsTable.addCell(
                 new PdfPCell(new Phrase("Subtotal:",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             totalsTable.addCell(
                 new PdfPCell(new Phrase(String.format("$%.2f", invoice.getSubtotal()))));
 
             totalsTable.addCell(
                 new PdfPCell(new Phrase("Impuesto (16%):",
                     new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD))));
             totalsTable.addCell(
                 new PdfPCell(new Phrase(String.format("$%.2f", invoice.getTaxAmount()))));

            PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            totalCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            totalsTable.addCell(totalCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("$%.2f", invoice.getTotalAmount()), 
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            totalValueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            totalsTable.addCell(totalValueCell);

            document.add(totalsTable);

            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph(
                "Gracias por su compra. Factura generada: " + LocalDateTime.now(),
                new Font(Font.FontFamily.HELVETICA, 8)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de factura", e);
        }
    }

    /**
     * Generar número de factura único.
     */
    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }

    /**
     * Obtener contenido PDF de una factura.
     */
    public byte[] getInvoicePDF(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + invoiceId));
        
        if (invoice.getPdfContent() == null || invoice.getPdfContent().length == 0) {
            throw new RuntimeException("La factura no tiene contenido PDF generado");
        }
        
        return invoice.getPdfContent();
    }

    /**
     * Mapear Invoice a InvoiceDTO.
     */
    private InvoiceDTO mapToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setTaxId(invoice.getTaxId());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setDiscount(invoice.getDiscount());
        dto.setStatus(invoice.getStatus());
        dto.setNotes(invoice.getNotes());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setUpdatedAt(invoice.getUpdatedAt());
        if (invoice.getOrder() != null) {
            dto.setOrderId(invoice.getOrder().getId());
        }
        return dto;
    }

    /**
     * Evento simple para RabbitMQ.
     */
    public static class InvoiceEvent {
        public Long invoiceId;
        public String invoiceNumber;
        public InvoiceStatus status;

        public InvoiceEvent(Long invoiceId, String invoiceNumber, InvoiceStatus status) {
            this.invoiceId = invoiceId;
            this.invoiceNumber = invoiceNumber;
            this.status = status;
        }

         // Getters
         public Long getInvoiceId() {
             return invoiceId;
         }
 
         public String getInvoiceNumber() {
             return invoiceNumber;
         }
 
         public InvoiceStatus getStatus() {
             return status;
         }
    }
}
