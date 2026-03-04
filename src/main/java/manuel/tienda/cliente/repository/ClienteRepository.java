package manuel.tienda.cliente.repository;

import manuel.tienda.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Este metodo lo pongo pq en la entidad hemos dicho que tiene que ser unica
    Optional<Cliente> findByUsername(String username);

    boolean existsByUsername(String username);
}
