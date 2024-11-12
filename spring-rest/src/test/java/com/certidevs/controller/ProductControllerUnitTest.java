package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
Test unitario:
* Rápidos
* Pequeños, prueban una unidad, un metodo
* Medibles
* Idempotentes
* Fáciles de leer

No carga la base de datos ni cargamos Spring, queremos velocidad.
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerUnitTest {

    // dependencias
    @Mock
    private ProductRepository productRepository;

    // SUT - System Under Test
    @InjectMocks
    private ProductController productController;

    @Test
    @DisplayName("Buscar todos los productos")
    void findAll() {
        // given when then

        // 1. configurar mocks
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(50d).build(),
                Product.builder().id(3L).name("prod3").price(100d).build()
        );
        when(productRepository.findAll()).thenReturn(products);

        // 2. ejecutar metodo a testear
        var responseEntity = productController.findAll();

        // 3. asserts y verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Comprobar status HTTP es 200 OK");
        var productResponse = responseEntity.getBody();
        assertNotNull(productResponse);
        assertEquals(3, productResponse.size());
        assertEquals(1L, productResponse.getFirst().getId());
        assertEquals(2L, productResponse.get(1).getId());

        // verify(productRepository, times(1)).findAll();
        verify(productRepository).findAll();

    }

    @Test
    @DisplayName("Buscar todos los productos con IVA calculado")
    void findAllWithIVA() {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(50d).build(),
                Product.builder().id(3L).name("prod3").price(100d).build()
        );
        when(productRepository.findAll()).thenReturn(products);

        var responseEntity = productController.findAllWithIVA();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Comprobar status HTTP es 200 OK");

        var productResponse = responseEntity.getBody();
        assertNotNull(productResponse);
        assertEquals(3, productResponse.size());
        assertEquals(12.1, productResponse.getFirst().getPrice());
        assertEquals(121, productResponse.getLast().getPrice());

        // verify(productRepository, times(1)).findAll();
        verify(productRepository).findAll();
    }


    @Test
    @DisplayName("Buscar productos por precio min y max")
    void findAllByPrice_minPrice_maxPrice() {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(50d).build(),
                Product.builder().id(3L).name("prod3").price(90d).build()
        );
        when(productRepository.findByPriceBetween(5d, 100d)).thenReturn(products);
        // when(productRepository.findByPriceBetween(anyDouble(), anyDouble())).thenReturn(products);

        var response = productController.findAllByPrice(5d, 100d);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Comprobar status HTTP es 200 OK");

        var productResponse = response.getBody();
        assertNotNull(productResponse);
        assertEquals(3, productResponse.size());
        assertEquals(10, productResponse.getFirst().getPrice());
        assertEquals(90, productResponse.getLast().getPrice());

        // verify(productRepository, times(1)).findAll();
        verify(productRepository).findByPriceBetween(5d, 100d);
        verify(productRepository).findByPriceBetween(anyDouble(), anyDouble());

    }
    @Test
    @DisplayName("Buscar productos por precio mínimo")
    void findAllByPrice_ByMinPrice() {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(50d).build(),
                Product.builder().id(3L).name("prod3").price(90d).build()
        );
        when(productRepository.findAllByPriceIsGreaterThanEqual(5d)).thenReturn(products);

        var response = productController.findAllByPrice(5d, null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Comprobar status HTTP es 200 OK");

        var productResponse = response.getBody();
        assertNotNull(productResponse);
        assertEquals(3, productResponse.size());
        assertEquals(10, productResponse.getFirst().getPrice());
        assertEquals(90, productResponse.getLast().getPrice());

        // verify(productRepository, times(1)).findAll();
        verify(productRepository).findAllByPriceIsGreaterThanEqual(5d);
        verify(productRepository).findAllByPriceIsGreaterThanEqual(anyDouble());

    }

    @Test
    void findByFilter() {
        Product productFilter = Product.builder().price(50d).build();
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(50d).build(),
                Product.builder().id(3L).name("prod3").price(100d).build()
        );
        when(
//                productRepository.findAll(ArgumentMatchers.<Example<Product>>any())
                productRepository.findAll((Example<Product>) ArgumentMatchers.any())
  //               productRepository.findAll((Any) new TypeReference<List<Product>>())
        ).thenReturn(products);

        // 2. ejecutar metodo a testear
        var responseEntity = productController.findByFilter(productFilter);

        // 3. asserts y verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Comprobar status HTTP es 200 OK");
        var productResponse = responseEntity.getBody();
        assertNotNull(productResponse);
        assertEquals(3, productResponse.size());
        assertEquals(1L, productResponse.getFirst().getId());
        assertEquals(2L, productResponse.get(1).getId());

        // verify(productRepository, times(1)).findAll();
        verify(productRepository).findAll(ArgumentMatchers.<Example<Product>>any());
    }

    @Test
    @DisplayName("Buscar producto por id, producto sí existe")
    void findById_OK() {
        Product product = Product.builder().id(1L).price(50d).build();
//        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var response = productController.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Comprobar status HTTP es 200 OK");
        // assertEquals(product, response.getBody());

        // assertAll permite que se ejecuten todas aunque alguna falle, así vemos todos los fallos
        assertAll(
                () -> assertEquals(product.getId(), response.getBody().getId())
                // () -> assertNotEquals(product.getPrice(), response.getBody().getPrice())
        );


        verify(productRepository).findById(1L);


    }
    @Test
    @DisplayName("Buscar producto por id, producto NO existe")
    void findById_NotFound_Empty() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//        try {
//            productController.findById(1L);
//            fail("Debería haberse lanzado una excepción");
//        } catch (Exception e) {
//           // verificar excepción
//        }
        assertThrows(
                ResponseStatusException.class,
                () -> productController.findById(1L)
        );
    }


}