package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

// http://localhost:8080/swagger-ui/index.html
@Slf4j
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
    @PutMapping("products/{id}")
    public ResponseEntity<Product> update (@PathVariable Long id, @RequestBody Product product) {
        if (id == null || !productRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 400 No puede tener ID porque es nueva creación

        // Opción 1: guardar el producto tal cual llega:
         productRepository.save(product);
         return ResponseEntity.ok(product);

        // Opción 2: sacar el producto de base de datos y solo guardar las propiedades que queramos:
//        return productRepository.findById(id).map(productDB -> {
//            productDB.setPrice(product.getPrice());
//            productDB.setQuantity(product.getQuantity());
//            // BeanUtils.copyProperties(product, productDB);
//            productRepository.save(productDB);
//            return ResponseEntity.ok(productDB);
//        }).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
//        );

    }

    @PatchMapping(value = "products/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<Product> partialUpdate(
            @PathVariable Long id, @RequestBody Product product
    ) {
        if (id == null || !productRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Optional<Product> productOpt = productRepository.findById(id).map(existingProduct -> {
            if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
            if (product.getQuantity() != null) existingProduct.setQuantity(product.getQuantity());
            if (product.getName() != null) existingProduct.setName(product.getName());
            return existingProduct;
        }).map(productRepository::save);

        return ResponseEntity.ok(
                productOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        );


//        if(productOpt.isPresent())
//            return ResponseEntity.ok(productOpt.get());
//        else
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

    }

    @DeleteMapping("products/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        // Opción 1: lo borramos:
//        try {
//            productRepository.deleteById(id);
//            return ResponseEntity.noContent().build(); // 204 No content
//        } catch (Exception e) {
//            log.error("Cant delete product ", e);
//            throw new ResponseStatusException(HttpStatus.CONFLICT);
//        }

        // Opción 2: lo desactivamos
        productRepository.findById(id).map(product -> {
            product.setActive(false);
            return productRepository.save(product);
        });
        return ResponseEntity.noContent().build();

    }

    // DELETE deleteById
    // DELETE deleteAll

}
