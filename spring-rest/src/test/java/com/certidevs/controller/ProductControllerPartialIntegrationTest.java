package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Optional;

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
class ProductControllerPartialIntegrationTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    @DisplayName("Buscar productos por precio between")
    void findAllByPrice() throws Exception {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(20d).build(),
                Product.builder().id(3L).name("prod3").price(30d).build(),
                Product.builder().id(4L).name("prod4").price(40d).build()
        );
        List<Product> filteredProducts = List.of(
                Product.builder().id(2L).name("prod2").price(20d).build(),
                Product.builder().id(3L).name("prod3").price(30d).build()
        );
        when(productRepository.findByPriceBetween(15d, 35d)).thenReturn(filteredProducts);
        mockMvc.perform(
                get("/api/products-by-price")
                        .param("minPrice", "15")
                        .param("maxPrice", "35")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price").value(20.0))
                .andExpect(jsonPath("$[1].price").value(30.0));

        verify(productRepository).findByPriceBetween(15d, 35d);
        verify(productRepository, never()).findAll();
        verify(productRepository, never()).findAllByPriceIsLessThanEqual(any());
        verify(productRepository, never()).findAllByPriceIsGreaterThanEqual(any());

    }
    @Test
    @DisplayName("Buscar productos por precio - solo precio mínimo")
    void findAllByPrice_minPrice() throws Exception {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(20d).build(),
                Product.builder().id(3L).name("prod3").price(30d).build(),
                Product.builder().id(4L).name("prod4").price(40d).build()
        );
        List<Product> filteredProducts = List.of(
                Product.builder().id(2L).name("prod2").price(20d).build(),
                Product.builder().id(3L).name("prod3").price(30d).build(),
                Product.builder().id(4L).name("prod4").price(40d).build()
        );
        when(productRepository.findAllByPriceIsGreaterThanEqual(15d)).thenReturn(filteredProducts);
        mockMvc.perform(
                        get("/api/products-by-price")
                                .param("minPrice", "15")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].price").value(20.0))
                .andExpect(jsonPath("$[1].price").value(30.0))
                .andExpect(jsonPath("$[2].price").value(40.0));

        verify(productRepository).findAllByPriceIsGreaterThanEqual(15d);
        verify(productRepository, never()).findAll();
        verify(productRepository, never()).findAllByPriceIsLessThanEqual(any());
        verify(productRepository, never()).findByPriceBetween(any(), any());

    }
    // En caso de que required = false no esté y entonces sean los parámetros obligatorios:
//    @Test
//    @DisplayName("Buscar productos por precio - solo precio mínimo")
//    void findAllByPrice_minPrice_badRequest() throws Exception {
//        mockMvc.perform(
//                        get("/api/products-by-price")
//                                .param("minPrice", "15")
//                )
//                .andExpect(status().isBadRequest());
//    }

    @Test
    @DisplayName("Buscar productos por filtro QBE product")
    void findByFilter() throws Exception {
        Product productFilter = Product.builder().price(50d).build();
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(50d).build(),
                Product.builder().id(2L).name("prod2").price(50d).build(),
                Product.builder().id(3L).name("prod3").price(50d).build()
        );
        when(
                productRepository.findAll(ArgumentMatchers.<Example<Product>>any())
        ).thenReturn(products);

        mockMvc.perform(
                        post("/api/products/filter")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productFilter))
//                                .content("""
//                                        {
//                                        "price": 50
//                                        }
//                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].price").value(50.0))
                .andExpect(jsonPath("$[1].price").value(50.0))
                .andExpect(jsonPath("$[2].price").value(50.0));
    }


    @Test
    void findById_OK() throws Exception {
        Product product = Product.builder().id(1L).price(50d).build();
//        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(
//                get("/api/products/" + product.getId())
                get("/api/products/{id}", product.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(50))
                .andExpect(jsonPath("$.id").value(1));

        verify(productRepository).findById(1L);
    }

    @Test
    void findById_NotFound() throws Exception {
//        Product product = Product.builder().id(1L).price(50d).build();
//        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(
                        get("/api/products/999")
                )
                .andExpect(status().isNotFound());

        verify(productRepository).findById(999L);
    }


    @Test
    void create_OK() throws Exception {
        Product product = Product.builder()
                .name("prod1")
                .price(50d)
                .build();

        // CUIDADO, ponemos any Product en vez de nuestro producto porque
        // como se produce una serialización-deserialización entonces las referencias
        // de producto coinciden
        when(productRepository.save(ArgumentMatchers.any(Product.class))).thenAnswer(invocation -> {
            Product productArgument = invocation.getArgument(0);
            productArgument.setId(1L);
            assertEquals("prod1", productArgument.getName());
            return productArgument;
        });

        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(
                    post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productJson)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(header().string("location", "/api/products/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.price").value(50.0));
    }

    @Test
    void create_badRequest() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("prod1")
                .price(50d)
                .build();
        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_OK() throws Exception{
        Product product = Product.builder()
                .id(1L)
                .name("prod1")
                .price(50d)
                .build();

        when(productRepository.existsById(product.getId())).thenReturn(true);

        String productJson = objectMapper.writeValueAsString(product);

        mockMvc.perform(
                        put("/api/products/{id}", product.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson)
                )
                .andExpect(status().isOk());

    }

    @Test
    void deleteById_OK() throws Exception {
        Product productFromDB = Product.builder()
                .id(1L).price(50d).quantity(1).name("prod1")
                .active(true) // OJO, PONEMOS ACTIVE TRUE
                .build();
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(productFromDB));

        mockMvc.perform(
                        delete("/api/products/{id}", productFromDB.getId())
                )
                .andExpect(status().isNoContent());

        // capturar argumento pasado a save en repository
        var productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product productSaved = productCaptor.getValue();
        assertFalse(productSaved.getActive()); // COMPROBAR ACTIVE FALSE
        /*
        En esta situación como tenemos esto:
         when(productRepository.findById(1L))
                .thenReturn(Optional.of(productFromDB));

                Significa que el controlador usa el mismo producto que tenemos en el test
                y por tanto al desactivarlo ya lo podemos ver en el test, sin necesidad
                de ArgumentCaptor.

                En cambio si el producto lo recibe como parámetro el controlador,
                como se crea un nuevo PRoduct a partir de un json (como ocurre create y update)
                ahí la referencia al producto no es la misma que en el test y entonces
                SÍ NECESITAMOS EL ARGUMENT CAPTOR
         */
    }

}