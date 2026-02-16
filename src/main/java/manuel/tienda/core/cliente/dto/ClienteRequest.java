package manuel.tienda.core.cliente.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ClienteRequest {

    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 4,max = 15)
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8,max = 100)
    private String password;


}
