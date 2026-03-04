package manuel.tienda;

import manuel.tienda.producto.entity.Producto;
import manuel.tienda.exception.ProductoInvalidoException;
import manuel.tienda.exception.ProductoNoEncontradoException;
import manuel.tienda.producto.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para ProductoService.
 * Se ejecutan contra el contexto real de Spring.
 */
@SpringBootTest
@Transactional
class ProductoServiceTest {

    @Autowired
    private ProductoService productoService;

    /**
     * Crea un producto válido reutilizable para los tests.
     * Centraliza la creación para evitar duplicación y facilitar mantenimiento.
     */
    private Producto crearProductoValido() {
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.valueOf(10.0));
        producto.setStock(5);
        return producto;
    }

    @Test
    @DisplayName("Debe guardar el producto correctamente")
    void create_productoValido_seGuardaCorrectamente() {

        Producto guardado = productoService.create(crearProductoValido());

        assertNotNull(guardado.getId());
        assertEquals("Producto Test", guardado.getNombre());
        assertEquals(10.0, guardado.getPrecio());
        assertEquals(5, guardado.getStock());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nombre está vacío")
    void create_nombreVacio_lanzaExcepcion() {

        Producto producto = crearProductoValido();
        producto.setNombre("");

        assertThrows(
                ProductoInvalidoException.class,
                () -> productoService.create(producto)
        );
    }

    @Test
    @DisplayName("Debe actualizar un producto correctamente")
    void update_productoExistente_seActualizaCorrectamente() {

        Producto guardado = productoService.create(crearProductoValido());

        Producto nuevo = new Producto();
        nuevo.setNombre("Actualizado");
        nuevo.setPrecio(BigDecimal.valueOf(20.0));
        nuevo.setStock(10);

        Producto actualizado = productoService.update(guardado.getId(), nuevo);

        assertEquals("Actualizado", actualizado.getNombre());
        assertEquals(20.0, actualizado.getPrecio());
        assertEquals(10, actualizado.getStock());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto no existe al actualizar")
    void update_productoNoExiste_lanzaExcepcion() {

        assertThrows(
                ProductoNoEncontradoException.class,
                () -> productoService.update(999L, crearProductoValido())
        );
    }

    @Test
    @DisplayName("Debe encontrar producto por id")
    void findById_productoExiste_loEncuentra() {

        Producto guardado = productoService.create(crearProductoValido());

        Producto encontrado = productoService.findById(guardado.getId());

        assertNotNull(encontrado);
        assertEquals(guardado.getId(), encontrado.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto no existe al buscar")
    void findById_productoNoExiste_lanzaExcepcion() {

        assertThrows(
                ProductoNoEncontradoException.class,
                () -> productoService.findById(999L)
        );
    }

    @Test
    @DisplayName("Debe eliminar un producto correctamente")
    void delete_productoExiste_seEliminaCorrectamente() {

        Producto guardado = productoService.create(crearProductoValido());

        productoService.delete(guardado.getId());

        assertThrows(
                ProductoNoEncontradoException.class,
                () -> productoService.findById(guardado.getId())
        );
    }
}
