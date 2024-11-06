
## Plugins

* Lombok
* JPA Buddy

## Profesor

alan@certidevs.com

## Configuración starters (dependencias)

Maven: gestión del ciclo de vida de proyectos java, permite agregar dependencias.

pom.xml agregar las dependencias ya las carga en el proyecto

Spring Boot proporciona starters que son agrupaciones de dependencias.

Hemos empezado con estas dependencias:

* Web: proporciona controladores MVC y REST
* Thymeleaf: plantillas HTML con sintaxis th:each, th:if....
* Lombok: @Getter, @Setter, @Builder

Completo:

* Web
* Thymeleaf
* Lombok
* Spring Data JPA
* Driver de base datos, por ejemplo MySQL
* Spring Security

```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
      <groupId>com.mysql</groupId>
      <artifactId>mysql-connector-j</artifactId>
      <scope>runtime</scope>
    </dependency>
```

Para configurar mysql hay que añadir la url usuario y contraseña en el archivo application.properties

Para MySQL es necesario:

* MySQL Community Server corriendo como servicio en Windows (se comprueba en admin. tareas en servicios)
* MySQL Workbench (es el cliente de interfaz de usuario)

* https://certidevs.com/tutorial-sql-mysql-instalacion
* Alternativa: simplemente usar la base de datos H2 en memoria
* Si hay problema al arrancar puede ser mejor quitar el parámetro ?createDatabaseIfNotExist=true y crear la base de datos manualmente