package com.certidevs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "manufacturers")
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String city;
    private Integer startYear;

    // bidireccional con products

    // Al ser bidireccional puede formar un ciclo infinito de JSON que hay que cortar ignorando en algún punto la relación
    @JsonIgnore
    // @JsonIgnoreProperties({"manufacturer"})
    @OneToMany(mappedBy = "manufacturer")
    List<Product> products = new ArrayList<>();
}
