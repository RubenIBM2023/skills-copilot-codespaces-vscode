package com.example.springcrudms.service;

import com.example.springcrudms.dto.ProductPatchDTO;
import com.example.springcrudms.dto.ProductRequestDTO;
import com.example.springcrudms.dto.ProductResponseDTO;
import com.example.springcrudms.exception.ProductNotFoundException;
import com.example.springcrudms.model.Product;
import com.example.springcrudms.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId("1");
        product.setName("Test Product");
        product.setDescription("Description");
        product.setPrice(new BigDecimal("9.99"));
        product.setStock(10);
        product.setCategory("Electronics");

        requestDTO = new ProductRequestDTO();
        requestDTO.setName("Test Product");
        requestDTO.setDescription("Description");
        requestDTO.setPrice(new BigDecimal("9.99"));
        requestDTO.setStock(10);
        requestDTO.setCategory("Electronics");
    }

    @Test
    void create_ShouldReturnProductResponse() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductResponseDTO response = productService.create(requestDTO);
        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void findAll_ShouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(pageable)).thenReturn(page);
        Page<ProductResponseDTO> result = productService.findAll(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findById_WhenExists_ShouldReturnProduct() {
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        ProductResponseDTO response = productService.findById("1");
        assertEquals("1", response.getId());
    }

    @Test
    void findById_WhenNotExists_ShouldThrowNotFoundException() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.findById("999"));
    }

    @Test
    void update_WhenExists_ShouldUpdateProduct() {
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductResponseDTO response = productService.update("1", requestDTO);
        assertNotNull(response);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_WhenNotExists_ShouldThrowNotFoundException() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.update("999", requestDTO));
    }

    @Test
    void patch_ShouldUpdateOnlyNonNullFields() {
        ProductPatchDTO patchDTO = new ProductPatchDTO();
        patchDTO.setPrice(new BigDecimal("19.99"));
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductResponseDTO response = productService.patch("1", patchDTO);
        assertNotNull(response);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(productRepository.existsById("1")).thenReturn(true);
        doNothing().when(productRepository).deleteById("1");
        assertDoesNotThrow(() -> productService.delete("1"));
        verify(productRepository).deleteById("1");
    }

    @Test
    void delete_WhenNotExists_ShouldThrowNotFoundException() {
        when(productRepository.existsById("999")).thenReturn(false);
        assertThrows(ProductNotFoundException.class, () -> productService.delete("999"));
    }
}