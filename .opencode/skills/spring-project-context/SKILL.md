---
name: spring-project-context
description: Mapeo de la arquitectura en capas, paquetes base, configuración DTO y manejo global de excepciones.
---

## Estructura de Paquetes
- `config/`: Security, CORS, Argon2, HikariCP y beans globales.
- `controller/`: Endpoints REST controllers y manejo de contratos HTTP.
- `dto/`: Records o POJOs de petición (`Request`) y respuesta (`Response`).
- `entity/`: Entidades de dominio con `jakarta.persistence.*`.
- `repository/`: Interfaces `JpaRepository` con consultas personalizadas.
- `service/`: Interfaces de lógica de negocio y sus implementaciones (`service/impl`).
- `security/`: Filtro JWT, servicio de UserDetails y utilidades de token.
- `exception/`: Manejador global de excepciones (`@RestControllerAdvice`).

## Reglas de Arquitectura y Respuestas
1. **Separación DTO/Entidad:** Los controladores nunca deben exponer ni recibir entidades directamente. Usar DTOs.
2. **Inyección de Dependencias:** Usar inyección por constructor (preferir `final` en atributos de servicio/controlador).
3. **Manejo de Errores:** Centralizar respuestas de error en un `@RestControllerAdvice` uniforme.