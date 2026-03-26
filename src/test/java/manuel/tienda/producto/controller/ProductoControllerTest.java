package manuel.tienda.producto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import manuel.tienda.producto.dto.ProductoRequest;
import manuel.tienda.producto.dto.ProductoResponse;
import manuel.tienda.producto.entity.Producto;
import manuel.tienda.producto.service.ProductoService;
import manuel.tienda.exception.ProductoNoEncontradoException;
import manuel.tienda.exception.ProductoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clase de pruebas unitarias para el controlador ProductoController.
 * <p>
 * Utiliza @WebMvcTest para probar únicamente el controlador y sus dependencias web,
 * mockeando el servicio ProductoService. Verifica el comportamiento de los endpoints
 * REST sin necesidad de una base de datos real.
 * </p>
 *
 * @author Manuel
 * @version 1.0
 * @since 2023
 */
@WebMvcTest(controllers = ProductoController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Prueba que se listen todos los productos correctamente.
     * <p>
     * Verifica que:
     * - El servicio sea llamado con Pageable.
     * - La respuesta tenga el código de estado 200.
     * - El JSON de respuesta contenga la lista de productos.
     * </p>
     *
     * @throws Exception si ocurre un error durante la ejecución de la prueba
     */
    @Test
    @DisplayName("Debe listar todos los productos")
    void listarProductos() throws Exception {
        // Preparar lista de productos
        List<Producto> productos = List.of(
                new Producto(1L, "Producto1", BigDecimal.valueOf(10.0), 5),
                new Producto(2L, "Producto2", BigDecimal.valueOf(20.0), 10)
        );
        Page<Producto> page = new PageImpl<>(productos);

        // Configurar el mock del servicio
        when(productoService.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        // Llamar al endpoint y verificar resultados
        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("Producto1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].nombre").value("Producto2"));
    }

    /**
     * Prueba que se cree un producto correctamente.
     * <p>
     * Verifica que:
     * - El servicio sea llamado con el producto mapeado.
     * - La respuesta tenga el código de estado 201.
     * - El JSON de respuesta contenga los datos del producto creado.
     * </p>
     *
     * @throws Exception si ocurre un error durante la ejecución de la prueba
     */
    @Test
    @DisplayName("Debe crear un producto correctamente")
    void crearProducto() throws Exception {
        // Preparar datos de entrada
        ProductoRequest request = new ProductoRequest("Producto1", BigDecimal.valueOf(10.0), 5);

        // Preparar producto creado
        Producto creado = new Producto(1L, "Producto1", BigDecimal.valueOf(10.0), 5);

        // Configurar el mock del servicio
        when(productoService.create(Mockito.any(Producto.class))).thenReturn(creado);

        // Llamar al endpoint y verificar resultados
        mockMvc.perform(post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Producto1"))
                .andExpect(jsonPath("$.precio").value(10.0))
                .andExpect(jsonPath("$.stock").value(5));
    }

    /**
     * Prueba que se actualice un producto correctamente.
     * <p>
     * Verifica que:
     * - El servicio sea llamado con el ID y producto.
     * - La respuesta tenga el código de estado 200.
     * - El JSON de respuesta contenga los datos actualizados.
     * </p>
     *
     * @throws Exception si ocurre un error durante la ejecución de la prueba
     */
    @Test
    @DisplayName("Debe actualizar un producto correctamente")
    void actualizarProducto() throws Exception {
        // Preparar datos de entrada
        ProductoRequest request = new ProductoRequest("Producto Actualizado", BigDecimal.valueOf(15.0), 8);

        // Preparar producto actualizado
        Producto actualizado = new Producto(1L, "Producto Actualizado", BigDecimal.valueOf(15.0), 8);

        // Configurar el mock del servicio
        when(productoService.update(eq(1L), Mockito.any(Producto.class))).thenReturn(actualizado);

        // Llamar al endpoint y verificar resultados
        mockMvc.perform(put("/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Producto Actualizado"))
                .andExpect(jsonPath("$.precio").value(15.0))
                .andExpect(jsonPath("$.stock").value(8));
    }

    /**
     * Prueba que se elimine un producto correctamente.
     * <p>
     * Verifica que:
     * - El servicio sea llamado con el ID correcto.
     * - La respuesta tenga el código de estado 204.
     * </p>
     *
     * @throws Exception si ocurre un error durante la ejecución de la prueba
     */
    @Test
    @DisplayName("Debe eliminar un producto correctamente")
    void eliminarProducto() throws Exception {
        // Configurar el mock del servicio
        doNothing().when(productoService).delete(1L);

        // Llamar al endpoint y verificar resultados
        mockMvc.perform(delete("/productos/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Prueba que se obtenga un producto por ID correctamente.
     * <p>
     * Verifica que:
     * - El servicio sea llamado con el ID correcto.
     * - La respuesta tenga el código de estado 200.
     * - El JSON de respuesta contenga los datos del producto.
     * </p>
     *
     * @throws Exception si ocurre un error durante la ejecución de la prueba
     */
    @Test
    @DisplayName("Debe obtener un producto por ID")
    void obtenerProductoPorId() throws Exception {
        // Preparar producto
        Producto producto = new Producto(1L, "Producto1", BigDecimal.valueOf(10.0), 5);

        // Configurar el mock del servicio
        when(productoService.findById(1L)).thenReturn(producto);

        // Llamar al endpoint y verificar resultados
        mockMvc.perform(get("/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Producto1"))
                .andExpect(jsonPath("$.precio").value(10.0))
                .andExpect(jsonPath("$.stock").value(5));
    }

    /**
     * Prueba que se maneje correctamente cuando no se encuentra un producto por ID.
     * <p>
     * Verifica que:
     * - El servicio lance ProductoNoEncontradoException.
     * - La respuesta tenga el código de estado 404.
     * </p>
     *
     * @throws Exception si ocurre un error durante la ejecución de la prueba
     */
    @Test
    @DisplayName("Debe retornar 404 cuando el producto no existe")
    void productoNoEncontrado() throws Exception {
        // Configurar el mock del servicio para lanzar excepción
        when(productoService.findById(1L)).thenThrow(new ProductoNoEncontradoException(1L));

        // Llamar al endpoint y verificar que retorne 404
        mockMvc.perform(get("/productos/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Prueba que se maneje correctamente la eliminación de un producto inexistente.
     * <p>
     * Verifica que:
     * - El servicio lance ProductoNoEncontradoException.
     * - La respuesta tenga el código de estado 404.
     * </p>
     *
     * @throws Exception si ocurre un error durante la ejecución de la prueba
     */
    @Test
    @DisplayName("Debe retornar 404 al intentar eliminar producto inexistente")
    void eliminarProductoInexistente() throws Exception {
        // Configurar el mock del servicio para lanzar excepción
        doThrow(new ProductoNoEncontradoException(1L)).when(productoService).delete(1L);

        // Llamar al endpoint y verificar que retorne 404
        mockMvc.perform(delete("/productos/1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Prueba que se validen correctamente los datos de entrada en la creación.
     * <p>
     * Verifica que:
     * - Datos inválidos generen error 400.
     * - La respuesta contenga detalles del error de validación.
     * </p>
     *
     * @throws Exception si ocurre un error durante la ejecución de la prueba
     */
    @Test
    @DisplayName("Debe validar datos de entrada en creación")
    void validarDatosCreacion() throws Exception {
        // Preparar datos de entrada inválidos
        ProductoRequest request = new ProductoRequest("", BigDecimal.valueOf(-1.0), -1); // Nombre vacío, precio negativo, stock negativo

        // Llamar al endpoint con datos inválidos y verificar que retorne 400
        mockMvc.perform(post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Prueba que se maneje correctamente la actualización de un producto inexistente.
     * <p>
     * Verifica que:
     * - El servicio lance ProductoNoEncontradoException.
     * - La respuesta tenga el código de estado 404.
     * </p>
     *
     * @throws Exception si ocurre un error durante la ejecución de la prueba
     */
    @Test
    @DisplayName("Debe retornar 404 al intentar actualizar producto inexistente")
    void actualizarProductoInexistente() throws Exception {
        // Preparar datos de entrada
        ProductoRequest request = new ProductoRequest("Producto", BigDecimal.valueOf(10.0), 5);

        // Configurar el mock del servicio para lanzar excepción
        when(productoService.update(eq(1L), Mockito.any(Producto.class))).thenThrow(new ProductoNoEncontradoException(1L));

        // Llamar al endpoint y verificar que retorne 404
        mockMvc.perform(put("/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
