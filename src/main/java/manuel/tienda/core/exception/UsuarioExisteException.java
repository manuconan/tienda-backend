package manuel.tienda.core.exception;

public class UsuarioExisteException extends RuntimeException {
    public UsuarioExisteException() {
        super("El usuario ya existe");
    }
}
