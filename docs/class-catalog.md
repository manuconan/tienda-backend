# Catalogo de Clases

## Objetivo

Este documento centraliza la responsabilidad funcional de cada clase del proyecto para facilitar mantenimiento, onboarding y revisiones tecnicas.

## Convenciones

- Se documentan clases de `src/main/java` por modulo.
- Las responsabilidades se describen a nivel de arquitectura, no de implementacion interna.
- Las clases auxiliares o de infraestructura se mantienen separadas del dominio.

## Modulo base y configuracion

| Clase | Ubicacion | Responsabilidad |
|------|-----------|-----------------|
| `MicroservicioSpringBootCoreDeTiendaOnlineApplication` | `src/main/java/manuel/tienda/` | Punto de arranque de Spring Boot. |
| `SecurityBeansConfig` | `src/main/java/manuel/tienda/config/` | Define beans de seguridad compartidos. |
| `ConfigurationParameters` | `src/main/java/manuel/tienda/config/` | Centraliza parametros de configuracion de aplicacion. |

## Modulo de autenticacion (`auth`)

| Clase | Ubicacion | Responsabilidad |
|------|-----------|-----------------|
| `SecurityConfig` | `src/main/java/manuel/tienda/auth/config/` | Configura reglas de seguridad HTTP y filtros. |
| `AuthController` | `src/main/java/manuel/tienda/auth/controller/` | Expone endpoints de login/autenticacion. |
| `LoginRequest` | `src/main/java/manuel/tienda/auth/dto/` | DTO de entrada para autenticacion. |
| `LoginResponse` | `src/main/java/manuel/tienda/auth/dto/` | DTO de salida con token/datos de sesion. |
| `JwtFilter` | `src/main/java/manuel/tienda/auth/filter/` | Intercepta peticiones y establece contexto de seguridad desde JWT. |
| `AuthService` | `src/main/java/manuel/tienda/auth/service/` | Coordina validacion de credenciales y emision de token. |
| `JwtService` | `src/main/java/manuel/tienda/auth/service/` | Genera, valida y parsea tokens JWT. |

## Modulo de clientes (`cliente`)

| Clase | Ubicacion | Responsabilidad |
|------|-----------|-----------------|
| `ClienteController` | `src/main/java/manuel/tienda/cliente/controller/` | Endpoints REST de gestion de clientes. |
| `ClienteRequest` | `src/main/java/manuel/tienda/cliente/dto/` | DTO de alta/actualizacion de cliente. |
| `ClienteActivoRequest` | `src/main/java/manuel/tienda/cliente/dto/` | DTO para cambios de estado activo/inactivo. |
| `ClienteResponse` | `src/main/java/manuel/tienda/cliente/dto/` | DTO de respuesta de cliente para API. |
| `Cliente` | `src/main/java/manuel/tienda/cliente/entity/` | Entidad persistente de cliente/usuario del sistema. |
| `ClienteMapper` | `src/main/java/manuel/tienda/cliente/mapper/` | Convierte entre entidad `Cliente` y DTO. |
| `ClienteRepository` | `src/main/java/manuel/tienda/cliente/repository/` | Acceso a datos de clientes y consultas de autenticacion. |
| `Role` | `src/main/java/manuel/tienda/cliente/role/` | Enumeracion de roles de seguridad para clientes. |
| `ClienteService` | `src/main/java/manuel/tienda/cliente/service/` | Logica de negocio y orquestacion CRUD de clientes. |

## Modulo de productos (`producto`)

| Clase | Ubicacion | Responsabilidad |
|------|-----------|-----------------|
| `ProductoController` | `src/main/java/manuel/tienda/producto/controller/` | Endpoints REST para operaciones de producto. |
| `ProductoRequest` | `src/main/java/manuel/tienda/producto/dto/` | DTO de entrada para crear/actualizar producto. |
| `ProductoResponse` | `src/main/java/manuel/tienda/producto/dto/` | DTO de salida de producto para la API. |
| `Producto` | `src/main/java/manuel/tienda/producto/entity/` | Entidad persistente de producto y stock. |
| `ProductoMapper` | `src/main/java/manuel/tienda/producto/mapper/` | Conversor entre entidad `Producto` y DTO. |
| `ProductoRepository` | `src/main/java/manuel/tienda/producto/repository/` | Repositorio de persistencia para productos. |
| `ProductoService` | `src/main/java/manuel/tienda/producto/service/` | Logica de negocio del dominio de productos. |

## Modulo de usuarios legacy (`Usuario`)

| Clase | Ubicacion | Responsabilidad |
|------|-----------|-----------------|
| `UsuarioController` | `src/main/java/manuel/tienda/Usuario/controller/` | Endpoints REST del modulo de usuarios. |
| `UsuarioRequest` | `src/main/java/manuel/tienda/Usuario/dto/` | DTO de entrada de usuario. |
| `UsuarioResponse` | `src/main/java/manuel/tienda/Usuario/dto/` | DTO de salida de usuario. |
| `Rol` | `src/main/java/manuel/tienda/Usuario/entity/` | Enumeracion de roles del modulo `Usuario`. |
| `Usuario` | `src/main/java/manuel/tienda/Usuario/entity/` | Entidad persistente de usuario. |
| `UsuarioMapper` | `src/main/java/manuel/tienda/Usuario/mapper/` | Conversor entidad/DTO del modulo `Usuario`. |
| `UsuarioRepository` | `src/main/java/manuel/tienda/Usuario/repository/` | Repositorio JPA de usuarios. |
| `UsuarioService` | `src/main/java/manuel/tienda/Usuario/service/` | Servicios de negocio del modulo `Usuario`. |

## Modulo de excepciones (`exception`)

| Clase | Ubicacion | Responsabilidad |
|------|-----------|-----------------|
| `ApiError` | `src/main/java/manuel/tienda/exception/` | Contrato estandar para respuestas de error. |
| `GlobalExceptionHandler` | `src/main/java/manuel/tienda/exception/` | Mapeo global de excepciones a HTTP y `ApiError`. |
| `ClienteExisteException` | `src/main/java/manuel/tienda/exception/` | Error de duplicado de cliente en alta. |
| `ClienteNoEncontradoException` | `src/main/java/manuel/tienda/exception/` | Error cuando cliente no existe. |
| `ClienteYaExisteException` | `src/main/java/manuel/tienda/exception/` | Error de conflicto de estado de cliente. |
| `InsufficientStockException` | `src/main/java/manuel/tienda/exception/` | Error por stock insuficiente. |
| `ProductoInvalidoException` | `src/main/java/manuel/tienda/exception/` | Error por datos invalidos de producto. |
| `ProductoNoEncontradoException` | `src/main/java/manuel/tienda/exception/` | Error cuando producto no existe. |

## Recomendaciones de documentacion de codigo

- Añadir Javadoc de clase y metodos publicos en controladores, servicios y mappers.
- Evitar comentarios decorativos o informales; priorizar explicaciones de negocio.
- Mantener un idioma unico en la documentacion tecnica (espanol o ingles).
- Actualizar este catalogo cuando se agreguen o eliminen clases.

