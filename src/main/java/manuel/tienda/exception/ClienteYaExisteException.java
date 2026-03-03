package manuel.tienda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class ClienteYaExisteException extends RuntimeException {
public ClienteYaExisteException(String mensaje){
    super(mensaje);
}
}
