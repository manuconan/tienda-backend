# API Reference

## 1. Objetivo

Documentar el contrato HTTP actual de la API para integraciones cliente y pruebas funcionales.

## 2. Convenciones generales

- Base URL local: `http://localhost:8080`
- Formato: `application/json`
- Errores: estructura `ApiError` definida en `GUIA_EXCEPCIONES.md`
- Paginacion Spring: `page`, `size`, `sort`

Ejemplo de ordenacion:

`GET /clientes?page=0&size=10&sort=username,asc`

## 3. Endpoints de autenticacion

### POST `/auth/login`

Autentica un cliente y devuelve token JWT.

Request:

```json
{
  "username": "cliente01",
  "password": "claveSegura123"
}
```

Response 200:

```json
{
  "token": "eyJhbGciOiJI..."
}
```

Errores frecuentes: `401 AUTH_INVALID_CREDENTIALS`, `400 VALIDATION_ERROR`.

## 4. Endpoints de clientes

### POST `/clientes`

Registra cliente.

### GET `/clientes/{id}`

Obtiene cliente por id.

### GET `/clientes`

Lista clientes con paginacion y filtro opcional:

- `username` (opcional)
- `page`, `size`, `sort`

Ejemplos:

- `GET /clientes`
- `GET /clientes?username=manu&page=0&size=5&sort=id,desc`

### DELETE `/clientes/{id}`

Elimina cliente por id.

## 5. Endpoints de productos

### GET `/productos`

Lista productos paginados.

Ejemplo:

- `GET /productos?page=0&size=20&sort=precio,asc`

### GET `/productos/{id}`

Obtiene producto por id.

### POST `/productos`

Crea producto.

### PUT `/productos/{id}`

Actualiza producto existente.

### DELETE `/productos/{id}`

Elimina producto.

## 6. Endpoints de usuarios legacy

### GET `/usuarios`

Lista usuarios paginados.

### GET `/usuarios/{username}`

Obtiene usuario por username.

### POST `/usuarios`

Registra usuario legacy.

## 7. Seguridad por endpoint (estado actual)

- Publico: `/auth/**`, `POST /clientes`, rutas Swagger.
- Requiere autenticacion JWT: resto de endpoints.

## 8. Referencias de codigo

- `src/main/java/manuel/tienda/auth/controller/AuthController.java`
- `src/main/java/manuel/tienda/cliente/controller/ClienteController.java`
- `src/main/java/manuel/tienda/producto/controller/ProductoController.java`
- `src/main/java/manuel/tienda/Usuario/controller/UsuarioController.java`
- `src/main/java/manuel/tienda/auth/config/SecurityConfig.java`

