
# API Cassava
```
Este es un proyecto de demostración para una API desarrollada con Spring Boot. La API proporciona funcionalidades de autenticación, gestión de usuarios y scraping web.
```
## Estructura del Proyecto

```
.gitattributes
.gitignore
.mvn/
    wrapper/
        maven-wrapper.properties
.vscode/
    launch.json
    settings.json
mvnw


mvnw.cmd




pom.xml


src/
    main/
        java/
            vyshu/
        resources/
            META-INF/
                

additional-spring-configuration-metadata.json


    test/
        java/
target/
    

api-cassava-1.0.0.jar.original


    classes/
        

application.properties


        META-INF/
        vyshu/
    generated-sources/
        annotations/
    generated-test-sources/
        test-annotations/
    maven-archiver/
        

pom.properties


    maven-status/
        maven-compiler-plugin/
    surefire-reports/
        ...
    test-classes/
        vyshu/
```

## Dependencias

El proyecto utiliza las siguientes dependencias principales:

- Spring Boot Starter Actuator
- Spring Boot Starter Web
- Spring Boot DevTools
- Spring Boot Starter Test
- Jsoup
- Spring Boot Starter Data JDBC
- MySQL Connector
- Spring Boot Starter Security
- JJWT (Java JWT)

## Configuración

### application.properties

El archivo `application.properties` contiene las configuraciones necesarias para la aplicación, incluyendo la configuración de la base de datos y el secreto JWT.

```properties
spring.application.name=api-cassava
jwt.secret=******************************************************************************
spring.datasource.url=jdbc:mysql://localhost:3306/dbData
spring.datasource.username=usuario
spring.datasource.password=**********************************************************************
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.web.resources.add-mappings=false
```

## Estructura del Código

### Controladores

- **AuthController**: Maneja las operaciones de autenticación como login, registro, validación de tokens y logout.
- **UserController**: Maneja las operaciones relacionadas con los usuarios como cambio de contraseña y nombre de usuario.
- **ApiController**: Proporciona endpoints para obtener datos raspados de la web.

### Servicios

- **AuthService**: Contiene la lógica de negocio para la autenticación de usuarios.
- **UserDataService**: Contiene la lógica de negocio para la gestión de datos de usuarios.
- **WebScrapingService**: Contiene la lógica para raspar datos de la web.

### Utilidades

- **JwtUtil**: Utilidad para generar y validar tokens JWT.
- **ValidateDataUsersUtil**: Utilidad para validar datos de usuarios como email, nombre de usuario y contraseña.

### Repositorios

- **UserRepository**: Interactúa con la base de datos para realizar operaciones relacionadas con los usuarios.

### Modelos

- **User**: Representa un usuario en el sistema.
- **ErrorDto**: Representa un error en el sistema.

### Excepciones

- **AuthException**: Excepción personalizada para errores de autenticación.
- **InvalidTokenException**: Excepción personalizada para tokens inválidos.
- **UserDataFormatExeption**: Excepción personalizada para errores de formato de datos de usuario.

## Ejecución

Para ejecutar el proyecto, utiliza el siguiente comando:

```sh
./mvnw spring-boot:run
```

## Pruebas

Para ejecutar las pruebas, utiliza el siguiente comando:

```sh
./mvnw test
```

## Endpoints

### Autenticación

- **POST /auth/v1/login**: Inicia sesión con un identificador (email o nombre de usuario) y contraseña.
- **POST /auth/v1/register**: Registra un nuevo usuario.
- **POST /auth/v1/validate/token**: Valida un token JWT.
- **POST /auth/v1/logout**: Revoca un token JWT.
- **POST /auth/v1/revoke/token**: Revoca un token específico.

### Gestión de Usuarios

- **POST /dashboard/v1/change-password**: Cambia la contraseña de un usuario.
- **POST /dashboard/v1/change-username**: Cambia el nombre de usuario.
- **POST /dashboard/v1/get-tokens**: Obtiene todos los tokens de un usuario.

### API

- **GET /api/flavour**: Obtiene los sabores de Cassava.
- **GET /api/size**: Obtiene los tamaños disponibles.
- **GET /api/temperature**: Obtiene las temperaturas disponibles.
- **GET /api/milk**: Obtiene los tipos de leche disponibles.
- **GET /api/topping**: Obtiene las coberturas disponibles.

## Contribuciones

Las contribuciones son bienvenidas. Por favor, abre un issue o un pull request para discutir cualquier cambio que te gustaría hacer.
