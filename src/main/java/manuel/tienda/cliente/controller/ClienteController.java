package manuel.tienda.cliente.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import manuel.tienda.cliente.dto.ClienteRequest;
import manuel.tienda.cliente.dto.ClienteResponse;
import manuel.tienda.cliente.service.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {

        this.clienteService = clienteService;
    }

    /**
     * Registrar nuevo cliente
     */
    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(
            @Valid @RequestBody ClienteRequest request) {

        ClienteResponse cliente = clienteService.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    /**
     * Obtener cliente por id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable @Min(1) Long id) {

        ClienteResponse cliente = clienteService.findById(id);

        return ResponseEntity.ok(cliente);
    }

    /**
     * Listar todos los clientes
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listar(@RequestParam(required = false) @Size(min = 4, max = 15) String username, Pageable pageable) {

        Page<ClienteResponse> clientes = clienteService.findAll(username, pageable);

        return ResponseEntity.ok(clientes);
    }

    /**
     * Eliminar cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Min(1) Long id) {

        clienteService.delete(id);

        return ResponseEntity.noContent().build();
    }
}