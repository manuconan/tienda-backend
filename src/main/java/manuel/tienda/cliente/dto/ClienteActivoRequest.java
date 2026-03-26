package manuel.tienda.cliente.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para solicitudes de cambio de estado activo de un cliente.
 * 
 * @author Manuel
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
public class ClienteActivoRequest {

    @NotNull(message = "El estado activo no puede ser nulo")
    private Boolean activo;

}

