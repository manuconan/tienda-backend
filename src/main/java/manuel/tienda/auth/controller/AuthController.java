package manuel.tienda.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import manuel.tienda.auth.dto.LoginRequest;
import manuel.tienda.auth.dto.LoginResponse;
import manuel.tienda.auth.service.AuthService;
import manuel.tienda.cliente.entity.Cliente;
import manuel.tienda.cliente.repository.ClienteRepository;
import manuel.tienda.exception.ClienteNoEncontradoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
         @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }


}

