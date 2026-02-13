package manuel.tienda.core.auth.service;

import manuel.tienda.core.auth.entity.Usuario;
import manuel.tienda.core.auth.repository.UsuarioRepository;
import manuel.tienda.core.exception.UsuarioExisteException;
import manuel.tienda.core.exception.UsuarioNoEncontradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * Devuelve todos los usuarios del sistema.
     */
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }


    /**
     * Registra un nuevo usuario validando duplicados y encriptando contraseña.
     */
    public Usuario create(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new UsuarioExisteException();
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
                        new UsuarioNoEncontradoException(
                                "Usuario " + username + " no encontrado"));
    }
}
