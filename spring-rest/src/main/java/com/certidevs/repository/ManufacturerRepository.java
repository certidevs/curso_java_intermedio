package com.certidevs.repository;

import com.certidevs.dto.ManufacturerWithProductDataDTO;
import com.certidevs.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    @Query("""
    SELECT new com.certidevs.dto.ManufacturerWithProductDataDTO(
    m.id,
    m.name,
    COUNT(m),
    SUM(p.price)
    ) FROM Manufacturer m
    LEFT JOIN Product p ON m.id = p.manufacturer.id
    """)
    List<ManufacturerWithProductDataDTO> findAllWithCalculatedProductsStats();

    @Query("""
    SELECT new com.certidevs.dto.ManufacturerWithProductDataDTO(
        m.id,
        m.name,
        COUNT(p),
        SUM(p.price)
    ) FROM Manufacturer m
    LEFT JOIN m.products p
    """)
    List<ManufacturerWithProductDataDTO> findAllWithCalculatedProductsStatsBidirectional();

    @Query("""
    SELECT m from Manufacturer m
    join fetch m.products
    WHERE m.id = ?1
    """)
    Optional<Manufacturer> findByIdWithEagerRelationships(Long id);
}