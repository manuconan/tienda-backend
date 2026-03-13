ADR-001 - Decisión: No usar @Data en entidades JPA
Contexto
Se eliminó el uso de Lombok @Data en entidades JPA.

Motivo
@Data genera automáticamente equals() y hashCode() usando todos los campos.

En entidades JPA esto puede provocar:

Problemas con relaciones lazy.

Inconsistencias en colecciones Set.

Cambios de hashCode en campos mutables.

Conflictos con proxies de Hibernate.

Decisión
Se usarán únicamente:

@Getter

@Setter

@NoArgsConstructor

@AllArgsConstructor (cuando aplique)

equals() y hashCode() serán implementados manualmente basados únicamente en el id.

ADR-002 - Estrategia de identidad en entidades JPA
Contexto
Las entidades inicialmente no tenían clave primaria explícita o
no implementaban correctamente equals/hashCode.

Decisión
Todas las entidades JPA deben tener @Id.

Se usa Long autogenerado como clave primaria.

equals() se basa únicamente en id != null.

hashCode() es estable usando getClass().hashCode().

Razón
JPA gestiona identidad por clave primaria.

Evita problemas en colecciones Hash.

Evita inconsistencias cuando id pasa de null a valor.

Compatible con proxies de Hibernate.

Consecuencias
Identidad técnica separada de identidad de negocio.

Modelo consistente y seguro para crecimiento futuro.

ADR-003 - Arquitectura por capas
Contexto
-Se necesita una organización clara del códgigo para separar responsablidades
del backend.

Decisión
El proeycto sigue una arquuitectura por capas:
Controller
Service
Repository
Entity

Razón
Separar responsabilidades permite:
Código más mantenible.
Facilitar el testing.
Reducir el acoplamiento entre componentes.

ADR-004 - Uso de DTO para comunicación con la API
Contexto
Las entidades JPA representan el modelo de persistencia y no deben exponerse directamente en la API REST.

Decisión
Se utilizan DTO para la comunicación entre la API y los clientes.

Tipos utilizados: Request DTO y Response DTO.

Ejemplo: ClienteRequest, ClienteResponse.

Razón
Evitar exponer directamente las entidades JPA.

Controlar qué datos se envían al cliente.

Separar el modelo de persistencia del contrato de la API.

Facilitar cambios en el modelo sin romper la API.

Consecuencias
Se introduce una capa adicional de transformación entre entidades y DTO.

ADR-005 - Uso de Mapper para transformar entidades y DTO
Contexto
La conversión entre entidades JPA y DTO puede generar código repetitivo si se realiza directamente en los servicios o controladores.

Decisión
Se implementan clases Mapper para centralizar la transformación entre entidades y DTO.

Ejemplo: ClienteMapper, ProductoMapper.

Razón
Centralizar la lógica de transformación.

Evitar duplicación de código.

Mantener los servicios y controladores más limpios.

Consecuencias
Se añade una capa adicional encargada únicamente de las transformaciones de datos.

ADR-006 - Manejo global de excepciones
Contexto
Las excepciones pueden lanzarse desde diferentes capas. Sin una estrategia global, cada controlador debería gestionar sus errores manualmente.

Decisión
Se implementa un manejador global de excepciones usando @RestControllerAdvice y @ExceptionHandler.

Este componente intercepta excepciones lanzadas en cualquier controller.

Razón
Centralizar el manejo de errores.

Evitar duplicación de código.

Mantener respuestas de error consistentes en toda la API.

Consecuencias
Todas las excepciones del dominio se gestionan en una única clase central.

ADR-007 - Uso de ApiError como estructura de error de la API
Contexto
Las respuestas de error de una API no deben devolverse como texto simple. Se necesita un formato estructurado.

Decisión
Se introduce una clase ApiError que representa la estructura de error devuelta por la API.

Campos: status, error, message, path, timestamp.

Razón
Proporcionar información clara sobre el error.

Facilitar la integración con clientes frontend.

Mantener consistencia en las respuestas de error.

Consecuencias
Todas las excepciones manejadas por el GlobalExceptionHandler se transforman en un objeto ApiError.

ADR-008 - Validación de datos mediante DTO
Contexto
Los datos recibidos en las peticiones HTTP deben validarse antes de ser procesados por la lógica de negocio.

Decisión
Se utilizan anotaciones de validación de Jakarta Validation en los DTO (@NotNull, @NotBlank, @Size, etc.).

Las validaciones se activan mediante @Valid en los controladores.

Razón
Validar datos antes de llegar a la lógica de negocio.

Reducir errores en capas inferiores.

Proporcionar mensajes claros al cliente.

Consecuencias
Si una validación falla, Spring lanza una excepción gestionada por el GlobalExceptionHandler.

ADR-009 - Estrategia de testing en la aplicación
Contexto
Es necesario verificar que la lógica de negocio y los endpoints de la API funcionan correctamente.

Decisión
Se implementan tests en dos niveles:
Service tests usando Mockito.
Controller tests usando MockMvc.

Razón
Validar la lógica de negocio de forma aislada.

Simular dependencias externas mediante mocks.

Verificar el comportamiento de los endpoints REST.

Consecuencias
El proyecto incluye una capa de testing que asegura la estabilidad del código ante futuros cambios.