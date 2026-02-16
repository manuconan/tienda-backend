package manuel.tienda.core.producto.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductoResponse {

    private final Long id;
    private final String nombre;
    private final BigDecimal precio;
    private final Integer stock;
}
