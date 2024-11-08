package com.certidevs.controller;

import com.certidevs.repository.ManufacturerRepository;
import com.certidevs.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequestMapping("api")
@RestController
public class ManufacturerController {

    private final ManufacturerRepository manufacturerRepository;
    private final ProductRepository productRepository;

    public ManufacturerController(ManufacturerRepository manufacturerRepository, ProductRepository productRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.productRepository = productRepository;
    }

    @DeleteMapping("manufacturers/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable Long id
    ) {

        // opción 1: borrar directamente (problemático)
        // org.springframework.dao.DataIntegrityViolationException: could not execute statement [Cannot delete or update a parent row: a foreign key constraint fails (`springdb`.`products`,
////        try {
//            manufacturerRepository.deleteById(id);
//            return ResponseEntity.noContent().build();
////        } catch (Exception e) {
////            throw new ResponseStatusException(HttpStatus.CONFLICT);
////        }


        // Opción 2: obtener los productos de este fabricante
        // y quitarles el fabricante para quitar la FK
//        var products = productRepository.findByManufacturer_Id(id)
//                .stream()
//                .map(product -> {
//                    product.setManufacturer(null);
//                    return product;
//                }).toList();
//        productRepository.saveAll(products);
//        try {
//            manufacturerRepository.deleteById(id);
//            return ResponseEntity.noContent().build();
//        } catch (Exception e) {
//            log.error("Error deleting manufacturer", e);
//            throw new ResponseStatusException(HttpStatus.CONFLICT);
//        }

        // Opción 3: crear una sentencia UPDATE a nivel de repo

        try {
            productRepository.updateSetManufacturerToNullByManufacturerId(id);
            manufacturerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting manufacturer", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}
