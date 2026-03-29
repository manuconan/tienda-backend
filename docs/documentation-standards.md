# Estandares de Documentacion

## Objetivo

Definir un criterio unico para documentar el proyecto con calidad profesional y consistencia tecnica.

## Reglas para JavaDoc

Aplicar JavaDoc en:

- Todas las clases e interfaces publicas.
- Metodos publicos de `controller`, `service`, `mapper` y `exception`.
- Metodos no publicos solo cuando encapsulan logica compleja o no obvia.

Plantilla recomendada:

```java
/**
 * Describe la responsabilidad de la clase.
 * Explica el alcance funcional y la capa arquitectonica.
 */
public class EjemploService {

    /**
     * Describe que hace el metodo y su regla principal.
     *
     * @param id identificador del recurso
     * @return dto de salida
     * @throws RecursoNoEncontradoException cuando el id no existe
     */
    public EjemploResponse obtenerPorId(Long id) {
        // ...
    }
}
```

## Reglas para Markdown

- Usar lenguaje tecnico y neutral.
- Evitar iconos, dibujos, emojis y texto decorativo.
- Mantener titulos claros y secciones estables: objetivo, contexto, decision, razon, consecuencias.
- Incluir rutas de archivo con backticks.

## Reglas de estilo

- Un solo idioma en todo el repositorio (preferentemente espanol tecnico).
- Evitar abreviaturas ambiguas.
- Evitar comentarios redundantes que describan lo obvio.
- Priorizar explicaciones de negocio sobre detalles triviales de sintaxis.

## Criterios de aceptacion para PR

- Toda clase nueva incluye JavaDoc de clase.
- Todo endpoint nuevo incluye JavaDoc de metodo.
- Toda regla de negocio nueva queda reflejada en `docs/architecture-decisions.md` cuando corresponde.
- No existen comentarios no profesionales ni decorativos.

