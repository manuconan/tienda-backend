package manuel.tienda.producto.service;

import manuel.tienda.producto.entity.Producto;
import manuel.tienda.exception.ProductoInvalidoException;
import manuel.tienda.exception.ProductoNoEncontradoException;
import manuel.tienda.producto.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Crea un nuevo producto validando sus datos.
     */
    public Producto create(Producto producto) {
        validarProducto(producto);
        return productoRepository.save(producto);
    }

    /**
     * Obtiene todos los productos.
     */
    @Transactional(readOnly = true)
    public Page<Producto> findAll(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    /**
     * Actualiza un producto existente.
     */
    public Producto update(Long id, Producto producto) {
        validarProducto(producto);

        Producto existente = obtenerProducto(id);
        existente.setNombre(producto.getNombre());
        existente.setPrecio(producto.getPrecio());
        existente.setStock(producto.getStock());

        return productoRepository.save(existente);
    }

    /**
     * Busca un producto por su ID.
     */
    @Transactional(readOnly = true)
    public Producto findById(Long id) {

        return obtenerProducto(id);
    }

    /**
     * Elimina un producto por su ID.
     */
    public void delete(Long id) {
        Producto producto = obtenerProducto(id);
        productoRepository.delete(producto);
    }

    /**
     * Metodo reutilizable para obtener producto o lanzar excepción.
     */
    private Producto obtenerProducto(Long id) {
        if (id == null) {
            throw new ProductoInvalidoException("El id no puede ser nulo");
        }

        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    /**
     * Valida reglas de negocio del producto.
     */
    private void validarProducto(Producto producto) {

        if (producto == null) {
            throw new ProductoInvalidoException("El producto no puede ser nulo");
        }

        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new ProductoInvalidoException("El nombre no puede estar vacío");
        }

        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO)<=0) {
            throw new ProductoInvalidoException("El precio debe ser mayor que cero");
        }

        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new ProductoInvalidoException("El stock no puede ser negativo");
        }
    }
}
