package com.example.springcrudms.dto;

public class CreateInvoiceDTO {
    private Long orderId;
    private String taxId;

    public CreateInvoiceDTO() {}

    public CreateInvoiceDTO(Long orderId, String taxId) {
        this.orderId = orderId;
        this.taxId = taxId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
}
