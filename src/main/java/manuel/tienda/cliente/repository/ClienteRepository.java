package manuel.tienda.cliente.repository;

import manuel.tienda.cliente.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la gestión de la persistencia de la entidad {@link Cliente}.
 *
 * <p>Extiende {@link JpaRepository} proporcionando operaciones CRUD estándar y
 * consultas derivadas adicionales necesarias para el sistema de filtrado dinámico
 * del endpoint {@code GET /clientes}.</p>
 *
 * <p>Los métodos de búsqueda paginada cubren los cuatro casos posibles de filtrado:</p>
 * <ul>
 *   <li>Sin filtros           — {@link #findAll(Pageable)} (heredado de JpaRepository)</li>
 *   <li>Solo username         — {@link #findByUsernameContaining(String, Pageable)}</li>
 *   <li>Solo activo           — {@link #findByActivo(Boolean, Pageable)}</li>
 *   <li>Username y activo     — {@link #findByUsernameContainingAndActivo(String, Boolean, Pageable)}</li>
 * </ul>
 *
 * @author Manuel
 * @version 1.1
 * @since 2023
 * @see manuel.tienda.cliente.service.ClienteService
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca un cliente por su username exacto.
     *
     * <p>Utilizado en el proceso de autenticación y en la búsqueda de usuario
     * por nombre único.</p>
     *
     * @param username nombre de usuario a buscar (valor exacto, sensible a mayúsculas).
     * @return {@link Optional} con el cliente si existe, vacío en caso contrario.
     */
    Optional<Cliente> findByUsername(String username);

    /**
     * Verifica si ya existe un cliente registrado con el username indicado.
     *
     * <p>Utilizado en las operaciones de registro y actualización para garantizar
     * la unicidad del username antes de persistir cambios.</p>
     *
     * @param username nombre de usuario a verificar.
     * @return {@code true} si existe al menos un cliente con ese username;
     *         {@code false} en caso contrario.
     */
    boolean existsByUsername(String username);

    /**
     * Busca clientes cuyo username contenga la cadena indicada, con paginación.
     *
     * <p>La búsqueda es parcial e insensible a mayúsculas dependiendo de la
     * configuración del motor de base de datos.</p>
     *
     * @param username cadena parcial a buscar dentro del campo username.
     * @param pageable configuración de paginación y ordenación.
     * @return página de clientes cuyo username contiene la cadena proporcionada.
     */
    Page<Cliente> findByUsernameContaining(String username, Pageable pageable);

    /**
     * Busca clientes por su estado activo, con paginación.
     *
     * <p>Permite listar únicamente clientes activos ({@code activo = true})
     * o únicamente clientes inactivos ({@code activo = false}).</p>
     *
     * @param activo estado activo a filtrar ({@code true} o {@code false}).
     * @param pageable configuración de paginación y ordenación.
     * @return página de clientes que coinciden con el estado activo indicado.
     */
    Page<Cliente> findByActivo(Boolean activo, Pageable pageable);

    /**
     * Busca clientes cuyo username contenga la cadena indicada y cuyo estado activo
     * coincida con el valor proporcionado, con paginación.
     *
     * <p>Combina los filtros de {@link #findByUsernameContaining} y
     * {@link #findByActivo} en una única consulta eficiente.</p>
     *
     * @param username cadena parcial a buscar dentro del campo username.
     * @param activo   estado activo a filtrar ({@code true} o {@code false}).
     * @param pageable configuración de paginación y ordenación.
     * @return página de clientes que cumplen ambos criterios simultáneamente.
     */
    Page<Cliente> findByUsernameContainingAndActivo(String username, Boolean activo, Pageable pageable);
}
