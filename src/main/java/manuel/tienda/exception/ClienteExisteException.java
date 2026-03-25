package manuel.tienda.exception;

public class ClienteExisteException extends RuntimeException {

    public ClienteExisteException() {

        super("El usuario ya existe");
    }
}
