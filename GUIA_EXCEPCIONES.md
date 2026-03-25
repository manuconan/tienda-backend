# 🚨 GUÍA DE EXCEPCIONES - Tienda Online API

## 📌 Para Desarrolladores

### ¿Cómo lanzar excepciones correctamente?

#### 1. **Producto no existe o no es válido**
```java
// HTTP 404 - Producto no existe
throw new ProductoNoEncontradoException(id);

// HTTP 400 - Datos inválidos
throw new ProductoInvalidoException("El precio debe ser mayor que cero");
```

#### 2. **Cliente duplicado o no existe**
```java
// HTTP 400 - Cliente ya existe (duplicado en registro)
throw new ClienteExisteException();

// HTTP 404 - Cliente no existe
throw new ClienteNoEncontradoException();

// HTTP 409 - Conflicto de estado
throw new ClienteYaExisteException("El cliente ya existe con este ID");
```

#### 3. **Stock insuficiente**
```java
// HTTP 400 - No hay suficiente stock
throw new InsufficientStockException("Stock insuficiente para la compra");
```

#### 4. **Argumentos inválidos**
```java
// HTTP 400 - Argumento nulo o vacío
throw new IllegalArgumentException("Username no puede estar vacío");
```

---

## 📊 Tabla de Excepciones

| Excepción | HTTP | Cuándo usar | Ejemplo |
|-----------|------|-----------|---------|
| ProductoInvalidoException | 400 | Datos del producto inválidos | Precio negativo |
| ProductoNoEncontradoException | 404 | Producto no existe | GET /productos/999 |
| ClienteExisteException | 400 | Cliente duplicado en registro | Registrar username duplicado |
| ClienteNoEncontradoException | 404 | Cliente no existe | GET /clientes/999 |
| ClienteYaExisteException | 409 | Conflicto de estado | Crear cliente con ID existente |
| InsufficientStockException | 400 | Stock insuficiente | Comprar 100 con 50 en stock |
| IllegalArgumentException | 400 | Argumento inválido | Username vacío |

---

## 🎯 Respuestas de Error Estándar

Todas las excepciones devuelven:

```json
{
  "timestamp": "2026-03-17T19:23:13.254894+01:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "path": "/api/endpoint"
}
```

---

## ✅ Checklist para Pull Requests

- [ ] ¿Lanzé una excepción apropiada?
- [ ] ¿El código HTTP es correcto?
- [ ] ¿El mensaje es claro para el cliente?
- [ ] ¿Está la excepción en GlobalExceptionHandler?
- [ ] ¿Pasaron los tests?

---

## 🔗 Referencias

- **GlobalExceptionHandler:** `src/main/java/manuel/tienda/exception/GlobalExceptionHandler.java`
- **ApiError:** `src/main/java/manuel/tienda/exception/ApiError.java`
- **Excepciones:** `src/main/java/manuel/tienda/exception/*.java`

---

## 🆘 Troubleshooting

**P: Veo error 500 en lugar de 400**
R: Probablemente lanzaste una excepción sin handler. Verifica que esté en GlobalExceptionHandler.

**P: ¿Cuál es la diferencia entre ClienteExisteException y ClienteYaExisteException?**
R: 
- `ClienteExisteException` (400): Duplicado al registrar
- `ClienteYaExisteException` (409): Conflicto de estado

**P: ¿Puedo crear una excepción nueva?**
R: Sí, pero debes:
1. Crearla en el paquete `exception/`
2. Heredar de `RuntimeException`
3. Agregar un handler en `GlobalExceptionHandler`

