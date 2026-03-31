# Seguridad y Autenticacion

## 1. Objetivo

Documentar el modelo de autenticacion/autorizacion actual y los criterios minimos de seguridad.

## 2. Componentes

- `SecurityConfig`: define reglas HTTP y modo stateless.
- `JwtFilter`: valida token Bearer y construye autenticacion.
- `AuthService`: valida credenciales de `Cliente`.
- `JwtService`: genera y valida tokens.
- `SecurityBeansConfig`: define `PasswordEncoder`.

## 3. Flujo de autenticacion

1. Cliente invoca `POST /auth/login` con username/password.
2. `AuthService` busca usuario en `ClienteRepository`.
3. Se valida password con `PasswordEncoder.matches`.
4. `JwtService` emite token con subject y claim `role`.
5. En peticiones posteriores se envia `Authorization: Bearer <token>`.
6. `JwtFilter` valida token y asigna autoridades al `SecurityContext`.

## 4. Reglas de autorizacion

Segun `SecurityConfig`:

- `permitAll`: `/auth/**`, Swagger y `POST /clientes`.
- `authenticated`: cualquier otra ruta.

## 5. Politicas tecnicas

- Sesion stateless (`SessionCreationPolicy.STATELESS`).
- `httpBasic`, `formLogin` y `logout` deshabilitados.
- Entrada no autenticada responde `401`.

## 6. Riesgos y mejoras recomendadas

- Externalizar secreto JWT a variable de entorno.
- Configurar expiracion token via propiedad.
- Registrar auditoria de login correcto/fallido.
- Añadir pruebas de integracion para rutas protegidas.

## 7. Referencias de codigo

- `src/main/java/manuel/tienda/auth/config/SecurityConfig.java`
- `src/main/java/manuel/tienda/auth/filter/JwtFilter.java`
- `src/main/java/manuel/tienda/auth/service/AuthService.java`
- `src/main/java/manuel/tienda/auth/service/JwtService.java`
- `src/main/java/manuel/tienda/config/SecurityBeansConfig.java`

