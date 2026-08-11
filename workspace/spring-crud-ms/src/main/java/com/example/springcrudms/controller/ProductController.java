package com.example.springcrudms.controller;

import com.example.springcrudms.dto.ProductPatchDTO;
import com.example.springcrudms.dto.ProductRequestDTO;
import com.example.springcrudms.dto.ProductResponseDTO;
import com.example.springcrudms.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@CrossOrigin(
    origins = {"http://localhost:8080", "http://127.0.0.1:8080", "http://localhost"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "true"
)
@Tag(name = "Products", description = "Product catalog CRUD operations")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ProductResponseDTO create(@Valid @RequestBody ProductRequestDTO request) {
        return productService.create(request);
    }

    @GetMapping
    @Operation(summary = "List all products with pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Products list")
    })
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ProductResponseDTO findById(@PathVariable String id) {
        return productService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product completely")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ProductResponseDTO update(@PathVariable String id,
                                     @Valid @RequestBody ProductRequestDTO request) {
        return productService.update(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update price and/or stock")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product patched"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ProductResponseDTO patch(@PathVariable String id,
                                    @Valid @RequestBody ProductPatchDTO patch) {
        return productService.patch(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Product deleted"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public void delete(@PathVariable String id) {
        productService.delete(id);
    }
}

