

## TESTING UNITARIO

JUNIT 5, MOCKITO

* @ExtendWith
* @Mock
* @InjectMocks
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

* Es más rápido
* No depende de Spring ni de base de datos
* Se aplica a cualquier clase Java: Controlador, Servicio, Componente

## TESTING INTEGRACIÓN PARCIAL

JUNIT 5 + MOCKITO + SPRING TEST + SPRING (SOLO UNA PARTE)

* @WebMvcTest(ProductController.class)
* @MockBean
* @Autowired MockMvc

* No carga base de datos. 
* No carga toda la app de spring solo el controlador a testear.
* Es más lento que el unitario porque carga el contenedor de dependencias Spring.
* Permite probar las rutas Mapping de los controladores y sus parámetros de forma real lanzando peticiones http con mockMvc y comprobando las respuestas.

* Sirve para
  * Controladores MVC
  * Controladores API REST

## TESTING INTEGRACIÓN COMPLETA

JUNIT 5 + MOCKITO + SPRING TEST + SPRING (COMPLETO) + JPA + HIBERNATE + BASE DE DATOS

* @SpringBootTest
* @AutoConfigureMockMvc
* @Transactional
* @Autowired MockMvc

* Más lento porque carga la base de datos y carga todo Spring, JPA, Hibernate
* Al interactuar de forma real con todas las capas integradas: controladores, servicios, repositorios


## TESTING FUNCIONAL / INTERFAZ USUARIO

JUNIT 5 + SELENIUM

* Se levanta la aplicación real
* Se levanta navegador
* Se simula una navegación de usuario a través de la UI
* Más lento aún que los anteriores

