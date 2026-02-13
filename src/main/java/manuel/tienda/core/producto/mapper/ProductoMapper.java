package manuel.tienda.core.producto.mapper;


import manuel.tienda.core.producto.dto.ProductoRequest;
import manuel.tienda.core.producto.dto.ProductoResponse;
import manuel.tienda.core.producto.entity.Producto;

public class ProductoMapper {


    public static Producto toEntity(ProductoRequest productoRequest) {
        Producto p = new Producto();
        p.setNombre(productoRequest.getNombre());

        return p;
    }

    public static ProductoResponse toResponse(Producto producto) {
        ProductoResponse response = new ProductoResponse();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setPrecio(producto.getPrecio());
        response.setStock(producto.getStock());
        return response;
    }
}
