package manuel.tienda.core.auth.mapper;

import manuel.tienda.core.auth.dto.UsuarioRequest;
import manuel.tienda.core.auth.dto.UsuarioResponse;
import manuel.tienda.core.auth.entity.Usuario;
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

