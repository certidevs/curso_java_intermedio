package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
    @GetMapping("products/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {

//        Optional<Product> productOpt = productRepository.findById(id);
//        if (productOpt.isPresent())
//            return ResponseEntity.ok(productOpt.get());
//        else
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return productRepository.findById(id)
                .map(product -> ResponseEntity.ok(product))
//                .orElseThrow();
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    // POST create
    
    // PUT update
    // DELETE deleteById
    // DELETE deleteAll

}
