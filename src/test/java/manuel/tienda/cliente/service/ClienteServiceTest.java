package manuel.tienda.cliente.service;


import manuel.tienda.cliente.dto.ClienteRequest;
import manuel.tienda.cliente.entity.Cliente;
import manuel.tienda.cliente.repository.ClienteRepository;
import manuel.tienda.exception.ClienteNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    // -------------------------------
    // TEST 1 → Cliente no existe
    // -------------------------------
    @Test
    void update_shouldThrowException_whenClienteNotExists() {

        Long id = 1L;

        ClienteRequest request = new ClienteRequest();
        request.setUsername("nuevoUsername");
        request.setPassword("password123");

        when(clienteRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class, () -> clienteService.update(id, request));

        verify(clienteRepository).findById(id);
    }

    // -----------------------------------------------------
    // TEST 2 → Actualiza username correctamente
    // -----------------------------------------------------
    @Test
    void update_shouldUpdateUsername_whenValidAndNotDuplicated() {

        Long id = 1L;


        Cliente cliente = new Cliente("viejoUsermane", "passwordHash");


        ClienteRequest request = new ClienteRequest();
        request.setUsername("nuevoUsername");
        request.setPassword("password123");

        when(clienteRepository.findById(id))
                .thenReturn(Optional.of(cliente));

        when(clienteRepository.existsByUsername("nuevoUsername"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoderPassword");

        when(clienteRepository.save(cliente))
                .thenReturn(cliente);

        clienteService.update(id, request);

        assertEquals("nuevoUsername", cliente.getUsername());

        verify(clienteRepository).findById(id);
        verify(clienteRepository).existsByUsername("nuevoUsername");
        verify(clienteRepository).save(cliente);
    }
}
