package com.example.springcrudms.controller;

import com.example.springcrudms.config.MongoConfig;
import com.example.springcrudms.dto.ProductRequestDTO;
import com.example.springcrudms.dto.ProductResponseDTO;
import com.example.springcrudms.exception.GlobalExceptionHandler;
import com.example.springcrudms.exception.ProductNotFoundException;
import com.example.springcrudms.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = ProductController.class,
    excludeAutoConfiguration = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class},
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = MongoConfig.class
        ),
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.example.springcrudms.config.MongoConfig.class
        )
    }
)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductResponseDTO responseDTO;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new ProductResponseDTO();
        responseDTO.setId("1");
        responseDTO.setName("Test Product");
        responseDTO.setDescription("Description");
        responseDTO.setPrice(new BigDecimal("9.99"));
        responseDTO.setStock(10);
        responseDTO.setCategory("Electronics");
        responseDTO.setCreatedAt(Instant.now());
        responseDTO.setUpdatedAt(Instant.now());

        requestDTO = new ProductRequestDTO();
        requestDTO.setName("Test Product");
        requestDTO.setDescription("Description");
        requestDTO.setPrice(new BigDecimal("9.99"));
        requestDTO.setStock(10);
        requestDTO.setCategory("Electronics");
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        when(productService.create(any())).thenReturn(responseDTO);
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void create_WithInvalidData_ShouldReturn400() throws Exception {
        ProductRequestDTO invalid = new ProductRequestDTO();
        // name and category are blank — should fail validation
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_ShouldReturn200() throws Exception {
        when(productService.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(responseDTO)));
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value("1"));
    }

    @Test
    void findById_WhenExists_ShouldReturn200() throws Exception {
        when(productService.findById("1")).thenReturn(responseDTO);
        mockMvc.perform(get("/api/v1/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void findById_WhenNotExists_ShouldReturn404() throws Exception {
        when(productService.findById("999")).thenThrow(new ProductNotFoundException("999"));
        mockMvc.perform(get("/api/v1/products/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        when(productService.update(eq("1"), any())).thenReturn(responseDTO);
        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(productService).delete("1");
        mockMvc.perform(delete("/api/v1/products/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void delete_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new ProductNotFoundException("999")).when(productService).delete("999");
        mockMvc.perform(delete("/api/v1/products/999"))
            .andExpect(status().isNotFound());
    }
}
