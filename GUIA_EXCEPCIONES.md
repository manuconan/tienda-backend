# Guia de Excepciones - Tienda Online API

## Objetivo

Este documento define las reglas para lanzar, mapear y mantener excepciones de dominio de forma consistente en toda la API.

## Criterios de uso

### Producto no existe o datos invalidos

```java
// HTTP 404 - Producto no existe
throw new ProductoNoEncontradoException(id);

// HTTP 400 - Datos de entrada invalidos
throw new ProductoInvalidoException("El precio debe ser mayor que cero");
```

### Cliente duplicado o no encontrado

```java
// HTTP 400 - Duplicado en registro
throw new ClienteExisteException();

// HTTP 404 - Recurso no encontrado
throw new ClienteNoEncontradoException();

// HTTP 409 - Conflicto de estado
throw new ClienteYaExisteException("El cliente ya existe con este ID");
```

### Stock insuficiente

```java
// HTTP 400 - Regla de negocio incumplida
throw new InsufficientStockException("Stock insuficiente para la compra");
```

### Argumentos invalidos

```java
// HTTP 400 - Validacion basica de argumentos
throw new IllegalArgumentException("Username no puede estar vacio");
```

## Catalogo de excepciones

| Excepcion | HTTP | Uso recomendado | Ejemplo |
|-----------|------|------------------|---------|
| `ProductoInvalidoException` | 400 | Datos de producto invalidos | Precio negativo |
| `ProductoNoEncontradoException` | 404 | Producto inexistente | `GET /productos/999` |
| `ClienteExisteException` | 400 | Duplicado en registro | Username repetido |
| `ClienteNoEncontradoException` | 404 | Cliente inexistente | `GET /clientes/999` |
| `ClienteYaExisteException` | 409 | Conflicto de estado | Crear cliente con ID existente |
| `InsufficientStockException` | 400 | Stock insuficiente | Comprar 100 con stock 50 |
| `IllegalArgumentException` | 400 | Argumento invalido | Username vacio |

## Formato estandar de error

La API debe responder con una estructura uniforme basada en `ApiError`:

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Descripcion del error",
  "path": "/api/endpoint",
  "timestamp": "2026-03-17 19:23:13"
}
```

## Checklist de calidad para Pull Requests

- [ ] Se lanza una excepcion de dominio adecuada al caso.
- [ ] El estado HTTP mapeado es coherente con el error.
- [ ] El mensaje de error es accionable para cliente o integrador.
- [ ] La excepcion esta registrada en `GlobalExceptionHandler`.
- [ ] Existen pruebas para el flujo exitoso y de error.

## Referencias

- `src/main/java/manuel/tienda/exception/GlobalExceptionHandler.java`
- `src/main/java/manuel/tienda/exception/ApiError.java`
- `src/main/java/manuel/tienda/exception/*.java`

## Resolucion de incidencias frecuentes

### Se obtiene HTTP 500 cuando deberia ser 4xx

La excepcion no esta siendo interceptada por `GlobalExceptionHandler` o se esta relanzando como `Exception` generica.

### Diferencia entre `ClienteExisteException` y `ClienteYaExisteException`

- `ClienteExisteException` (400): duplicado durante el alta.
- `ClienteYaExisteException` (409): conflicto por estado actual del recurso.

### Alta de nuevas excepciones

1. Crear la excepcion en `exception/` extendiendo `RuntimeException`.
2. Registrar su handler en `GlobalExceptionHandler`.
3. Definir o reutilizar un codigo de error estable.
4. Cubrir el caso con pruebas de servicio y/o controlador.

