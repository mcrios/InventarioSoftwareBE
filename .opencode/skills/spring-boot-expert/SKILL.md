---
name: spring-boot-expert
description: Convenciones de desarrollo backend para Spring Boot 3.x, Jakarta EE, Security JWT, mejores prácticas de rendimiento y arquitectura de producción.
---

## Stack Tecnológico y Estándares
- **Framework:** Spring Boot 3.x (Java 17/21+)
- **Persistencia:** Jakarta EE (`jakarta.persistence.*`), Spring Data JPA, HikariCP
- **Seguridad:** Spring Security con `Argon2PasswordEncoder` y filtros JWT stateless
- **Estructuras de Colecciones:** Uso exclusivo de `java.util.List` para relaciones entre entidades (evitar `Set`).
- **Imports Limpios:** Importar **únicamente** las librerías y clases que se utilicen explícitamente en el archivo.

---

## Directivas de Seguridad y Endpoints
1. **Rutas sin versión:** Diseñar endpoints directamente bajo el contexto REST (sin prefijo `/v1` ni prefijos innecesarios).
2. **CORS:** El bloqueo y control de orígenes no autorizados se delega exclusivamente a la configuración global de CORS.
3. **Filtros JWT:** Validar tokens Bearer mediante filtros stateless en la cadena de seguridad (`SecurityFilterChain`).
4. **Cifrado de Credenciales:** Emplear `Argon2PasswordEncoder` para el hashing de contraseñas.

---

## Mejores Prácticas de Persistencia y Modelado (JPA)

### 1. Definición de Entidades
- Utilizar exclusivamente el espacio de nombres `jakarta.persistence.*`.
- Declarar relaciones `@OneToMany` y `@ManyToMany` utilizando `List<T>`.
- Usar tipos explícitos y precisos para llaves primarias (`Long`) y auditoría de timestamps (`LocalDateTime`).

### 2. Evitar el Problema N+1 y Optimizar Consultas
- Emplear `JOIN FETCH` o `@EntityGraph` en consultas de repositorios cuando se requiera cargar colecciones asociadas en una sola llamada a base de datos.
- Para operaciones de lectura pesadas o vistas resumidas, priorizar el uso de **Interfaces de Proyección (Spring Data JPA)** o **Records DTO** directamente en las consultas JPQL/Nativas (`SELECT new com.example.dto...`).

---

## Mejores Prácticas de Capa de Servicio y Controladores

### 1. Inyección de Dependencias
- Usar inyección de dependencias basada estrictamente en constructores.
- Declarar todos los atributos de servicios y controladores como `private final` para garantizar inmutabilidad.


### 2. Manejo de Excepciones
- No retornar mensajes de excepción ni stack traces crudos al cliente.
- Delegar todas las excepciones a un manejador global con `@RestControllerAdvice` que retorne respuestas estructuradas y consistentes 

---

## Estilo de Código y Principios SOLID
- **Interfaces de Servicio:** Definir interfaces para los servicios cuando existan múltiples implementaciones o contratos de integración estrictos; de lo contrario, mantener la lógica modular.
- **Loggin Profesional:** Usar `SLF4J` (`LoggerFactory.getLogger(...)`) con niveles adecuados (`INFO`, `WARN`, `ERROR`, `DEBUG`) evitando el uso de `System.out.println`.
- **Inmutabilidad:** Preferir tipos inmutables y estructuras sencillas en la lógica de negocio.