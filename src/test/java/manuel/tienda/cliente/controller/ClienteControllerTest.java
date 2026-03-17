package manuel.tienda.cliente.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import manuel.tienda.cliente.dto.ClienteRequest;
import manuel.tienda.cliente.dto.ClienteResponse;
import manuel.tienda.cliente.service.ClienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe registrar un cliente correctamente")
    void registrarCliente() throws Exception {

        ClienteRequest request = new ClienteRequest();
        request.setUsername("manuel");
        request.setPassword("password123");

        ClienteResponse response = new ClienteResponse(1L, "manuel");

        when(clienteService.registrar(Mockito.any())).thenReturn(response);
    }
}
