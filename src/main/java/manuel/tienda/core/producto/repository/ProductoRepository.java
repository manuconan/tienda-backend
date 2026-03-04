package manuel.tienda.core.producto.repository;

import manuel.tienda.core.producto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository//Convierte a esta clase java en un beans de Spring como si fuera un @service
//con el proposito de interactuar con el esquema de BBDD

/*Ahora debemos hacer que esta interfaz extienda de JpaRepository para indicarle
* que tipo de "Entity" gestionará*/
public interface ProductoRepository extends JpaRepository<Producto,Long> {
}
