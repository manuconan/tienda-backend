package manuel.tienda.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Representa una respuesta de error estándar para la API.
 * <p>
 * Proporciona una estructura consistente para todos los errores,
 * facilitando su consumo por parte del cliente y su análisis en logs.
 * <p>
 * Incluye:
 * - Timestamp del error
 * - Estado HTTP
 * - Código interno de error
 * - Mensaje descriptivo
 * - Ruta de la petición
 */
@Getter
@ToString
public class ApiError {

    /**
     * Fecha y hora en la que se produce el error.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    /**
     * Estado HTTP asociado al error.
     * Se mantiene como HttpStatus internamente para mayor semántica.
     */
    private final HttpStatus status;

    /**
     * Código interno de error.
     * Permite identificar de forma única el tipo de error
     * independientemente del mensaje.
     * <p>
     * Ejemplo: USER_NOT_FOUND, VALIDATION_ERROR
     */
    private final String code;

    /**
     * Mensaje descriptivo del error.
     */
    private final String message;

    /**
     * Ruta de la petición que originó el error.
     */
    private final String path;

    /**
     * Constructor de la respuesta de error.
     *
     * @param status  estado HTTP
     * @param code    código interno de error
     * @param message mensaje descriptivo
     * @param path    endpoint donde ocurrió el error
     */
    public ApiError(HttpStatus status, String code, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
    }

    /**
     * Devuelve el código HTTP numérico (ej: 404, 500).
     * <p>
     * Se utiliza en la serialización JSON para facilitar
     * el consumo por el cliente.
     */
    public int getStatus() {
        return status.value();
    }

    /**
     * Devuelve la descripción estándar del estado HTTP
     * (ej: "Not Found", "Bad Request").
     */
    public String getError() {
        return status.getReasonPhrase();
    }
}