package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// http://localhost:8080/swagger-ui/index.html
@AllArgsConstructor
@RequestMapping("api")
@RestController
public class ProductController {
    private final ProductRepository productRepository;

    // GET http://localhost:8080/api/products
    @GetMapping("products")
    public ResponseEntity<List<Product>> findAll() {
        var products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }
    // GET findById
    // POST create
    // PUT update
    // DELETE deleteById
    // DELETE deleteAll

}
