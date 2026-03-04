package manuel.tienda.core.cliente.service;



import manuel.tienda.cliente.dto.ClienteRequest;
import manuel.tienda.cliente.repository.ClienteRepository;
import manuel.tienda.cliente.service.ClienteService;
import manuel.tienda.core.cliente.Cliente;
import manuel.tienda.exception.ClienteNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    // -------------------------------
    // TEST 1 → Cliente no existe
    // -------------------------------
    @Test
    void update_shouldThrowException_whenClienteNotExists() {

        Long id = 1L;

        when(clienteRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ClienteNoEncontradoException.class,
                () -> clienteService.update(id, new ClienteRequest("nuevoUsername"))
        );

        verify(clienteRepository).findById(id);
    }

    // -----------------------------------------------------
    // TEST 2 → Actualiza username correctamente
    // -----------------------------------------------------
    @Test
    void update_shouldUpdateUsername_whenValidAndNotDuplicated() {

        Long id = 1L;

        Cliente cliente = new Cliente();
        cliente.setUsername("viejoUsername");

        ClienteRequest request = new ClienteRequest("nuevoUsername");

        when(clienteRepository.findById(id))
                .thenReturn(Optional.of(cliente));

        when(clienteRepository.existsByUsername("nuevoUsername"))
                .thenReturn(false);

        when(clienteRepository.save(cliente))
                .thenReturn(cliente);

        clienteService.update(id, request);

        assertEquals("nuevoUsername", cliente.getUsername());

        verify(clienteRepository).findById(id);
        verify(clienteRepository).existsByUsername("nuevoUsername");
        verify(clienteRepository).save(cliente);
    }
}
