package com.example.springcrudms.service;

import com.example.springcrudms.dto.ProductPatchDTO;
import com.example.springcrudms.dto.ProductRequestDTO;
import com.example.springcrudms.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseDTO create(ProductRequestDTO request);
    Page<ProductResponseDTO> findAll(Pageable pageable);
    ProductResponseDTO findById(String id);
    ProductResponseDTO update(String id, ProductRequestDTO request);
    ProductResponseDTO patch(String id, ProductPatchDTO patch);
    void delete(String id);
}
