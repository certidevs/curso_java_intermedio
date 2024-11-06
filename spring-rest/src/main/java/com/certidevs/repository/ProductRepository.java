package com.certidevs.repository;

import com.certidevs.dto.ManufacturerWithProductDataDTO;
import com.certidevs.model.Manufacturer;
import com.certidevs.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByPriceIsGreaterThanEqual(Double price);

    List<Product> findByActiveTrueAndPriceLessThanEqualOrderByPriceDesc(Double price);

    List<Product> findByManufacturer_Id(Long id);

    // Jakarta Persistence Query Language JPQL
//    @Query("select p from Product p join fetch p.manufacturer  where p.manufacturer.id = ?1")
    @Query("""
    select p from Product p
    join fetch p.manufacturer
    where p.manufacturer.id = ?1
    """)
    List<Product> findByManufacturer_Id_Query(Long id);

    long deleteByManufacturer(Manufacturer manufacturer);


}