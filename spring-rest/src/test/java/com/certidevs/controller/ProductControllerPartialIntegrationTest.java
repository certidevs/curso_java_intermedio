package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
    void findAll() {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(22.3).build(),
                Product.builder().id(2L).name("prod2").price(40.3).build()
        );
        when(productRepository.findAll()).thenReturn(products);

//        mockMvc.perform(get("/products"))
        // .andExpect status ok
        // .andExpect jsonPath

        verify(productRepository).findAll();
    }
}