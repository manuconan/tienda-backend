package manuel.tienda.cliente.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import manuel.tienda.auth.service.JwtService;
import manuel.tienda.cliente.dto.ClienteRequest;
import manuel.tienda.cliente.dto.ClienteResponse;
import manuel.tienda.cliente.service.ClienteService;
import manuel.tienda.exception.ClienteExisteException;
import manuel.tienda.exception.ClienteNoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clase de pruebas para ClienteController.
 *
 * <p>
 * Se utiliza @WebMvcTest para probar únicamente la capa web,
 * mockeando el servicio ClienteService.
 * </p>
 *
 * <p>
 * Se validan:
 * - Respuestas HTTP correctas
 * - Estructura del JSON
 * - Manejo de excepciones
 * - Validaciones de entrada
 * </p>
 */
@WebMvcTest(controllers = ClienteController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Verifica que se registre un cliente correctamente.
     * Debe devolver 201 y el cliente creado.
     */
    @Test
    @DisplayName("Debe registrar un cliente correctamente")
    void registrarCliente() throws Exception {

        ClienteRequest request = new ClienteRequest();
        request.setUsername("cliente1");
        request.setPassword("password123");

        ClienteResponse response = new ClienteResponse(1L, "cliente1", true);

        when(clienteService.registrar(Mockito.any(ClienteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("cliente1"))
                .andExpect(jsonPath("$.password").doesNotExist());

        Mockito.verify(clienteService).registrar(Mockito.any(ClienteRequest.class));
    }

    /**
     * Verifica que se obtenga un cliente por ID correctamente.
     */
    @Test
    @DisplayName("Debe obtener un cliente por id")
    void obtenerClientePorId() throws Exception {

        ClienteResponse response = new ClienteResponse(1L, "cliente1", true);

        when(clienteService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("cliente1"));

        Mockito.verify(clienteService).findById(1L);
    }

    /**
     * Verifica que se liste correctamente la paginación de clientes.
     */
    @Test
    @DisplayName("Debe listar clientes con paginación")
    void listarClientes() throws Exception {

        Page<ClienteResponse> page = new PageImpl<>(List.of(
                new ClienteResponse(1L, "cliente1", true),
                new ClienteResponse(2L, "cliente2", true)
        ));

        when(clienteService.findAll(Mockito.any(), Mockito.any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].username").value("cliente1"));

        Mockito.verify(clienteService).findAll(Mockito.any(), Mockito.any(Pageable.class));
    }

    /**
     * Verifica que se elimine un cliente correctamente.
     * Debe devolver 204 (No Content).
     */
    @Test
    @DisplayName("Debe eliminar un cliente correctamente")
    void eliminarCliente() throws Exception {

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(clienteService).delete(1L);
    }

    /**
     * Verifica que se devuelva 404 cuando el cliente no existe.
     */
    @Test
    @DisplayName("Debe retornar 404 si el cliente no existe")
    void clienteNoEncontrado() throws Exception {

        when(clienteService.findById(1L))
                .thenThrow(new ClienteNoEncontradoException());

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Verifica que no se permita registrar un cliente duplicado.
     */
    @Test
    @DisplayName("Debe retornar 409 si el cliente ya existe")
    void clienteDuplicado() throws Exception {

        ClienteRequest request = new ClienteRequest();
        request.setUsername("cliente1");
        request.setPassword("password123");

        when(clienteService.registrar(Mockito.any(ClienteRequest.class)))
                .thenThrow(new ClienteExisteException());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Verifica que se validen correctamente los datos de entrada.
     */
    @Test
    @DisplayName("Debe validar datos incorrectos")
    void validarDatos() throws Exception {

        ClienteRequest request = new ClienteRequest();
        request.setUsername("a"); // inválido
        request.setPassword("123"); // inválido

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que un JSON mal formado devuelva 400.
     */
    @Test
    @DisplayName("Debe retornar 400 si el JSON es inválido")
    void jsonInvalido() throws Exception {

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ json mal }"))
                .andExpect(status().isBadRequest());
    }
}