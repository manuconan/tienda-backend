package manuel.tienda.exception;

public class ClienteNoEncontradoException extends RuntimeException {
    public ClienteNoEncontradoException() {
        super("Usuario no encontrado");
    }

}
