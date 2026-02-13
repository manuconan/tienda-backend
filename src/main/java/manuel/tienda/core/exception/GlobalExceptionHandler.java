package manuel.tienda.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ApiError> handleProductoNoEncontrado(
            ProductoNoEncontradoException ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ApiError> handleUsuarioNoEncontrado(
            UsuarioNoEncontradoException ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({
            ProductoInvalidoException.class,
            UsuarioExisteException.class,
            InsufficientStockException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return buildError(
                new RuntimeException(message),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return buildError(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
