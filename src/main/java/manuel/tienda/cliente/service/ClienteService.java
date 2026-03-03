package manuel.tienda.cliente.service;


import manuel.tienda.cliente.dto.ClienteRequest;
import manuel.tienda.cliente.dto.ClienteResponse;
import manuel.tienda.cliente.entity.Cliente;
import manuel.tienda.cliente.mapper.ClienteMapper;
import manuel.tienda.cliente.repository.ClienteRepository;
import manuel.tienda.exception.ClienteExisteException;
import manuel.tienda.exception.ClienteNoEncontradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;

    }

    //Registrar un cliente
    @Transactional()
    public ClienteResponse registrar(ClienteRequest clienteRequest) {
        if (clienteRepository.existsByUsername(clienteRequest.getUsername())) {
            throw new ClienteExisteException();
        }

        Cliente c = new Cliente(clienteRequest.getUsername(), passwordEncoder.encode(clienteRequest.getPassword()));
        clienteRepository.save(c);

        return new ClienteResponse(c.getId(), c.getUsername());
    }

    //buscar un Cliente por usarname
    public ClienteResponse findUserByUsername(String username) {

        Cliente cliente = clienteRepository.findByUsername(username).orElseThrow(() -> new ClienteNoEncontradoException());

        return ClienteMapper.toResponse(cliente);
    }

    /**
     * Encontrar un Cliente por id
     */
    public ClienteResponse findById(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNoEncontradoException());

        return ClienteMapper.toResponse(cliente);
    }

    /*
     *Encontrar a todos los Clientes
     *
     */
    @Transactional(readOnly = true)
    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll().stream().map(ClienteMapper::toResponse).toList();
    }

    /*
     * Modificar Cliente
     */
    public ClienteResponse updateId(Long id, ClienteRequest clienteRequest) {


        Cliente encontrado = clienteRepository.findById(id).orElseThrow(ClienteNoEncontradoException::new);

        if (!clienteRequest.getUsername().isBlank()) {
            if (!encontrado.getUsername().equals(clienteRequest.getUsername())) {

                if (!clienteRepository.existsByUsername(clienteRequest.getUsername())) {
                    encontrado.updateUsername(clienteRequest.getUsername());
                }
            } else throw new ClienteExisteException();

            if (!clienteRequest.getPassword().isBlank()) {
                encontrado.updatePasswordHash(passwordEncoder.encode(clienteRequest.getPassword()));
            }
            clienteRepository.save(encontrado);

        }
        return ClienteMapper.toResponse(encontrado);

    }

    /*
     * Eliminar un Cliente
     */
    @Transactional
    public void delete(Long id) {
        Cliente clienteEncontrado = clienteRepository.findById(id).orElseThrow(() -> new ClienteNoEncontradoException());

        clienteRepository.delete(clienteEncontrado);

    }

    /*
     * Actualizar un cliente
     * */
    @Transactional
    public ClienteResponse update(Long id, ClienteRequest clienteRequest) {

        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNoEncontradoException::new);

        // Actualizar username
        if (clienteRequest.getUsername() != null && !clienteRequest.getUsername().isBlank()) {

            String nuevoUsername = clienteRequest.getUsername();

            if (!cliente.getUsername().equals(nuevoUsername)) {

                boolean existe = clienteRepository.existsByUsername(nuevoUsername);

                if (existe) {
                    throw new ClienteExisteException();
                }

                cliente.updateUsername(nuevoUsername);
            }
        }

        // Actualizar password
        if (clienteRequest.getPassword() != null && !clienteRequest.getPassword().isBlank()) {

            String passwordEncriptado = passwordEncoder.encode(clienteRequest.getPassword());

            cliente.updatePasswordHash(passwordEncriptado);
        }

        clienteRepository.save(cliente);

        return ClienteMapper.toResponse(cliente);
    }
}

