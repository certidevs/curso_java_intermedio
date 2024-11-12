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
import org.springframework.dao.DataIntegrityViolationException;
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

    @Test
    void create_BadRequest() {
        Product product = Product.builder().id(1L).price(50d).build();

        var exception = assertThrows(
                ResponseStatusException.class,
                () -> productController.create(product)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void create_OK() {
        Product product = Product.builder()
                .name("prod1")
                .price(50d)
                .build();

        // Captura el argumento product y simula que se le genera
        // un id como lo haría la base de datos real
        when(productRepository.save(product)).thenAnswer(invocation -> {
            Product productArgument = invocation.getArgument(0);
            productArgument.setId(1L);
            return productArgument;
        });

        var response = productController.create(product);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertEquals("/api/products/1", response.getHeaders().getLocation().toString());

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("prod1", response.getBody().getName());
        assertEquals(50d, response.getBody().getPrice());

    }

    @Test
    void created_conflict() {
        Product product = Product.builder()
                .name("prod1")
                .price(50d)
                .build();

//        doThrow(new RuntimeException())
//                .when(productRepository)
//                .save(product);
//        doThrow(new DataIntegrityViolationException("Foreign Key error"))
//                .when(productRepository)
//                .save(product);
        when(productRepository.save(product)).thenThrow(new DataIntegrityViolationException("Conflicto"));

        var exception = assertThrows(
                ResponseStatusException.class,
                () -> productController.create(product)
        );
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    @DisplayName("Actualizar producto - Id null lanza excepción")
    void update_IdNull() {
        assertThrows(
                ResponseStatusException.class,
                () -> productController.update(null, null)
        );
    }

    @Test
    @DisplayName("Actualizar producto - producto no existe")
    void update_NotExistById(){
        when(productRepository.existsById(1L)).thenReturn(false);
        assertThrows(
                ResponseStatusException.class,
                () -> productController.update(1L, null)
        );
    }

    @Test
    @DisplayName("Actualizar producto - OK")
    void update_OK() {
        Product product = Product.builder()
                .id(1L)
                .name("prod1")
                .price(50d)
                .build();

        when(productRepository.existsById(1L)).thenReturn(true);
        var response = productController.update(1L, product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(product.getId(), response.getBody().getId());
        assertEquals(product.getName(), response.getBody().getName());

    }

    @Test
    void partialUpdate_findById_OK() {
        when(productRepository.existsById(1L)).thenReturn(true);

        Product productFromDB = Product.builder()
                .id(1L).price(50d).quantity(1).name("prod1").active(true)
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(productFromDB));
        when(productRepository.save(productFromDB)).thenReturn(productFromDB);

        Product editedProduct = Product.builder()
                .id(1L).price(60d).quantity(2).name("prod1 edit").active(false)
                .build();

        var response = productController.partialUpdate(1L, editedProduct);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // asserts para comprobar que el producto de base de datos (el que devuelve el repositorio)
        // ha ido modificado por el controlador:
        assertEquals(editedProduct.getPrice(), productFromDB.getPrice());
        assertEquals(editedProduct.getPrice(), response.getBody().getPrice());
        assertEquals(editedProduct.getQuantity(), productFromDB.getQuantity());
        assertEquals(editedProduct.getName(), productFromDB.getName());
        assertNotEquals(editedProduct.getActive(), productFromDB.getActive());
        assertTrue(productFromDB.getActive());

    }
    @Test
    void partialUpdate_findById_NotFound() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        var exception = assertThrows(
                ResponseStatusException.class,
                () -> productController.partialUpdate(1L, null)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

}