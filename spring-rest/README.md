

## Testing Unitario:

JUNIT 5, MOCKITO

* @ExtendWith
* @Mock
* JUnit 5:
  * assertEquals
  * assertNotNull
  * assertTrue y assertFalse
* Mockito:
  * when
  * thenReturn
  * thenAnswer
  * thenThrow
  * doThrow
  * verify


## Testing integración parcial

* @WebMvcTest
* @MockBean
* MockMvc

No carga base de datos. No carga toda la app de spring solo el controlador a testear.


## Testing integración completa

* @SpringBootTest
* @Transactional