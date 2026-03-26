package manuel.tienda.cliente.mapper;

import manuel.tienda.cliente.dto.ClienteResponse;
import manuel.tienda.cliente.entity.Cliente;


public class ClienteMapper {


    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getUsername(),
                cliente.getActivo()
        );
    }
}



