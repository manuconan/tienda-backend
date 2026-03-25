// src/main/java/manuel/tienda/exception/GlobalExceptionHandler.java
package manuel.tienda.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maneja de forma centralizada todas las excepciones lanzadas por la aplicación.
 * <p>
 * Objetivos:
 * - Evitar duplicación de código en controladores.
 * - Garantizar un formato de error consistente (ApiError).
 * - Mapear excepciones de dominio a códigos HTTP apropiados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Fábrica central de respuestas de error.
     * <p>
     * Centralizar la creación de ApiError evita inconsistencias si el formato
     * de error cambia en el futuro (p.ej., añadir errorCode o detalles).
     *
     * @param ex      excepción capturada
     * @param status  estado HTTP que se devolverá al cliente
     * @param request request HTTP original para obtener el endpoint
     * @return ResponseEntity con el ApiError serializado a JSON
     */
    private ResponseEntity<ApiError> buildError(
            Exception ex,
            HttpStatus status,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja recursos que no existen.
     * <p>
     * Agrupar excepciones con la misma semántica HTTP (404) reduce
     * duplicación y facilita mantener el handler cuando el proyecto crece.
     */
    @ExceptionHandler({
            ProductoNoEncontradoException.class,
            ClienteNoEncontradoException.class
    })
    public ResponseEntity<ApiError> handleNotFound(
            RuntimeException ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.NOT_FOUND, request);
    }

    /**
     * Maneja errores de validación de reglas de negocio.
     * <p>
     * Estas excepciones indican que la petición es incorrecta o
     * no cumple las reglas del dominio (HTTP 400).
     */
    @ExceptionHandler({
            ProductoInvalidoException.class,
            ClienteExisteException.class,
            InsufficientStockException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Maneja errores producidos por validaciones de Bean Validation (@Valid).
     * <p>
     * Se extrae el primer mensaje de error para devolver una respuesta
     * simple al cliente. En APIs más complejas se suele devolver una lista
     * completa de errores de validación.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Error de validación");
        return
                buildError(new RuntimeException(message),
                        HttpStatus.BAD_REQUEST, request);
    }


    /**
     * Maneja conflictos de estado del recurso (HTTP 409).
     * <p>
     * Ejemplo típico: intentar crear un cliente que ya existe.
     */
    @ExceptionHandler(ClienteYaExisteException.class)
    public ResponseEntity<ApiError> handleConflict(
            ClienteYaExisteException ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.CONFLICT, request);
    }

    /**
     * Maneja errores de argumentos inválidos.
     * <p>
     * IllegalArgumentException se lanza cuando un argumento no cumple con
     * las precondiciones de una función (ej: username vacío, valores nulos).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Fallback de seguridad.
     * <p>
     * Captura cualquier excepción no manejada previamente para evitar
     * exponer detalles internos de la aplicación al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}