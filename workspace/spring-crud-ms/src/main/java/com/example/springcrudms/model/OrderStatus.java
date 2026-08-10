package com.example.springcrudms.model;

public enum OrderStatus {
    PENDING("Pendiente"),
    CONFIRMED("Confirmado"),
    SHIPPED("Enviado"),
    IN_TRANSIT("En Tránsito"),
    DELIVERED("Entregado"),
    CANCELLED("Cancelado");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
