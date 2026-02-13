package manuel.tienda.core.producto.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Esto es lo que el cliente envía */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductoRequest {
    @NotBlank
    private String nombre;

    @NotNull
    @Positive
    private Double precio;

    @NotNull
    @Min(0)
    private Integer stock;


}
