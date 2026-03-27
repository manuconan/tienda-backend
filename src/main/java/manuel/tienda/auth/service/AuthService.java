package manuel.tienda.auth.service;

import lombok.RequiredArgsConstructor;
import manuel.tienda.Usuario.entity.Usuario;
import manuel.tienda.Usuario.repository.UsuarioRepository;
import manuel.tienda.auth.dto.LoginRequest;
import manuel.tienda.auth.dto.LoginResponse;
import manuel.tienda.cliente.entity.Cliente;
import manuel.tienda.cliente.repository.ClienteRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername()).orElse(null);

        if (usuario != null) {
            if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
                throw new BadCredentialsException("Credenciales incorrectas");
            }

            return new LoginResponse(jwtService.generateToken(usuario.getUsername()));
        }

        Cliente cliente = clienteRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.getPassword(), cliente.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(cliente.getUsername());

        return new LoginResponse(token);
    }
}
