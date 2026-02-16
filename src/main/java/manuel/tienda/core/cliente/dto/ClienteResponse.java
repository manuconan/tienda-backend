package manuel.tienda.core.cliente.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class ClienteResponse {

    private final Long id;
    private final String username;

}
