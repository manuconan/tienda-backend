## ADR-001 - Decisión: No usar @Data en entidades JPA

## Contexto

Se eliminó el uso de Lombok @Data en entidades JPA.

## Motivo

@Data genera automáticamente equals() y hashCode() usando todos los campos.

En entidades JPA esto puede provocar:
- Problemas con relaciones lazy.
- Inconsistencias en colecciones Set.
- Cambios de hashCode en campos mutables.
- Conflictos con proxies de Hibernate.

## Decisión

Se usarán únicamente:
- @Getter
- @Setter
- @NoArgsConstructor
- @AllArgsConstructor (cuando aplique)

equals() y hashCode() serán implementados manualmente basados únicamente en el id.

## ADR-002 - Estrategia de identidad en entidades JPA

### Contexto
Las entidades inicialmente no tenían clave primaria explícita o
no implementaban correctamente equals/hashCode.

### Decisión
- Todas las entidades JPA deben tener @Id.
- Se usa Long autogenerado como clave primaria.
- equals() se basa únicamente en id != null.
- hashCode() es estable usando getClass().hashCode().

### Razón
- JPA gestiona identidad por clave primaria.
- Evita problemas en colecciones Hash.
- Evita inconsistencias cuando id pasa de null a valor.
- Compatible con proxies de Hibernate.

### Consecuencias
- Identidad técnica separada de identidad de negocio.
- Modelo consistente y seguro para crecimiento futuro.
