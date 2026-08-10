package com.example.springcrudms.service;

import com.example.springcrudms.dto.ProductPatchDTO;
import com.example.springcrudms.dto.ProductRequestDTO;
import com.example.springcrudms.dto.ProductResponseDTO;
import com.example.springcrudms.exception.ProductNotFoundException;
import com.example.springcrudms.model.Product;
import com.example.springcrudms.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO create(ProductRequestDTO request) {
        Product product = toEntity(request);
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Override
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(toResponseFn());
    }

    @Override
    public ProductResponseDTO findById(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return toResponse(product);
    }

    @Override
    public ProductResponseDTO update(String id, ProductRequestDTO request) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setCategory(request.getCategory());
        Product saved = productRepository.save(existing);
        return toResponse(saved);
    }

    @Override
    public ProductResponseDTO patch(String id, ProductPatchDTO patch) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        if (patch.getPrice() != null) {
            existing.setPrice(patch.getPrice());
        }
        if (patch.getStock() != null) {
            existing.setStock(patch.getStock());
        }
        Product saved = productRepository.save(existing);
        return toResponse(saved);
    }

    @Override
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    private Product toEntity(ProductRequestDTO request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        return product;
    }

    private ProductResponseDTO toResponse(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategory(product.getCategory());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    private Function<Product, ProductResponseDTO> toResponseFn() {
        return this::toResponse;
    }
}
