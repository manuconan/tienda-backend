package manuel.tienda.cliente.repository;

import manuel.tienda.cliente.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para gestionar la persistencia de la entidad Cliente.
 *
 * @author Manuel
 * @version 1.0
 * @since 2023
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca un cliente por username.
     *
     * @param username nombre de usuario a buscar
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findByUsername(String username);

    /**
     * Verifica si existe un cliente con el username especificado.
     *
     * @param username nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Busca clientes cuyo username contenga la cadena especificada.
     *
     * @param username cadena parcial a buscar
     * @param pageable información de paginación
     * @return página con clientes que coinciden
     */
    Page<Cliente> findByUsernameContaining(String username, Pageable pageable);

    /**
     * Busca clientes por su estado activo.
     *
     * @param activo estado del cliente (true/false)
     * @param pageable información de paginación
     * @return página con clientes que coinciden
     */
    Page<Cliente> findByActivo(Boolean activo, Pageable pageable);

    /**
     * Busca clientes por username y estado activo.
     *
     * @param username cadena parcial a buscar en el username
     * @param pageable información de paginación
     * @return página con clientes que coinciden ambos criterios
     */
    Page<Cliente> findByUsernameContainingAndActivo(String username, Pageable pageable);
}
