package manuel.tienda.Usuario.controller;

import jakarta.validation.Valid;
import manuel.tienda.Usuario.dto.UsuarioRequest;
import manuel.tienda.Usuario.dto.UsuarioResponse;
import manuel.tienda.Usuario.entity.Usuario;
import manuel.tienda.Usuario.mapper.UsuarioMapper;
import manuel.tienda.Usuario.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Controlador REST para la gestión de usuarios.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene todos los usuarios.
     */
    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> findAll(@PageableDefault(sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<UsuarioResponse> usuarios = usuarioService.findAll(pageable)
                .map(UsuarioMapper::toResponse);

        return ResponseEntity.ok(usuarios);
    }

    /**
     * Registra un nuevo usuario.
     */
    @PostMapping
    public ResponseEntity<UsuarioResponse> create(
            @Valid @RequestBody UsuarioRequest usuarioRequest) {

        Usuario usuario = UsuarioMapper.toEntity(usuarioRequest);

        Usuario registrado = usuarioService.create(usuario);

        UsuarioResponse response = UsuarioMapper.toResponse(registrado);

        return ResponseEntity
                .created(URI.create("/usuarios/" + registrado.getUsername()))
                .body(response);
    }

    /**
     * Busca un usuario por username.
     */
    @GetMapping("/{username}")
    public ResponseEntity<UsuarioResponse> findByUsername(
            @PathVariable String username) {

        Usuario usuario = usuarioService.findByUsername(username);

        UsuarioResponse response = UsuarioMapper.toResponse(usuario);

        return ResponseEntity.ok(response);
    }
}
