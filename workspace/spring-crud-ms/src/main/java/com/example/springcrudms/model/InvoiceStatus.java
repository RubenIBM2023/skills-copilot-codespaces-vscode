package com.example.springcrudms.model;

public enum InvoiceStatus {
    DRAFT("Borrador"),
    ISSUED("Emitida"),
    PAID("Pagada"),
    PARTIALLY_PAID("Parcialmente Pagada"),
    CANCELLED("Cancelada");

    private final String displayName;

    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
