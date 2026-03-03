package manuel.tienda.producto.mapper;


import manuel.tienda.producto.dto.ProductoRequest;
import manuel.tienda.producto.dto.ProductoResponse;
import manuel.tienda.producto.entity.Producto;

public class ProductoMapper {


    public static Producto toEntity(ProductoRequest productoRequest) {
        Producto p = new Producto();
        p.setNombre(productoRequest.getNombre());

        return p;
    }

    public static ProductoResponse toResponse(Producto producto) {
        ProductoResponse response=new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
        );
        return response ;
    }
}
