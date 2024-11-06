package com.certidevs.repository;

import com.certidevs.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findAll() {
        productRepository.save(
                Product.builder().name("Producto 1").quantity(2).price(24.33).active(true).build()
        );

        assertEquals(1, productRepository.count());
        List<Product> products = productRepository.findAll();
//        assertEquals("Producto 1", products.getFirst().getName());
        assertEquals("Producto 1", products.get(0).getName());
    }

    void findById() {}
    void findByPrice() {}
    void findByManufacturerId() {}
    void calculateSumPricesWhereProductsGreaterThan() {}
}