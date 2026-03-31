# Core - Tienda Online API

Servicio backend construido con Spring Boot para gestion de clientes, productos y autenticacion JWT.

## 1. Alcance del proyecto

El proyecto expone una API REST con arquitectura por capas:

- Gestion de clientes (alta, consulta y baja).
- Gestion de productos (CRUD con validaciones de negocio).
- Inicio de sesion con emision de token JWT.
- Manejo global de errores con contrato uniforme (`ApiError`).

## 2. Stack tecnico

- Java 21
- Spring Boot 3.2.5
- Spring Web, Spring Data JPA, Spring Validation
- Spring Security + JWT (JJWT)
- PostgreSQL
- Maven

## 3. Arquitectura

El codigo se organiza por dominio y capas:

- `controller`: contrato HTTP
- `service`: reglas de negocio
- `repository`: acceso a datos
- `entity`: modelo persistente
- `dto`: contratos de entrada y salida
- `mapper`: conversion entre entidad y DTO
- `exception`: errores de dominio y manejador global

## 4. Ejecucion local

Requisitos:

- JDK 21
- Maven Wrapper (`mvnw.cmd`)
- PostgreSQL disponible en la URL configurada

Comandos basicos:

```powershell
./mvnw.cmd clean test
./mvnw.cmd spring-boot:run
```

## 5. Configuracion

Configuracion principal en `src/main/resources/application.properties`:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.jpa.hibernate.ddl-auto`

## 6. Documentacion tecnica

- Guia de excepciones: `GUIA_EXCEPCIONES.md`
- Decisiones de arquitectura (ADR): `docs/architecture-decisions.md`
- Catalogo de clases: `docs/class-catalog.md`
- Estandares de documentacion: `docs/documentation-standards.md`
- Referencia de API: `docs/api-reference.md`
- Seguridad y autenticacion: `docs/security-auth.md`
- Calidad y pruebas: `docs/testing-quality.md`
- Operacion y configuracion: `docs/operations-config.md`
- Modelo de datos: `docs/database-model.md`

## 7. Criterios de calidad

- Respuestas de error estandarizadas mediante `ApiError`.
- Validacion de entrada en DTO con Jakarta Validation.
- Endpoints protegidos por JWT salvo rutas publicas definidas.
- Cobertura de pruebas en capa web y servicios.

## 8. Convenciones de mantenimiento

- Mantener un lenguaje tecnico formal y consistente.
- Evitar contenido decorativo en documentacion y comentarios.
- Registrar decisiones estructurales en ADR antes de cambios mayores.

