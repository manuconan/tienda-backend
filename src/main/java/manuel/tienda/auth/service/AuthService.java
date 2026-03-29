package manuel.tienda.auth.service;

import lombok.RequiredArgsConstructor;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        Cliente cliente = clienteRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.getPassword(), cliente.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(cliente);

        return new LoginResponse(token);
    }
}
