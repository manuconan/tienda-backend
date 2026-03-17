package manuel.tienda.cliente.controller;

import jakarta.validation.Valid;
import manuel.tienda.cliente.dto.ClienteRequest;
import manuel.tienda.cliente.dto.ClienteResponse;
import manuel.tienda.cliente.service.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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

        return ResponseEntity.ok(cliente);
    }

    /**
     * Obtener cliente por id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Long id) {

        ClienteResponse cliente = clienteService.findById(id);

        return ResponseEntity.ok(cliente);
    }

    /**
     * Listar todos los clientes
     */
    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listar(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ClienteResponse> clientes = clienteService.findAll(pageable);

        return ResponseEntity.ok(clientes);
    }

    /**
     * Eliminar cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        clienteService.delete(id);

        return ResponseEntity.noContent().build();
    }
}