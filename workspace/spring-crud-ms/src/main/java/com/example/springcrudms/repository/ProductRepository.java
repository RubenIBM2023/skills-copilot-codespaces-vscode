package com.example.springcrudms.repository;

import com.example.springcrudms.model.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategory(String category);
    Page<Product> findAll(Pageable pageable);
}
