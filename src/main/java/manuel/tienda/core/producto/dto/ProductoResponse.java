package manuel.tienda.core.producto.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Esto es lo que el cliente recibe despues de hacer la petición */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponse {
    public Long id;
    public String nombre;
    public Double precio;
    public Integer stock;
}
