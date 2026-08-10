package com.example.springcrudms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductRequestDTO {
    @NotBlank
    @Size(max = 100)
    @JsonProperty("name")
    private String name;

    @Size(max = 500)
    @JsonProperty("description")
    private String description;

    @NotNull
    @DecimalMin("0.0")
    @JsonProperty("price")
    private BigDecimal price;

    @NotNull
    @Min(0)
    @JsonProperty("stock")
    private Integer stock;

    @NotBlank
    @JsonProperty("category")
    private String category;

    // Constructors
    public ProductRequestDTO() {
    }

    public ProductRequestDTO(String name, String description, BigDecimal price, Integer stock, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
