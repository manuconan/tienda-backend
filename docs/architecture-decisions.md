# Architecture Decisions (ADR)

## Formato ADR

Cada decision sigue esta estructura:

- Estado: Proposed | Accepted | Deprecated
- Contexto
- Decision
- Alternativas consideradas
- Impacto

## ADR-001 - Arquitectura por capas

- Estado: Accepted
- Contexto: Se requiere separacion clara entre API, negocio y persistencia.
- Decision: Organizacion en `controller`, `service`, `repository`, `entity`, `dto`, `mapper`.
- Alternativas consideradas: enfoque monolitico sin capas.
- Impacto: mayor mantenibilidad y testabilidad por componente.

## ADR-002 - DTO como contrato HTTP

- Estado: Accepted
- Contexto: Exponer entidades JPA acopla API con persistencia.
- Decision: Usar DTO de entrada/salida en todos los endpoints.
- Alternativas consideradas: exponer entidad directamente.
- Impacto: contrato estable y desacoplado de cambios internos.

## ADR-003 - Mappers dedicados

- Estado: Accepted
- Contexto: La conversion entidad/DTO se repetia en capas no responsables.
- Decision: Centralizar conversion en `ClienteMapper`, `ProductoMapper`, `UsuarioMapper`.
- Alternativas consideradas: conversion inline en controladores/servicios.
- Impacto: menor duplicidad y reglas de transformacion trazables.

## ADR-004 - Manejo global de excepciones

- Estado: Accepted
- Contexto: Manejo local por controlador produce respuestas inconsistentes.
- Decision: Centralizar errores con `GlobalExceptionHandler` y `ApiError`.
- Alternativas consideradas: manejo por endpoint.
- Impacto: consistencia funcional y mejor observabilidad.

## ADR-005 - Seguridad stateless con JWT

- Estado: Accepted
- Contexto: Se necesita autenticacion sin estado para API REST.
- Decision: Configuracion Spring Security stateless + `JwtFilter` + `JwtService`.
- Alternativas consideradas: sesiones HTTP, basic auth.
- Impacto: escalabilidad horizontal y desacople del estado de sesion.

## ADR-006 - Cliente como identidad del sistema

- Estado: Accepted
- Contexto: La autenticacion actual utiliza `ClienteRepository`.
- Decision: `AuthService` valida credenciales contra `Cliente` y emite JWT.
- Alternativas consideradas: autenticacion sobre modulo `Usuario`.
- Impacto: unifica identidad funcional sobre el dominio `cliente`.

## ADR-007 - Password hash con BCrypt

- Estado: Accepted
- Contexto: Contraseñas no deben almacenarse en texto plano.
- Decision: Persistir hash y validar con `PasswordEncoder.matches`.
- Alternativas consideradas: hash manual o algoritmos inseguros.
- Impacto: cumplimiento basico de seguridad para credenciales.

## ADR-008 - Paginacion y ordenacion por Pageable

- Estado: Accepted
- Contexto: Los listados deben escalar y permitir orden configurable.
- Decision: Endpoints de listado usan `Pageable` y parametros `page`, `size`, `sort`.
- Alternativas consideradas: listado completo sin pagina.
- Impacto: menor consumo de memoria y mejor experiencia de integracion.

## ADR-009 - Validacion de entrada en DTO

- Estado: Accepted
- Contexto: Se necesita validar datos antes de ejecutar logica de negocio.
- Decision: Usar Jakarta Validation (`@NotBlank`, `@Size`, etc.) con `@Valid`.
- Alternativas consideradas: validacion manual en servicios.
- Impacto: errores tempranos y respuestas de validacion uniformes.

## ADR-010 - Estrategia de pruebas por capas

- Estado: Accepted
- Contexto: Se requiere deteccion temprana de regresiones.
- Decision: pruebas web con MockMvc y pruebas de servicio con mocks.
- Alternativas consideradas: solo pruebas manuales.
- Impacto: mayor confianza en cambios y despliegues.
