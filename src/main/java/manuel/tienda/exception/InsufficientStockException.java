package manuel.tienda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {

        super("No tenemos stock suficiente, lo sentimos...");
    }


}
