# Testing y Calidad

## 1. Objetivo

Definir estrategia de pruebas y criterios de calidad para despliegues confiables.

## 2. Estado actual de pruebas

Pruebas detectadas en `src/test/java`:

- `cliente/controller/ClienteControllerTest`
- `producto/controller/ProductoControllerTest`
- `producto/ProductoServiceTest`
- `Usuario/controller/UsuarioControllerTest`

## 3. Cobertura minima recomendada

- Controladores: flujos 2xx, validaciones 4xx y seguridad 401/403.
- Servicios: reglas de negocio y escenarios de excepcion.
- Seguridad: login, token invalido/expirado, acceso autorizado/no autorizado.

## 4. Piramide de pruebas propuesta

1. Unitarias de servicio (rapidas, aisladas).
2. WebMvc para contrato HTTP.
3. Integracion con BD en entorno controlado (fase evolutiva).

## 5. Criterios de aceptacion de calidad

- Todo cambio funcional incorpora o actualiza pruebas.
- Todo bug corregido incluye prueba de no regresion.
- No se aceptan PR sin validar `mvn test`.

## 6. Comandos

```powershell
./mvnw.cmd clean test
```

## 7. Referencias de codigo

- `src/test/java/manuel/tienda/cliente/controller/ClienteControllerTest.java`
- `src/test/java/manuel/tienda/producto/controller/ProductoControllerTest.java`
- `src/test/java/manuel/tienda/producto/ProductoServiceTest.java`
- `src/test/java/manuel/tienda/Usuario/controller/UsuarioControllerTest.java`

