package manuel.tienda.Usuario.service;

import manuel.tienda.Usuario.entity.Usuario;
import manuel.tienda.Usuario.repository.UsuarioRepository;
import manuel.tienda.exception.ClienteExisteException;
import manuel.tienda.exception.ClienteNoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de la lógica de negocio relacionada con usuarios.
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Devuelve todos los usuarios del sistema con paginación.
     */
    @Transactional(readOnly = true)
    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    /**
     * Registra un nuevo usuario validando duplicados y encriptando contraseña.
     */
    public Usuario create(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new ClienteExisteException();
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setEnabled(true);

        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por username.
     */
    public Usuario findByUsername(String username) {

        return usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ClienteNoEncontradoException());
    }
}
