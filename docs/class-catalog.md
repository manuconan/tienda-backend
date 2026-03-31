# Catalogo de Clases

## 1. Objetivo

Documentar responsabilidades y relaciones de las clases de `src/main/java` para facilitar mantenimiento, auditoria tecnica y onboarding.

## 2. Convenciones

- La descripcion es funcional, no una copia literal del codigo.
- Se identifica la capa arquitectonica de cada clase.
- Este catalogo debe actualizarse en cada cambio estructural.

## 3. Modulo raiz y configuracion

| Clase | Capa | Responsabilidad |
|------|------|-----------------|
| `MicroservicioSpringBootCoreDeTiendaOnlineApplication` | Bootstrap | Punto de entrada de Spring Boot. |
| `ConfigurationParameters` | Config | Exposicion de propiedades de aplicacion tipadas. |
| `SecurityBeansConfig` | Config | Bean `PasswordEncoder` y componentes base de seguridad. |

## 4. Modulo `auth`

| Clase | Capa | Responsabilidad |
|------|------|-----------------|
| `SecurityConfig` | Config | Reglas de autorizacion, sesion stateless y registro de filtro JWT. |
| `AuthController` | Controller | Endpoint de login (`/auth/login`). |
| `LoginRequest` | DTO | Datos de entrada para autenticacion. |
| `LoginResponse` | DTO | Respuesta con token JWT. |
| `JwtFilter` | Security | Valida token y establece `SecurityContext`. |
| `AuthService` | Service | Valida credenciales y emite token. |
| `JwtService` | Service | Genera, valida y parsea JWT. |

Dependencias clave: `AuthService` usa `ClienteRepository`, `PasswordEncoder`, `JwtService`.

## 5. Modulo `cliente`

| Clase | Capa | Responsabilidad |
|------|------|-----------------|
| `ClienteController` | Controller | API de clientes con paginacion y filtros. |
| `ClienteRequest` | DTO | Entrada para alta/actualizacion. |
| `ClienteActivoRequest` | DTO | Entrada para cambio de estado activo. |
| `ClienteResponse` | DTO | Salida de cliente para API. |
| `Cliente` | Entity | Identidad de usuario del sistema y datos de autenticacion. |
| `ClienteMapper` | Mapper | Conversion entidad a DTO de salida. |
| `ClienteRepository` | Repository | Persistencia y busquedas por username/estado. |
| `Role` | Enum | Roles de seguridad (`USER`, `ADMIN`). |
| `ClienteService` | Service | Reglas de negocio CRUD y gestion de credenciales. |

## 6. Modulo `producto`

| Clase | Capa | Responsabilidad |
|------|------|-----------------|
| `ProductoController` | Controller | API REST de productos con `Pageable`. |
| `ProductoRequest` | DTO | Entrada para crear/actualizar producto. |
| `ProductoResponse` | DTO | Salida de producto para API. |
| `Producto` | Entity | Modelo persistente de catalogo y stock. |
| `ProductoMapper` | Mapper | Conversion entre entidad y DTO. |
| `ProductoRepository` | Repository | Operaciones JPA de productos. |
| `ProductoService` | Service | Validaciones de negocio y operaciones CRUD. |

## 7. Modulo `Usuario` (legacy)

| Clase | Capa | Responsabilidad |
|------|------|-----------------|
| `UsuarioController` | Controller | API de usuarios legacy. |
| `UsuarioRequest` | DTO | Entrada de usuario legacy. |
| `UsuarioResponse` | DTO | Salida de usuario legacy. |
| `Usuario` | Entity | Entidad de usuario con relacion a `Rol`. |
| `Rol` | Entity/Enum-like | Catalogo de roles legacy. |
| `UsuarioMapper` | Mapper | Conversion entidad y DTO en modulo legacy. |
| `UsuarioRepository` | Repository | Persistencia de usuarios legacy. |
| `UsuarioService` | Service | Logica de alta y busqueda de usuarios legacy. |

## 8. Modulo `exception`

| Clase | Capa | Responsabilidad |
|------|------|-----------------|
| `ApiError` | Contract | Estructura comun de errores HTTP. |
| `GlobalExceptionHandler` | Advice | Mapeo centralizado de excepciones a respuestas HTTP. |
| `ClienteExisteException` | Exception | Duplicado de cliente. |
| `ClienteNoEncontradoException` | Exception | Cliente no encontrado. |
| `ClienteYaExisteException` | Exception | Conflicto de estado en cliente. |
| `ProductoInvalidoException` | Exception | Datos invalidos de producto. |
| `ProductoNoEncontradoException` | Exception | Producto no encontrado. |
| `InsufficientStockException` | Exception | Regla de stock insuficiente. |

## 9. Criterio de gobierno

- Cualquier clase nueva debe registrarse aqui en el mismo PR.
- Cambios de responsabilidad deben reflejarse en ADR y en este catalogo.

