package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll() throws Exception {
        productRepository.saveAll(List.of(
                Product.builder().name("prod1").price(30d).build(),
                Product.builder().name("prod2").price(40d).build(),
                Product.builder().name("prod3").price(50d).build()
        ));
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("prod1"))
                .andExpect(jsonPath("$[1].name").value("prod2"))
                .andExpect(jsonPath("$[1].price").value(40d))
                .andExpect(jsonPath("$[1].active").doesNotExist());

    }

    @Test
    @DisplayName("Buscar productos por filtro QBE product")
    void findByFilter() throws Exception {
        productRepository.saveAll(List.of(
                Product.builder().name("prod1").price(30d).build(),
                Product.builder().name("prod2").price(40d).build(),
                Product.builder().name("prod3").price(50d).build()
        ));

        Product productFilter = Product.builder().price(50d).build();
        mockMvc.perform(post("/api/products/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productFilter))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price").value(50.0));
    }

    @Test
    void create_OK() throws Exception{
        Product product = Product.builder()
                .name("prod1")
                .price(50d)
                .build();

        String productJson = objectMapper.writeValueAsString(product);

        MvcResult result = mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.price").value(50.0))
                .andReturn();

        // EXTRAER LA CABECERA LOCATION Y EXTRAER EL ID DEL NUEVO PRODUCTO PARA NO TENER HARDCODED
        String location = result.getResponse().getHeader("location");
        assertNotNull(location);

        // /api/products/600
        Long productId = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
        System.out.println(productId);

        mockMvc.perform(get(location))
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.price").value(50.0));

        assertTrue(productRepository.existsById(productId));

    }

    @Test
    void update_OK() throws Exception {
        Product product = Product.builder()
                .name("prod1")
                .price(50d)
                .build();
        productRepository.save(product);

        product.setName("prod1 modificado");
        product.setPrice(100d);

        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(
                        put("/api/products/{id}", product.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.price").value(100.0))
                .andExpect(jsonPath("$.name").value("prod1 modificado"));

        productRepository.findById(product.getId()).ifPresent(productUpdated -> {
            assertEquals(100d, productUpdated.getPrice());
            assertEquals("prod1 modificado", productUpdated.getName());
        });

    }

    @Test
    void deleteById_OK() throws Exception {
        var product = productRepository.save(
                Product.builder().name("prod1").active(true).build()
        );

        mockMvc.perform(
                        delete("/api/products/{id}", product.getId())
                )
                .andExpect(status().isNoContent());

        productRepository.findById(product.getId()).ifPresent(productUpdated -> {
            assertFalse(productUpdated.getActive());
        });
        var savedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertFalse(savedProduct.getActive());
    }

}
