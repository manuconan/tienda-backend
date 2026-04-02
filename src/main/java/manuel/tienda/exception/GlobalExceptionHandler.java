package manuel.tienda.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
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
     * <p>
     * Nivel WARN: un 404 es esperable, pero su registro permite detectar
     * patrones de IDs inválidos o bugs en el cliente que consumen la API.
     */
    @ExceptionHandler({
            ProductoNoEncontradoException.class,
            ClienteNoEncontradoException.class
    })
    public ResponseEntity<ApiError> handleNotFound(
            Exception ex,
            HttpServletRequest request) {

        log.warn("[{} {}] - Recurso no encontrado: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        return buildError(ex, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", request);
    }

    /**
     * Maneja errores de negocio o peticiones inválidas (HTTP 400).
     * <p>
     * Nivel WARN: petición rechazada por reglas de negocio (stock insuficiente,
     * producto inválido). No es un error crítico del sistema, pero es útil
     * monitorizarlo para detectar mal uso de la API.
     */
    @ExceptionHandler({
            ProductoInvalidoException.class,
            InsufficientStockException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(
            Exception ex,
            HttpServletRequest request) {

        log.warn("[{} {}] - Petición de negocio inválida: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        return buildError(ex, HttpStatus.BAD_REQUEST, "BAD_REQUEST", request);
    }

    /**
     * Maneja errores de validación de Bean Validation (@Valid).
     * <p>
     * Nivel WARN: los errores de campo son responsabilidad del cliente.
     * Se registra el detalle de los campos fallidos sin stack trace,
     * ya que no representan un fallo interno del sistema.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("[{} {}] - Error de validación: {}",
                request.getMethod(), request.getRequestURI(), message);

        return buildError(
                new RuntimeException(message),
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                request
        );
    }

    /**
     * Maneja conflictos de estado del recurso (HTTP 409).
     * <p>
     * Nivel WARN: indica intentos de crear recursos duplicados.
     * Frecuente en reintentos del cliente o bugs del frontend.
     */
    @ExceptionHandler({
            ClienteExisteException.class,
            ClienteYaExisteException.class
    })
    public ResponseEntity<ApiError> handleConflict(
            Exception ex,
            HttpServletRequest request) {

        log.warn("[{} {}] - Conflicto de recurso: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        return buildError(ex, HttpStatus.CONFLICT, "RESOURCE_CONFLICT", request);
    }

    /**
     * Maneja argumentos inválidos en llamadas a métodos (HTTP 400).
     * <p>
     * Nivel WARN: inusual en flujo normal; su aparición puede indicar
     * un bug en el cliente o en la lógica de transformación de datos.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("[{} {}] - Argumento inválido: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        return buildError(ex, HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", request);
    }

    /**
     * Maneja body JSON mal formado o no parseable (HTTP 400).
     * <p>
     * Nivel WARN: se omite el mensaje de la excepción intencionadamente,
     * ya que puede contener fragmentos del body original con datos sensibles
     * (contraseñas, tokens) que no deben aparecer en los ficheros de log.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("[{} {}] - JSON malformado o no legible en la petición",
                request.getMethod(), request.getRequestURI());

        return buildError(ex, HttpStatus.BAD_REQUEST, "MALFORMED_JSON", request);
    }

    /**
     * Maneja credenciales de login inválidas (HTTP 401).
     * <p>
     * Nivel WARN: se omite la excepción completa deliberadamente por seguridad.
     * Loguear el mensaje o stack trace de {@link BadCredentialsException} puede
     * revelar detalles internos del proveedor de autenticación y facilitar
     * ataques de enumeración o fuerza bruta. Solo se registra la URI.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        log.warn("[{} {}] - Intento de autenticación fallido",
                request.getMethod(), request.getRequestURI());

        return buildError(ex, HttpStatus.UNAUTHORIZED, "AUTH_INVALID_CREDENTIALS", request);
    }

    /**
     * Manejador genérico de errores no controlados (HTTP 500).
     * <p>
     * Nivel ERROR con stack trace completo: estos errores son inesperados
     * y requieren diagnóstico inmediato. Se incluye método, URI y la excepción
     * completa para que herramientas como Kibana o Splunk puedan correlacionar
     * la traza con el contexto de la petición que lo originó.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("[{} {}] - Error inesperado: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        return buildError(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                request
        );
    }
}