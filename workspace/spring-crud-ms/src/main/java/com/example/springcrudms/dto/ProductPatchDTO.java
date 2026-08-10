package com.example.springcrudms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class ProductPatchDTO {
    @DecimalMin("0.0")
    private BigDecimal price;

    @Min(0)
    private Integer stock;

    // Constructor vacío
    public ProductPatchDTO() {
    }

    // Constructor con parámetros
    public ProductPatchDTO(BigDecimal price, Integer stock) {
        this.price = price;
        this.stock = stock;
    }

    // Getters y setters
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
