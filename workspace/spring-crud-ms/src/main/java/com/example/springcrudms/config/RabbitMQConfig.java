package com.example.springcrudms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el sistema de pedidos.
 * Define exchanges, queues y bindings para eventos de cambio de estado.
 */
@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String SHIPMENT_EXCHANGE = "shipment.exchange";
    public static final String INVOICE_EXCHANGE = "invoice.exchange";

    // Queue names
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_STATUS_QUEUE = "order.status.queue";
    public static final String SHIPMENT_CREATED_QUEUE = "shipment.created.queue";
    public static final String SHIPMENT_STATUS_QUEUE = "shipment.status.queue";
    public static final String INVOICE_CREATED_QUEUE = "invoice.created.queue";
    public static final String STOCK_UPDATE_QUEUE = "stock.update.queue";

    // Routing keys
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String ORDER_STATUS_ROUTING_KEY = "order.status.*";
    public static final String SHIPMENT_CREATED_ROUTING_KEY = "shipment.created";
    public static final String SHIPMENT_STATUS_ROUTING_KEY = "shipment.status.*";
    public static final String INVOICE_CREATED_ROUTING_KEY = "invoice.created";
    public static final String STOCK_UPDATE_ROUTING_KEY = "stock.update";

    // ===== EXCHANGES =====
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange shipmentExchange() {
        return new TopicExchange(SHIPMENT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange invoiceExchange() {
        return new TopicExchange(INVOICE_EXCHANGE, true, false);
    }

    // ===== ORDER QUEUES =====
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);
    }

    @Bean
    public Queue orderStatusQueue() {
        return new Queue(ORDER_STATUS_QUEUE, true);
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue())
                .to(orderExchange())
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding orderStatusBinding() {
        return BindingBuilder.bind(orderStatusQueue())
                .to(orderExchange())
                .with(ORDER_STATUS_ROUTING_KEY);
    }

    // ===== SHIPMENT QUEUES =====
    @Bean
    public Queue shipmentCreatedQueue() {
        return new Queue(SHIPMENT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue shipmentStatusQueue() {
        return new Queue(SHIPMENT_STATUS_QUEUE, true);
    }

    @Bean
    public Binding shipmentCreatedBinding() {
        return BindingBuilder.bind(shipmentCreatedQueue())
                .to(shipmentExchange())
                .with(SHIPMENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding shipmentStatusBinding() {
        return BindingBuilder.bind(shipmentStatusQueue())
                .to(shipmentExchange())
                .with(SHIPMENT_STATUS_ROUTING_KEY);
    }

    // ===== INVOICE QUEUES =====
    @Bean
    public Queue invoiceCreatedQueue() {
        return new Queue(INVOICE_CREATED_QUEUE, true);
    }

    @Bean
    public Binding invoiceCreatedBinding() {
        return BindingBuilder.bind(invoiceCreatedQueue())
                .to(invoiceExchange())
                .with(INVOICE_CREATED_ROUTING_KEY);
    }

    // ===== STOCK UPDATE QUEUE =====
    @Bean
    public Queue stockUpdateQueue() {
        return new Queue(STOCK_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding stockUpdateBinding() {
        return BindingBuilder.bind(stockUpdateQueue())
                .to(orderExchange())
                .with(STOCK_UPDATE_ROUTING_KEY);
    }
}
