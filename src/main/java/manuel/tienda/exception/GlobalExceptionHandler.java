package manuel.tienda.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones de la aplicación.
 * <p>
 * Centraliza el tratamiento de errores para:
 * - Garantizar respuestas homogéneas (ApiError)
 * - Mapear excepciones a códigos HTTP adecuados
 * - Evitar duplicación en controladores
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Construye una respuesta de error estándar.
     *
     * @param ex      excepción capturada
     * @param status  estado HTTP asociado
     * @param code    código interno de error
     * @param request petición HTTP original
     * @return respuesta estructurada con ApiError
     */
    private ResponseEntity<ApiError> buildError(
            Exception ex,
            HttpStatus status,
            String code,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                status,
                code,
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja recursos inexistentes (HTTP 404).
     */
    @ExceptionHandler({
            ProductoNoEncontradoException.class,
            ClienteNoEncontradoException.class
    })
    public ResponseEntity<ApiError> handleNotFound(
            Exception ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", request);
    }

    /**
     * Maneja errores de negocio o peticiones inválidas (HTTP 400).
     */
    @ExceptionHandler({
            ProductoInvalidoException.class,
            InsufficientStockException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(
            Exception ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.BAD_REQUEST, "BAD_REQUEST", request);
    }

    /**
     * Maneja errores de validación de Bean Validation (@Valid).
     * <p>
     * Se agregan todos los errores de campo en un único mensaje.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildError(
                new RuntimeException(message),
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                request
        );
    }

    /**
     * Maneja conflictos de estado del recurso (HTTP 409).
     */
    @ExceptionHandler({
            ClienteExisteException.class,
            ClienteYaExisteException.class
    })
    public ResponseEntity<ApiError> handleConflict(
            Exception ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.CONFLICT, "RESOURCE_CONFLICT", request);
    }

    /**
     * Maneja argumentos inválidos en llamadas a métodos (HTTP 400).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", request);
    }

    /**
     * Manejador genérico de errores no controlados (HTTP 500).
     * <p>
     * Registra el error para diagnóstico y evita exponer detalles internos.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error", ex);

        return buildError(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                request
        );
    }
}