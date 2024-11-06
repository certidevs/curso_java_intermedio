package com.certidevs.repository;

import com.certidevs.dto.ManufacturerWithProductDataDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ManufacturerRepositoryTest {

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Test
    @Sql("data_products_manufacturers.sql")
    void findAllWithCalculatedProductsStats() {
        List<ManufacturerWithProductDataDTO> manufacturers =
                manufacturerRepository.findAllWithCalculatedProductsStats();

        assertEquals(1, manufacturers.size());
        assertEquals(3, manufacturers.get(0).productsCount());
        assertEquals(150d, manufacturers.get(0).productsSumTotalPrice());
    }

    @Test
    @Sql("data_products_manufacturers.sql")
    void findAllWithCalculatedProductsStatsBidirectional() {
        List<ManufacturerWithProductDataDTO> manufacturers =
                manufacturerRepository.findAllWithCalculatedProductsStatsBidirectional();

        assertEquals(1, manufacturers.size());
        assertEquals(3, manufacturers.get(0).productsCount());
        assertEquals(150d, manufacturers.get(0).productsSumTotalPrice());
    }

    @Test
    @Sql("data_products_manufacturers.sql")
    @DisplayName("findById normal que no trae las asociaciones por defecto salvo que se pidan y se lanza una segunda query")
    void findById() {
        var manufacturer = manufacturerRepository.findById(1L).get();
        System.out.println("Aquí todavía no hemos consultado los productos");
        assertEquals(3, manufacturer.getProducts().size());

        /* Se lanzan dos consultas porque la asociación products es LAZY
        Hibernate: select m1_0.id,m1_0.city,m1_0.name,m1_0.start_year from manufacturers m1_0 where m1_0.id=?
        Aquí todavía no hemos consultado los productos
        Hibernate: select p1_0.id_manufacturer,p1_0.id,p1_0.active,p1_0.name,p1_0.price,p1_0.quantity from products p1_0 where p1_0.id_manufacturer=?
         */
    }

    @Test
    @Sql("data_products_manufacturers.sql")
    @DisplayName("findById normal EAGER que sí trae las asociaciones por defecto todo en una misma query")
    void findById_Eager() {
        var manufacturer = manufacturerRepository.findByIdWithEagerRelationships(1L).get();
        System.out.println("Aquí todavía no hemos consultado los productos");
        assertEquals(3, manufacturer.getProducts().size());

        /* Se lanza una única consulta que trae de forma EAGER los productos del Manufacturer porque se ha hecho en el JPQL
        Hibernate: select m1_0.id,m1_0.city,m1_0.name,p1_0.id_manufacturer,p1_0.id,p1_0.active,p1_0.name,p1_0.price,p1_0.quantity,m1_0.start_year from manufacturers m1_0 join products p1_0 on m1_0.id=p1_0.id_manufacturer where m1_0.id=?
        Aquí todavía no hemos consultado los productos
         */
    }
}