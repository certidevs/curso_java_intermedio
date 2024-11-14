package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
Aquí usamos JUnit 5 + Mockito + Spring Test

Test de integración parcial porque no carga toda la aplicación de Spring

Solo carga el controlador ProductController y nada más

Como no carga base de datos ni ninguna dependencia usaremos mocks

Tarda más en ejecutarse que un test unitario
Pero tarda menos en ejecutar que un test de integración completa con base de datos.
 */
@WebMvcTest(ProductController.class)
//@SpringBootTest
class ProductControllerPartialIntegrationTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll_WithProducts() throws Exception {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(22.3).build(),
                Product.builder().id(2L).name("prod2").price(40.3).build()
        );
        when(productRepository.findAll()).thenReturn(products);

        // HttpClient
//        RestTemplate
//        TestRestTemplate
//        WebClient
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("prod1"))
                .andExpect(jsonPath("$[1].name").value("prod2"))
                .andExpect(jsonPath("$[1].active").doesNotExist());


        verify(productRepository).findAll();
    }
    @Test
    void findAll_NoProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Buscar productos con IVA calculado con precios OK")
    void findAllWithIVA_OK() throws Exception {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(100d).build()
        );
        when(productRepository.findAll()).thenReturn(products);
        mockMvc.perform(get("/api/products-iva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(12.1))
                .andExpect(jsonPath("$[1].price").value(121.0));
        verify(productRepository).findAll();
    }
    @Test
    @DisplayName("Buscar productos con IVA calculado con precios null")
    void findAllWithIVA_priceNull() throws Exception {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").build(),
                Product.builder().id(2L).name("prod2").build()
        );
        when(productRepository.findAll()).thenReturn(products);
        mockMvc.perform(get("/api/products-iva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").isEmpty())
                .andExpect(jsonPath("$[1].price").doesNotExist());
        verify(productRepository).findAll();
    }



}