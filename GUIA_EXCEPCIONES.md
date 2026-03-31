# Guia de Excepciones

## 1. Objetivo

Definir una politica unica para lanzar, mapear y mantener excepciones de dominio con respuestas HTTP consistentes.

## 2. Alcance

Esta guia aplica a:

- Servicios de dominio (`cliente`, `producto`, `auth`, `Usuario`).
- Capa web (controladores REST).
- Manejo global (`GlobalExceptionHandler`).

## 3. Contrato de error

Todas las respuestas de error se serializan con `ApiError`:

```json
{
  "timestamp": "2026-03-31 09:00:00",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "username: no puede estar vacio",
  "path": "/clientes"
}
```

Campos obligatorios:

- `timestamp`: fecha/hora del error.
- `status`: codigo HTTP numerico.
- `code`: identificador estable para integracion.
- `message`: descripcion legible del problema.
- `path`: endpoint solicitado.

## 4. Taxonomia y mapeo HTTP

| Excepcion | HTTP | Codigo interno | Uso esperado |
|-----------|------|----------------|--------------|
| `ProductoNoEncontradoException` | 404 | `RESOURCE_NOT_FOUND` | Recurso de producto inexistente |
| `ClienteNoEncontradoException` | 404 | `RESOURCE_NOT_FOUND` | Recurso de cliente/usuario inexistente |
| `ProductoInvalidoException` | 400 | `BAD_REQUEST` | Regla de negocio invalida en producto |
| `InsufficientStockException` | 400 | `BAD_REQUEST` | Operacion no valida por stock |
| `MethodArgumentNotValidException` | 400 | `VALIDATION_ERROR` | Error de validacion DTO (`@Valid`) |
| `IllegalArgumentException` | 400 | `INVALID_ARGUMENT` | Argumento invalido en servicio/controlador |
| `HttpMessageNotReadableException` | 400 | `MALFORMED_JSON` | Body JSON mal formado |
| `ClienteExisteException` | 409 | `RESOURCE_CONFLICT` | Duplicado de recurso |
| `ClienteYaExisteException` | 409 | `RESOURCE_CONFLICT` | Conflicto de estado |
| `BadCredentialsException` | 401 | `AUTH_INVALID_CREDENTIALS` | Credenciales invalidas en login |
| `Exception` | 500 | `INTERNAL_ERROR` | Error no clasificado |

## 5. Reglas de implementacion

1. Lanzar excepciones especificas de dominio desde `service`.
2. No construir respuestas HTTP de error en servicios.
3. Centralizar el mapeo HTTP en `GlobalExceptionHandler`.
4. Mantener codigos `code` estables entre versiones.
5. Evitar mensajes tecnicos internos en errores 4xx/5xx.

## 6. Proceso para nuevas excepciones

1. Crear clase en `src/main/java/manuel/tienda/exception/`.
2. Registrar `@ExceptionHandler` correspondiente.
3. Definir codigo de error estable y semantico.
4. Agregar pruebas del flujo de error.
5. Actualizar esta guia y `docs/api-reference.md` si aplica.

## 7. Control de calidad

- Cada endpoint debe documentar errores de negocio esperados.
- Toda validacion de DTO debe reflejarse como `VALIDATION_ERROR`.
- Los errores 500 deben registrarse en log con stacktrace.

## 8. Referencias de codigo

- `src/main/java/manuel/tienda/exception/ApiError.java`
- `src/main/java/manuel/tienda/exception/GlobalExceptionHandler.java`
- `src/main/java/manuel/tienda/exception/`

