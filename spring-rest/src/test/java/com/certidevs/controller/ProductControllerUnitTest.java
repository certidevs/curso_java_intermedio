package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

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
}