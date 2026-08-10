package com.example.springcrudms.model;

public enum ShipmentStatus {
    PENDING("Pendiente"),
    PICKED_UP("Recogido"),
    IN_TRANSIT("En Tránsito"),
    OUT_FOR_DELIVERY("En Reparto"),
    DELIVERED("Entregado"),
    DELAYED("Atrasado"),
    FAILED("Fallido");

    private final String displayName;

    ShipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
