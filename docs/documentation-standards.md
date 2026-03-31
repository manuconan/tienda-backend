# Estandares de Documentacion

## 1. Objetivo

Establecer normas de documentacion tecnica para mantener consistencia, trazabilidad y calidad editorial en todo el repositorio.

## 2. Alcance

Aplicable a:

- Archivos Markdown del repositorio.
- JavaDoc en codigo fuente de `src/main/java`.
- Descripciones de Pull Request relacionadas con cambios funcionales.

## 3. Normas para JavaDoc

Obligatorio en:

- Clases publicas de `controller`, `service`, `exception`, `config`.
- Metodos publicos expuestos por API o reglas de negocio.

Recomendado en:

- Metodos privados con logica no trivial.
- Mappers cuando exista transformacion con reglas.

Contenido minimo:

1. Responsabilidad funcional.
2. Parametros relevantes (`@param`).
3. Resultado (`@return`) cuando aplique.
4. Excepciones de negocio (`@throws`) cuando corresponda.

## 4. Normas para Markdown

- Redaccion formal y tecnica.
- Sin iconos, dibujos, emojis ni contenido decorativo.
- Estructura estable por secciones (objetivo, alcance, detalles, referencias).
- Rutas y simbolos de codigo en backticks.
- No incluir secretos, credenciales reales ni datos sensibles.

## 5. Estilo editorial

- Idioma principal: espanol tecnico.
- Frases directas y verificables.
- Evitar ambiguedad y opinion sin respaldo tecnico.
- Mantener terminologia consistente entre documentos.

## 6. Trazabilidad obligatoria

Cada documento funcional debe enlazar al menos una referencia concreta a codigo fuente, por ejemplo:

- `src/main/java/manuel/tienda/cliente/service/ClienteService.java`
- `src/main/java/manuel/tienda/exception/GlobalExceptionHandler.java`

## 7. Criterios de aceptacion en PR

- Se actualiza documentacion si cambia comportamiento funcional.
- Se actualiza ADR si la decision altera arquitectura o seguridad.
- Se actualiza `docs/class-catalog.md` si cambian clases/paquetes.
- No se aprueban PR con documentacion desalineada respecto al codigo.

## 8. Plantilla minima para nuevo documento

```markdown
# Titulo

## 1. Objetivo
## 2. Alcance
## 3. Detalle tecnico
## 4. Referencias de codigo
```

