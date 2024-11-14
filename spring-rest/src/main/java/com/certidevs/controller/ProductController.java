package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
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
    public static final double IVA_21 = 1.21;
    private final ProductRepository productRepository;

    // GET http://localhost:8080/api/products
    @GetMapping("products")
    public ResponseEntity<List<Product>> findAll() {
        var products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }
    @GetMapping("products-iva")
    public ResponseEntity<List<Product>> findAllWithIVA() {
//        var products = productRepository.findAll().stream().map(p -> {
//            p.setPrice(p.getPrice() * 1.21);
//            return p;
//        }).toList();

        var products = productRepository.findAll();
        // Devolverá precio a 0
//        products.forEach(p -> p.setPrice(Optional.ofNullable(p.getPrice()).orElse(0d) * IVA_21));
        // Devolverá precio a null
        products.forEach(p -> {
            if(p.getPrice() != null) p.setPrice(p.getPrice() * IVA_21);
        });
        return ResponseEntity.ok(products);
    }

//    @GetMapping("products-by-price/{minPrice}/{maxPrice}")
//    public ResponseEntity<List<Product>> findAllByPrice(@PathVariable Double minPrice, @PathVariable Double maxPrice) {

    // /api/products-by-price?minPrice=30&maxPrice=50
    @GetMapping("products-by-price")
    public ResponseEntity<List<Product>> findAllByPrice(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {

        List<Product> products;
        if (minPrice != null && maxPrice != null)
            products = productRepository.findByPriceBetween(minPrice, maxPrice);
        else if(minPrice != null)
            products = productRepository.findAllByPriceIsGreaterThanEqual(minPrice);
        else if (maxPrice != null)
            products = productRepository.findAllByPriceIsLessThanEqual(maxPrice);
        else
            products = productRepository.findAll();

        return ResponseEntity.ok(products);
    }

    @PostMapping("products/filter")
    public ResponseEntity<List<Product>> findByFilter(@RequestBody Product product) {
        var products = productRepository.findAll(Example.of(product));
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


        // Dando por hecho que el Product sí existe porque se validó en el existsById
//        var productDB = productRepository.findById(id).get();
//        if (product.getPrice() != null) productDB.setPrice(product.getPrice());
//        if (product.getQuantity() != null) productDB.setQuantity(product.getQuantity());
//        if (product.getName() != null) productDB.setName(product.getName());
//        return ResponseEntity.ok(productDB);

        // No obstante alguien podría justo borrarlo después de la validación
        // por lo tanto es buena idea usar el optional y lanzar excepción si no existe
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

    // @RequestParam DELETE /api/products?ids=1,2,3,4,5,6,7,8,9,10
    // @RequestBody DELETE /api/products { "ids": [1, 2, 3]}
    @DeleteMapping("products")
    public ResponseEntity<Void> deleteAll(@RequestBody List<Long> ids) {
        try {
            // Lanza dos consultas por cada producto a borrar
            // Hibernate: select p1_0.id,p1_0.active,m1_0.id,m1_0.city,m1_0.name,m1_0.start_year,p1_0.name,p1_0.price,p1_0.quantity from products p1_0 left join manufacturers m1_0 on m1_0.id=p1_0.id_manufacturer where p1_0.id=?
            // Hibernate: delete from products where id=?
            //productRepository.deleteAllById(ids);

            // Borra en una sola sentencia: Hibernate: delete p1_0 from products p1_0 where p1_0.id in (?,?,?,?,?,?,?)
            productRepository.deleteAllByIdInBatch(ids);

            // Una sola sentencia  Hibernate: delete p1_0 from products p1_0 where p1_0.id in (?,?,?) and p1_0.active=1
            // productRepository.deleteAllBy_ActiveTrue_And_IdIn(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Cant delete product ", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

}
