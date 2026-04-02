package manuel.tienda.cliente.service;


import manuel.tienda.cliente.dto.ClienteRequest;
import manuel.tienda.cliente.dto.ClienteResponse;
import manuel.tienda.cliente.entity.Cliente;
import manuel.tienda.cliente.mapper.ClienteMapper;
import manuel.tienda.cliente.repository.ClienteRepository;
import manuel.tienda.cliente.role.Role;
import manuel.tienda.exception.ClienteExisteException;
import manuel.tienda.exception.ClienteNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Servicio para gestionar operaciones relacionadas con clientes en el sistema de tienda online.
 * Proporciona funcionalidades CRUD (Crear, Leer, Actualizar, Eliminar) para entidades de Cliente,
 * incluyendo autenticación y validaciones de negocio.
 *
 * @author Manuel
 * @version 1.0
 * @since 2023
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    /**
     * Constructor para inyección de dependencias.
     *
     * @param clienteRepository Repositorio para acceder a datos de clientes.
     * @param passwordEncoder   Codificador para encriptar contraseñas.
     */
    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo cliente en el sistema.
     * Verifica que el username no exista previamente y encripta la contraseña antes de guardar.
     *
     * @param clienteRequest Datos del cliente a registrar (username y password).
     * @return ClienteResponse con los datos del cliente registrado (ID y username).
     * @throws ClienteExisteException Si el username ya está en uso.
     */
    @Transactional()
    public ClienteResponse registrar(ClienteRequest clienteRequest) {
        log.info("Intentando registrar cliente con username: {}", clienteRequest.getUsername());
        if (clienteRepository.existsByUsername(clienteRequest.getUsername())) {

            log.warn("Registro fallido: El username '{}' ya está en uso", clienteRequest.getUsername());
            throw new ClienteExisteException();
        }

        Cliente c = new Cliente(clienteRequest.getUsername(), passwordEncoder.encode(clienteRequest.getPassword()));
        c.setRole(Role.USER);
        clienteRepository.save(c);

        log.info("Cliente registrado exitosamente con ID: {} y username: {}", c.getId(), c.getUsername());

        return new ClienteResponse(c.getId(), c.getUsername(), c.getActivo());
    }

    /**
     * Encuentra todos los clientes con soporte para paginación y filtrado opcional por username y estado activo.
     * Si se proporciona un username, filtra clientes que contengan ese texto en el username.
     * Si se proporciona un estado activo, filtra clientes por ese estado.
     *
     * @param username Filtro opcional para buscar clientes por username (puede ser null o vacío).
     * @param pageable Información de paginación y ordenación.
     * @return Página de ClienteResponse con los clientes encontrados.
     */
    @Transactional(readOnly = true)
    public Page<ClienteResponse> findAll(String username, Pageable pageable) {

        log.info("Buscando clientes con filtro username: '{}', page: {}, size: {}",
                username, pageable.getPageNumber(), pageable.getPageSize());
        if (username != null && !username.isEmpty()) {
            // Filtro solo por username
            return clienteRepository.findByUsernameContaining(username, pageable)
                    .map(ClienteMapper::toResponse);

        } else {
            // Sin filtros
            log.debug("No se proporcionó filtro de username, buscando todos los clientes");

            return clienteRepository.findAll(pageable)
                    .map(ClienteMapper::toResponse);
        }
    }

    /**
     * Encuentra un cliente por su ID único.
     *
     * @param id ID del cliente a buscar.
     * @return ClienteResponse con los datos del cliente encontrado.
     * @throws ClienteNoEncontradoException Si no se encuentra un cliente con el ID proporcionado.
     */
    public ClienteResponse findById(Long id) {

        log.info("Buscando cliente con ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cliente no encontrado con id: {}", id);
                    return new ClienteNoEncontradoException();
                });

        log.info("Cliente encontrado: ID: {}, Username: {}", cliente.getId(), cliente.getUsername());

        return ClienteMapper.toResponse(cliente);
    }

    /**
     * Busca un cliente por su username.
     *
     * @param username Username del cliente a buscar.
     * @return ClienteResponse con los datos del cliente encontrado.
     * @throws ClienteNoEncontradoException Si no se encuentra un cliente con el username proporcionado.
     */
    public ClienteResponse findUserByUsername(String username) {

        log.info("Buscando cliente con username: '{}'", username);

        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow((() -> {

                    log.warn("Cliente no encontrado con username: '{}'", username);
                    return new ClienteNoEncontradoException();
                }));

        log.info("Cliente encontrado con username: '{}'", cliente.getUsername());

        return ClienteMapper.toResponse(cliente);
    }

    /**
     * Actualiza los datos de un cliente existente.
     * Permite actualizar username (si no está en uso) y/o contraseña.
     *
     * @param id             ID del cliente a actualizar.
     * @param clienteRequest Datos a actualizar (username y/o password).
     * @return ClienteResponse con los datos actualizados del cliente.
     * @throws ClienteNoEncontradoException Si no se encuentra el cliente con el ID proporcionado.
     * @throws ClienteExisteException       Si el nuevo username ya está en uso por otro cliente.
     */
    @Transactional
    public ClienteResponse update(Long id, ClienteRequest clienteRequest) {

        log.info("Intentando actualizar cliente con ID: {}", id);
        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNoEncontradoException::new);

        // Actualizar username
        if (clienteRequest.getUsername() != null && !clienteRequest.getUsername().isBlank()) {

            log.info("Actualizando username para cliente ID: {}, nuevo username: '{}'", id, clienteRequest.getUsername());
            String nuevoUsername = clienteRequest.getUsername();

            if (!cliente.getUsername().equals(nuevoUsername)) {
                log.debug("El nuevo username '{}' es diferente al actual para cliente ID: {}", nuevoUsername, id);
                boolean existe = clienteRepository.existsByUsername(nuevoUsername);

                if (existe) {

                    log.warn("Actualización fallida: El username '{}' ya está en uso por otro cliente", nuevoUsername);

                    throw new ClienteExisteException();
                }
                log.info("Username actualizado exitosamente para cliente ID: {}, nuevo username: '{}'", id, nuevoUsername);
                cliente.updateUsername(nuevoUsername);
            }
        }

        // Actualizar password
        log.debug("Verificando si se debe actualizar la contraseña para cliente ID: {}", id);

        if (clienteRequest.getPassword() != null && !clienteRequest.getPassword().isBlank()) {

            log.info("Actualizando contraseña para cliente ID: {}", id);

            String passwordEncriptado = passwordEncoder.encode(clienteRequest.getPassword());
            cliente.updatePasswordHash(passwordEncriptado);

            log.info("Contraseña actualizada exitosamente para cliente ID: {}", id);
        }

        clienteRepository.save(cliente);
        log.info("Cliente actualizado exitosamente: ID: {}, Username: {}", cliente.getId(), cliente.getUsername());
        return ClienteMapper.toResponse(cliente);
    }

    /**
     * Elimina un cliente del sistema por su ID.
     *
     * @param id ID del cliente a eliminar.
     * @throws ClienteNoEncontradoException Si no se encuentra el cliente con el ID proporcionado.
     */
    @Transactional
    public void delete(Long id) {
        log.info("Intentando eliminar cliente con ID: {}", id);
        Cliente clienteEncontrado = clienteRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Cliente no encontrado con id: {}", id);

                    return new ClienteNoEncontradoException();
                });

        clienteRepository.delete(clienteEncontrado);
        log.info("Cliente eliminado exitosamente con ID: {}", id);
    }

    /**
     * Cambia el estado activo de un cliente.
     *
     * @param id     ID del cliente a modificar.
     * @param activo nuevo estado del cliente.
     * @return ClienteResponse con los datos actualizados del cliente.
     * @throws ClienteNoEncontradoException Si no se encuentra el cliente con el ID proporcionado.
     */
    @Transactional
    public ClienteResponse cambiarEstado(Long id, Boolean activo) {

        log.info("Cambiando estado activo para cliente ID: {}, nuevo estado: {}", id, activo);

        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> {

            log.warn("Cliente no encontrado con id: {}", id);

            return new ClienteNoEncontradoException();
        });

        cliente.updateActivo(activo);

        clienteRepository.save(cliente);

        log.info("Estado del cliente ID: {} actualizado a: {}", id, activo);

        return ClienteMapper.toResponse(cliente);
    }
}
