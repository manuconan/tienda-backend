# Modelo de Datos

## 1. Objetivo

Describir el modelo persistente actual y sus relaciones para soporte funcional y evolutivo.

## 2. Entidades principales

### `clientes`

Definida por `Cliente`:

- `id` (PK, autogenerada)
- `username` (unico, no nulo)
- `passwordHash` (no nulo, hash BCrypt)
- `activo` (no nulo)
- `role` (enum)

### `productos`

Definida por `Producto`:

- `id` (PK, autogenerada)
- `nombre` (no nulo)
- `precio` (decimal 10,2, no nulo)
- `stock` (no nulo)

### `usuarios` y `usuarios_roles` (legacy)

Definida por `Usuario` y `Rol`:

- `usuarios.id`, `usuarios.username`, `usuarios.password`, `usuarios.enabled`
- Relacion many-to-many con `roles` por tabla intermedia `usuarios_roles`

## 3. Reglas relevantes

- `username` en clientes y usuarios debe ser unico.
- Credenciales se comparan mediante `PasswordEncoder`.
- `ddl-auto=update` permite evolucion automatica en local.

## 4. Recomendaciones de gobierno de esquema

- Introducir migraciones versionadas para entornos productivos.
- Definir indices para columnas de busqueda frecuente (`username`).
- Evitar cambios manuales no versionados sobre el esquema.

## 5. Referencias de codigo

- `src/main/java/manuel/tienda/cliente/entity/Cliente.java`
- `src/main/java/manuel/tienda/producto/entity/Producto.java`
- `src/main/java/manuel/tienda/Usuario/entity/Usuario.java`
- `src/main/resources/application.properties`

