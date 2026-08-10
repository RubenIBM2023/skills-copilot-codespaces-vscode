package com.example.springcrudms.service;

import com.example.springcrudms.model.Product;
import com.example.springcrudms.repository.ProductRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de gestión de stock en MongoDB.
 * Responsable de actualizar el stock de productos cuando se crean ordenes.
 */
@Service
@Transactional
public class StockService {

    private final ProductRepository productRepository;

    public StockService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Actualizar stock de un producto.
     * cantidad positiva = aumentar stock
     * cantidad negativa = disminuir stock
     */
    public void updateStock(String productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        Integer newStock = product.getStock() + quantity;

        if (newStock < 0) {
            throw new RuntimeException(
                "Stock insuficiente para producto: " + product.getName() +
                ". Disponible: " + product.getStock() +
                ", Solicitado: " + Math.abs(quantity)
            );
        }

        product.setStock(newStock);
        productRepository.save(product);
    }

    /**
     * Obtener stock actual de un producto.
     */
    public Integer getStock(String productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));
        return product.getStock();
    }

    /**
     * Listener para eventos de actualización de stock desde RabbitMQ.
     */
    @RabbitListener(queues = "stock.update.queue")
    public void handleStockUpdate(StockUpdateEvent event) {
        try {
            updateStock(event.getProductId(), event.getQuantity());
            System.out.println("Stock actualizado para producto: " + event.getProductId() +
                             ", cantidad: " + event.getQuantity());
        } catch (Exception e) {
            System.err.println("Error actualizando stock: " + e.getMessage());
        }
    }

    /**
     * Evento de actualización de stock.
     */
    public static class StockUpdateEvent {
        public String productId;
        public Integer quantity;

        public StockUpdateEvent() {
        }

        public StockUpdateEvent(String productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

         // Getters y Setters
         public String getProductId() {
             return productId;
         }
 
         public void setProductId(String productId) {
             this.productId = productId;
         }
 
         public Integer getQuantity() {
             return quantity;
         }
 
         public void setQuantity(Integer quantity) {
             this.quantity = quantity;
         }
    }
}
