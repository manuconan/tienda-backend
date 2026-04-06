package manuel.tienda.cliente.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import manuel.tienda.cliente.dto.ClienteRequest;
import manuel.tienda.cliente.dto.ClienteResponse;
import manuel.tienda.cliente.service.ClienteService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de clientes en el sistema de tienda online.
 *
 * <p>Expone los endpoints HTTP del recurso {@code /clientes} y delega la lógica
 * de negocio en {@link ClienteService}. Gestiona el ciclo de vida completo de un
 * cliente: registro, consulta individual, listado paginado con filtros, actualización,
 * cambio de estado activo y eliminación.</p>
 *
 * <p>El listado de clientes requiere autenticación con rol {@code USER} o {@code ADMIN}
 * y admite filtrado dinámico por {@code username} (parcial) y/o {@code activo},
 * además de paginación y ordenación mediante los parámetros estándar de Spring Data.</p>
 *
 * @author Manuel
 * @version 1.2
 * @since 2023
 */
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param clienteService Servicio que contiene la lógica de negocio de clientes.
     */
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ──────────────────────────────────────────────────────────────────
    // POST /clientes — Registro
    // ──────────────────────────────────────────────────────────────────

    /**
     * Registra un nuevo cliente en el sistema.
     *
     * <p>Valida los datos de entrada mediante Bean Validation antes de delegar
     * en el servicio. Si el username ya existe se devuelve {@code 409 Conflict}.</p>
     *
     * @param request Datos del cliente a registrar. Debe contener {@code username} y {@code password}
     *                válidos según las restricciones definidas en {@link ClienteRequest}.
     * @return {@code 201 Created} con el {@link ClienteResponse} del cliente registrado.
     */
    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(
            @Valid @RequestBody ClienteRequest request) {

        ClienteResponse cliente = clienteService.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /clientes — Listado con filtros, paginación y ordenación
    // ──────────────────────────────────────────────────────────────────

    /**
     * Lista todos los clientes con soporte para filtrado dinámico, paginación y ordenación.
     *
     * <p>Requiere autenticación con rol {@code USER} o {@code ADMIN}.</p>
     *
     * <p>Los filtros son opcionales e independientes entre sí; se pueden combinar libremente.
     * La paginación y la ordenación se controlan mediante los parámetros estándar de Spring Data
     * ({@code page}, {@code size}, {@code sort}).</p>
     *
     * <p>Ejemplos de uso:</p>
     * <ul>
     *   <li>{@code GET /clientes}                               - todos los clientes paginados</li>
     *   <li>{@code GET /clientes?username=man}                  - clientes cuyo username contiene "man"</li>
     *   <li>{@code GET /clientes?activo=true}                   - solo clientes activos</li>
     *   <li>{@code GET /clientes?username=man&activo=true}      - combinación de ambos filtros</li>
     *   <li>{@code GET /clientes?page=0&size=5&sort=username,asc} - paginación y ordenación</li>
     * </ul>
     *
     * @param username Filtro parcial por nombre de usuario (opcional, 2-50 caracteres).
     * @param activo   Filtro por estado activo del cliente (opcional).
     *                 {@code true} devuelve solo clientes activos; {@code false}, solo inactivos.
     * @param pageable Configuración de paginación y ordenación. Por defecto: 10 elementos
     *                 por página, ordenados por {@code id} ascendente.
     * @return {@code 200 OK} con una página de {@link ClienteResponse} que cumplen los criterios.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> listar(
            @RequestParam(required = false) @Size(min = 2, max = 50) String username,
            @RequestParam(required = false) Boolean activo,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<ClienteResponse> clientes = clienteService.findAll(username, activo, pageable);

        return ResponseEntity.ok(clientes);
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /clientes/{id} — Consulta por ID
    // ──────────────────────────────────────────────────────────────────

    /**
     * Obtiene un cliente por su identificador único.
     *
     * <p>El parámetro {@code id} debe ser un entero positivo mayor o igual a 1.
     * Si no existe un cliente con ese ID se devuelve {@code 404 Not Found}.</p>
     *
     * @param id Identificador único del cliente. Debe ser mayor o igual a 1.
     * @return {@code 200 OK} con el {@link ClienteResponse} del cliente encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable @Min(1) Long id) {

        ClienteResponse cliente = clienteService.findById(id);

        return ResponseEntity.ok(cliente);
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /clientes/username/{username} — Consulta por username
    // ──────────────────────────────────────────────────────────────────

    /**
     * Obtiene un cliente por su nombre de usuario exacto.
     *
     * <p>Útil para búsquedas directas cuando se conoce el username completo,
     * por ejemplo desde el sistema de autenticación.</p>
     *
     * @param username Nombre de usuario exacto del cliente a buscar.
     * @return {@code 200 OK} con el {@link ClienteResponse} del cliente encontrado.
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ClienteResponse> findByUsername(@PathVariable String username) {

        ClienteResponse cliente = clienteService.findUserByUsername(username);

        return ResponseEntity.ok(cliente);
    }

    // ──────────────────────────────────────────────────────────────────
    // PUT /clientes/{id} — Actualización completa
    // ──────────────────────────────────────────────────────────────────

    /**
     * Actualiza los datos de un cliente existente.
     *
     * <p>Permite actualizar el {@code username} (si no está ya en uso por otro cliente)
     * y/o la {@code password}. Solo se actualizan los campos presentes y no vacíos
     * en el cuerpo de la petición.</p>
     *
     * @param id      Identificador único del cliente a actualizar. Debe ser mayor o igual a 1.
     * @param request Datos a actualizar ({@code username} y/o {@code password}).
     * @return {@code 200 OK} con el {@link ClienteResponse} del cliente actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> update(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody ClienteRequest request) {

        ClienteResponse cliente = clienteService.update(id, request);

        return ResponseEntity.ok(cliente);
    }

    // ──────────────────────────────────────────────────────────────────
    // PATCH /clientes/{id}/estado — Cambio de estado activo
    // ──────────────────────────────────────────────────────────────────

    /**
     * Cambia el estado activo de un cliente sin modificar el resto de sus datos.
     *
     * <p>Uso recomendado para activar o desactivar un cliente de forma puntual,
     * evitando enviar todo el cuerpo del recurso como requeriría un {@code PUT}.</p>
     *
     * @param id     Identificador único del cliente. Debe ser mayor o igual a 1.
     * @param activo Nuevo estado activo del cliente ({@code true} para activar,
     *               {@code false} para desactivar).
     * @return {@code 200 OK} con el {@link ClienteResponse} del cliente actualizado.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ClienteResponse> cambiarEstado(
            @PathVariable @Min(1) Long id,
            @RequestParam Boolean activo) {

        ClienteResponse cliente = clienteService.cambiarEstado(id, activo);

        return ResponseEntity.ok(cliente);
    }

    // ──────────────────────────────────────────────────────────────────
    // DELETE /clientes/{id} — Eliminación
    // ──────────────────────────────────────────────────────────────────

    /**
     * Elimina un cliente del sistema por su identificador único.
     *
     * <p>El parámetro {@code id} debe ser un entero positivo mayor o igual a 1.
     * Si no existe un cliente con ese ID se devuelve {@code 404 Not Found}.</p>
     *
     * @param id Identificador único del cliente a eliminar. Debe ser mayor o igual a 1.
     * @return {@code 204 No Content} si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Min(1) Long id) {

        clienteService.delete(id);

        return ResponseEntity.noContent().build();
    }
}