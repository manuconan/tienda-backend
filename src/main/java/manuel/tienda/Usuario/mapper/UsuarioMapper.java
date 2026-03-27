package manuel.tienda.Usuario.mapper;

import manuel.tienda.Usuario.dto.UsuarioRequest;
import manuel.tienda.Usuario.dto.UsuarioResponse;
import manuel.tienda.Usuario.entity.Usuario;
public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequest usuarioRequest) {
        Usuario u = new Usuario();
        u.setUsername(usuarioRequest.getUsername());
        u.setPassword(usuarioRequest.getPassword());
        return u;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(usuario.getId());
        r.setUsername(usuario.getUsername());
        return r;
    }
}

