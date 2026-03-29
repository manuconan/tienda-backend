# Architecture Decisions (ADR)

## ADR-001: No usar `@Data` en entidades JPA

### Contexto
Se elimino el uso de Lombok `@Data` en entidades JPA para evitar efectos no deseados sobre identidad y relaciones.

### Decision
En entidades se usan `@Getter`, `@Setter`, `@NoArgsConstructor` y `@AllArgsConstructor` (cuando aplica).
Los metodos `equals()` y `hashCode()` se implementan manualmente basados en `id`.

### Razon
`@Data` genera `equals()` y `hashCode()` con todos los campos, lo que puede romper colecciones, relaciones `lazy` y proxies de Hibernate.

### Consecuencias
Mejor control de identidad y menor riesgo de errores de persistencia.

## ADR-002: Estrategia de identidad en entidades JPA

### Contexto
Se requiere una politica uniforme de identidad para evitar inconsistencias en persistencia y colecciones.

### Decision
Todas las entidades usan `@Id` con `Long` autogenerado.
`equals()` se basa en `id != null` y `hashCode()` en `getClass().hashCode()`.

### Razon
JPA gestiona identidad por clave primaria y esta estrategia es estable para entidades con ciclo de vida mutable.

### Consecuencias
Modelo consistente, compatible con proxies y seguro para evolucion futura.

## ADR-003: Arquitectura por capas

### Contexto
Se necesita separar responsabilidades para mejorar mantenibilidad y testabilidad.

### Decision
El proyecto adopta una arquitectura por capas:

- Controller
- Service
- Repository
- Entity/DTO/Mapper

### Razon
Reduce acoplamiento y favorece cambios localizados.

### Consecuencias
Mayor claridad estructural y mejor cobertura de pruebas por capa.

## ADR-004: Uso de DTO en la API

### Contexto
Las entidades JPA no deben exponerse directamente como contrato HTTP.

### Decision
La API usa DTO de entrada y salida (por ejemplo, `ClienteRequest` y `ClienteResponse`).

### Razon
Protege el modelo de persistencia y permite evolucionar la API sin acoplarla a la base de datos.

### Consecuencias
Se introduce una capa explicita de transformacion.

## ADR-005: Uso de mappers para conversion entidad/DTO

### Contexto
La conversion manual repetida en controladores y servicios genera duplicacion.

### Decision
La conversion se centraliza en clases mapper (`ClienteMapper`, `ProductoMapper`, `UsuarioMapper`).

### Razon
Evita duplicaciones y mantiene servicios/controladores enfocados en logica de negocio.

### Consecuencias
Se agrega una capa adicional dedicada a transformaciones.

## ADR-006: Manejo global de excepciones

### Contexto
Sin una estrategia global, cada controlador deberia resolver errores localmente.

### Decision
Se usa `@RestControllerAdvice` con `@ExceptionHandler` para centralizar el manejo de errores.

### Razon
Permite respuestas coherentes y reduce codigo repetido.

### Consecuencias
Todas las excepciones de dominio se gestionan en un unico punto.

## ADR-007: Estructura de error estandar (`ApiError`)

### Contexto
Las respuestas de error deben ser estructuradas y estables para integraciones.

### Decision
La API devuelve errores usando `ApiError` con campos de contexto.

### Razon
Mejora trazabilidad y consumo por frontend u otros clientes.

### Consecuencias
Los handlers convierten excepciones a una estructura uniforme.

## ADR-008: Validacion en DTO con Jakarta Validation

### Contexto
La validacion debe ocurrir antes de ejecutar logica de negocio.

### Decision
Se usan anotaciones de validacion (`@NotNull`, `@NotBlank`, `@Size`, etc.) y `@Valid` en endpoints.

### Razon
Reduce errores aguas abajo y mejora calidad de respuesta al cliente.

### Consecuencias
Los fallos de validacion se gestionan por el manejador global de excepciones.

## ADR-009: Estrategia de testing

### Contexto
Es necesario asegurar comportamiento de negocio y de endpoints.

### Decision
Se implementan dos niveles de pruebas:

- Servicio con Mockito.
- Controlador con MockMvc.

### Razon
Permite validacion aislada de logica y verificacion de contratos HTTP.

### Consecuencias
Mayor estabilidad y deteccion temprana de regresiones.
