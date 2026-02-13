package manuel.tienda.core.exception;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String username) {
        super("Usuario "+ username+" no encontrado");
    }

}
