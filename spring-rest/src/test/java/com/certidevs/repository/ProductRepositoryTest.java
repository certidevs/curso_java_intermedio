package com.certidevs.repository;

import com.certidevs.model.Product;
import org.junit.jupiter.api.DisplayName;
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
    @Autowired
    private ManufacturerRepository manufacturerRepository;

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

    @Test
    void findById() {
        // insertar datos con el repositorio
        var product = productRepository.save(
                Product.builder().name("Producto 1").quantity(2).price(24.33).active(true).build()
        );
        assertNotNull(product.getId());
        var productDB = productRepository.findById(product.getId()).orElseThrow();
        assertEquals("Producto 1", productDB.getName());
        assertEquals(product.getId(), productDB.getId());

    }

    @Test
    void findByActiveTrueAndPriceLessThanEqualOrderByPriceDesc() {
        productRepository.saveAll(List.of(
                Product.builder().name("Producto 1").quantity(2).price(24.33).active(true).build(),
                Product.builder().name("Producto 2").quantity(3).price(44.33).active(false).build(),
                Product.builder().name("Producto 3").quantity(4).price(64.33).active(true).build(),
                Product.builder().name("Producto 4").quantity(5).price(84.33).active(false).build()
        ));

        var products = productRepository.findByActiveTrueAndPriceLessThanEqualOrderByPriceDesc(70d);
        assertEquals(2, products.size());
        assertEquals(64.33, products.get(0).getPrice());
        assertEquals(24.33, products.get(1).getPrice());
    }

    void findByPrice() {}
    void findByManufacturerId() {}
    void calculateSumPricesWhereProductsGreaterThan() {}


    @Test
    @DisplayName("Filtrar productos por id de fabricante")
    @Sql("data_products_manufacturers.sql")
    void findByManufacturer_Id() {

        var products = productRepository.findByManufacturer_Id(1L);
        assertEquals(2, products.size());
//        Hibernate: select p1_0.id,p1_0.active,p1_0.id_manufacturer,p1_0.name,p1_0.price,p1_0.quantity from products p1_0 where p1_0.id_manufacturer=?
//        Hibernate: select m1_0.id,m1_0.city,m1_0.name,m1_0.start_year from manufacturers m1_0 where m1_0.id=?
        assertEquals("Adidas", products.get(0).getManufacturer().getName());
    }

    @Test
    @DisplayName("Filtrar productos por id de fabricante haciendo una consulta JPQL con join fetch para cargar los productos y fabricantes todo en una sola consulta")
    @Sql("data_products_manufacturers.sql")
    void findByManufacturer_Id_QueryJPQL() {

        var products = productRepository.findByManufacturer_Id_Query(1L);
        assertEquals(2, products.size());
//        Hibernate: select p1_0.id,p1_0.active,m1_0.id,m1_0.city,m1_0.name,m1_0.start_year,p1_0.name,p1_0.price,p1_0.quantity from products p1_0 join manufacturers m1_0 on m1_0.id=p1_0.id_manufacturer where p1_0.id_manufacturer=?
        assertEquals("Adidas", products.get(0).getManufacturer().getName());
    }

    @Test
    @DisplayName("Borrar productos de un fabricante")
    @Sql("data_products_manufacturers.sql")
    void deleteByManufacturer() {
        var manufacturer = manufacturerRepository.findAll().get(0);
        long deleted = productRepository.deleteByManufacturer(manufacturer);
        assertTrue(deleted >= 3);
        assertEquals(0, productRepository.count());
    }
}