package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
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
    // GET http://localhost:8080/api/products/1
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

    // POST create http://localhost:8080/api/products
    @PostMapping("products")
    public ResponseEntity<Product> create(@RequestBody Product product) { // DTO mapper Entidad
        if (product.getId() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 400 No puede tener ID porque es nueva creación

        // if(product.getManufacturer() != null)
            // guardar el fabricante

        try {
            productRepository.save(product);
             return ResponseEntity.created(new URI("/api/products/" + product.getId())).body(product);
//            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    // PUT update

    // DELETE deleteById
    // DELETE deleteAll

}
