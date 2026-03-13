// src/main/java/manuel/tienda/exception/ApiError.java
package manuel.tienda.exception;

import java.time.LocalDateTime;

/**
 * DTO que representa la estructura estándar de error que devuelve la API.
 *
 * Se utiliza junto con GlobalExceptionHandler para enviar respuestas
 * consistentes cuando ocurre una excepción en la aplicación.
 *
 * El objeto es inmutable: una vez creado no puede modificarse.
 * Esto garantiza que el error refleje exactamente el estado en el
 * momento en que ocurrió la excepción.
 */
public class ApiError {

    /** Momento exacto en el que se generó el error */
    private final LocalDateTime timestamp;

    /** Código HTTP del error (ej: 404, 400, 500) */
    private final int status;

    /** Nombre estándar del error HTTP (ej: "Not Found") */
    private final String error;

    /** Mensaje descriptivo de la excepción */
    private final String message;

    /** Endpoint de la petición que provocó el error */
    private final String path;

    /**
     * Crea una nueva instancia de ApiError.
     *
     * El timestamp se genera automáticamente para asegurar que
     * todos los errores registren el momento exacto en que ocurrieron,
     * evitando depender de componentes externos para esta información.
     *
     * @param status  código HTTP del error
     * @param error   descripción estándar del error HTTP
     * @param message mensaje específico de la excepción
     * @param path    ruta del endpoint que originó el error
     */
    public ApiError(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * @return instante en que se generó el error
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @return código HTTP del error
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return nombre estándar del error HTTP
     */
    public String getError() {
        return error;
    }

    /**
     * @return mensaje detallado del error
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return endpoint donde ocurrió la excepción
     */
    public String getPath() {
        return path;
    }
}