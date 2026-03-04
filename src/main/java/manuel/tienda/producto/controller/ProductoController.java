package manuel.tienda.producto.controller;

import jakarta.validation.Valid;
import manuel.tienda.producto.dto.ProductoRequest;
import manuel.tienda.producto.dto.ProductoResponse;
import manuel.tienda.producto.entity.Producto;
import manuel.tienda.producto.mapper.ProductoMapper;
import manuel.tienda.producto.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Obtener todos los productos.
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> getAll() {

        List<ProductoResponse> productos = productoService.findAll()
                .stream()
                .map(ProductoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(productos);
    }

    /**
     * Crear un producto.
     */
    @PostMapping
    public ResponseEntity<ProductoResponse> create(
            @Valid @RequestBody ProductoRequest request) {

        Producto producto = ProductoMapper.toEntity(request);

        Producto creado = productoService.create(producto);

        ProductoResponse response = ProductoMapper.toResponse(creado);

        return ResponseEntity
                .created(URI.create("/productos/" + creado.getId()))
                .body(response);
    }

    /**
     * Actualizar un producto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {

        Producto producto = ProductoMapper.toEntity(request);

        Producto actualizado = productoService.update(id, producto);

        ProductoResponse response = ProductoMapper.toResponse(actualizado);

        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar un producto.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        productoService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Buscar producto por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable Long id) {

        Producto producto = productoService.findById(id);

        ProductoResponse response = ProductoMapper.toResponse(producto);

        return ResponseEntity.ok(response);
    }
}
