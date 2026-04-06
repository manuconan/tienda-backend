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
 * Pruebas de integración de la capa web para {@link ClienteController}.
 *
 * <p>Utiliza {@link WebMvcTest} para levantar únicamente la capa HTTP del controlador,
 * con {@link ClienteService} completamente mockeado mediante Mockito. La seguridad
 * está deshabilitada ({@link SecurityAutoConfiguration}) para centrar las pruebas
 * en la lógica del controlador.</p>
 *
 * <p>Aspectos validados:</p>
 * <ul>
 *   <li>Códigos de estado HTTP correctos para cada escenario.</li>
 *   <li>Estructura y contenido del JSON de respuesta.</li>
 *   <li>Comportamiento ante excepciones de negocio (404, 409).</li>
 *   <li>Validaciones de entrada con Bean Validation (400).</li>
 *   <li>Filtrado del listado por {@code username}, {@code activo} y combinación de ambos.</li>
 *   <li>Paginación y ordenación del listado.</li>
 *   <li>Actualización de datos de un cliente existente.</li>
 *   <li>Cambio de estado activo de un cliente.</li>
 *   <li>Búsqueda de cliente por username exacto.</li>
 * </ul>
 *
 * @author Manuel
 * @version 1.2
 * @since 2023
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

    // ──────────────────────────────────────────────────────────────────
    // POST /clientes — Registro
    // ──────────────────────────────────────────────────────────────────

    /**
     * Verifica que un registro con datos válidos devuelva {@code 201 Created}
     * junto con los datos del cliente creado, sin exponer la contraseña.
     */
    @Test
    @DisplayName("POST /clientes — Debe registrar un cliente correctamente y devolver 201")
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
     * Verifica que intentar registrar un username ya existente devuelva {@code 409 Conflict}
     * con un cuerpo de error que incluya el campo {@code message}.
     */
    @Test
    @DisplayName("POST /clientes — Debe devolver 409 si el username ya está en uso")
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
     * Verifica que datos de entrada inválidos (username demasiado corto, password corto)
     * devuelvan {@code 400 Bad Request}.
     */
    @Test
    @DisplayName("POST /clientes — Debe devolver 400 si los datos de entrada son inválidos")
    void validarDatos() throws Exception {

        ClienteRequest request = new ClienteRequest();
        request.setUsername("a"); // inválido: demasiado corto
        request.setPassword("123"); // inválido: demasiado corto

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que un cuerpo JSON mal formado devuelva {@code 400 Bad Request}.
     */
    @Test
    @DisplayName("POST /clientes — Debe devolver 400 si el JSON está mal formado")
    void jsonInvalido() throws Exception {

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ json mal }"))
                .andExpect(status().isBadRequest());
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /clientes/{id} — Consulta por ID
    // ──────────────────────────────────────────────────────────────────

    /**
     * Verifica que una consulta por ID existente devuelva {@code 200 OK}
     * con los datos correctos del cliente.
     */
    @Test
    @DisplayName("GET /clientes/{id} — Debe devolver 200 con el cliente encontrado")
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
     * Verifica que consultar un ID inexistente devuelva {@code 404 Not Found}
     * con un cuerpo de error que incluya el campo {@code message}.
     */
    @Test
    @DisplayName("GET /clientes/{id} — Debe devolver 404 si el cliente no existe")
    void clienteNoEncontrado() throws Exception {

        when(clienteService.findById(1L))
                .thenThrow(new ClienteNoEncontradoException());

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /clientes — Listado con filtros, paginación y ordenación
    // ──────────────────────────────────────────────────────────────────

    /**
     * Verifica que el listado sin filtros devuelva {@code 200 OK} con la página completa
     * de clientes y los metadatos de paginación.
     */
    @Test
    @DisplayName("GET /clientes — Debe listar clientes paginados sin filtros")
    void listarClientes() throws Exception {

        Page<ClienteResponse> page = new PageImpl<>(List.of(
                new ClienteResponse(1L, "cliente1", true),
                new ClienteResponse(2L, "cliente2", true)
        ));

        when(clienteService.findAll(Mockito.any(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].username").value("cliente1"));

        Mockito.verify(clienteService).findAll(Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
    }

    /**
     * Verifica que el filtro por {@code username} parcial devuelva {@code 200 OK}
     * con los clientes que contienen la cadena indicada.
     */
    @Test
    @DisplayName("GET /clientes?username=man — Debe filtrar clientes por username parcial")
    void listarClientesFiltradosPorUsername() throws Exception {

        Page<ClienteResponse> page = new PageImpl<>(List.of(
                new ClienteResponse(1L, "manuel", true)
        ));

        when(clienteService.findAll(Mockito.eq("man"), Mockito.isNull(), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/clientes").param("username", "man"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username").value("manuel"));

        Mockito.verify(clienteService).findAll(Mockito.eq("man"), Mockito.isNull(), Mockito.any(Pageable.class));
    }

    /**
     * Verifica que el filtro por {@code activo=true} devuelva {@code 200 OK}
     * con solo los clientes activos.
     */
    @Test
    @DisplayName("GET /clientes?activo=true — Debe filtrar clientes por estado activo")
    void listarClientesFiltradosPorActivo() throws Exception {

        Page<ClienteResponse> page = new PageImpl<>(List.of(
                new ClienteResponse(1L, "cliente1", true),
                new ClienteResponse(3L, "cliente3", true)
        ));

        when(clienteService.findAll(Mockito.isNull(), Mockito.eq(true), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/clientes").param("activo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].activo").value(true));

        Mockito.verify(clienteService).findAll(Mockito.isNull(), Mockito.eq(true), Mockito.any(Pageable.class));
    }

    /**
     * Verifica que el filtro combinado {@code username} + {@code activo} devuelva
     * {@code 200 OK} con los clientes que cumplen ambos criterios simultáneamente.
     */
    @Test
    @DisplayName("GET /clientes?username=man&activo=true — Debe filtrar por username y activo combinados")
    void listarClientesFiltradosPorUsernameYActivo() throws Exception {

        Page<ClienteResponse> page = new PageImpl<>(List.of(
                new ClienteResponse(1L, "manuel", true)
        ));

        when(clienteService.findAll(Mockito.eq("man"), Mockito.eq(true), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/clientes")
                        .param("username", "man")
                        .param("activo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username").value("manuel"))
                .andExpect(jsonPath("$.content[0].activo").value(true));

        Mockito.verify(clienteService).findAll(Mockito.eq("man"), Mockito.eq(true), Mockito.any(Pageable.class));
    }

    /**
     * Verifica que proporcionar un {@code username} de un solo carácter (longitud inferior
     * al mínimo permitido de 2) devuelva {@code 400 Bad Request}.
     */
    @Test
    @DisplayName("GET /clientes?username=a — Debe devolver 400 si el username es demasiado corto")
    void listarClientesUsernameDemasiadoCorto() throws Exception {

        mockMvc.perform(get("/clientes").param("username", "a"))
                .andExpect(status().isBadRequest());
    }

    // ──────────────────────────────────────────────────────────────────
    // DELETE /clientes/{id} — Eliminación
    // ──────────────────────────────────────────────────────────────────

    /**
     * Verifica que eliminar un cliente existente devuelva {@code 204 No Content}.
     */
    @Test
    @DisplayName("DELETE /clientes/{id} — Debe eliminar un cliente y devolver 204")
    void eliminarCliente() throws Exception {

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(clienteService).delete(1L);
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /clientes/username/{username} — Consulta por username exacto
    // ──────────────────────────────────────────────────────────────────

    /**
     * Verifica que la búsqueda por username exacto devuelva {@code 200 OK}
     * con los datos del cliente correspondiente.
     */
    @Test
    @DisplayName("GET /clientes/username/{username} — Debe devolver 200 con el cliente encontrado")
    void obtenerClientePorUsername() throws Exception {

        ClienteResponse response = new ClienteResponse(1L, "manuel", true);

        when(clienteService.findUserByUsername("manuel")).thenReturn(response);

        mockMvc.perform(get("/clientes/username/manuel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("manuel"));

        Mockito.verify(clienteService).findUserByUsername("manuel");
    }

    /**
     * Verifica que buscar un username inexistente devuelva {@code 404 Not Found}.
     */
    @Test
    @DisplayName("GET /clientes/username/{username} — Debe devolver 404 si el username no existe")
    void obtenerClientePorUsernameNoEncontrado() throws Exception {

        when(clienteService.findUserByUsername("noexiste"))
                .thenThrow(new ClienteNoEncontradoException());

        mockMvc.perform(get("/clientes/username/noexiste"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ──────────────────────────────────────────────────────────────────
    // PUT /clientes/{id} — Actualización
    // ──────────────────────────────────────────────────────────────────

    /**
     * Verifica que actualizar un cliente con datos válidos devuelva {@code 200 OK}
     * con los datos actualizados.
     */
    @Test
    @DisplayName("PUT /clientes/{id} — Debe actualizar un cliente y devolver 200")
    void actualizarCliente() throws Exception {

        ClienteRequest request = new ClienteRequest();
        request.setUsername("nuevoUsername");
        request.setPassword("nuevaPassword1");

        ClienteResponse response = new ClienteResponse(1L, "nuevoUsername", true);

        when(clienteService.update(Mockito.eq(1L), Mockito.any(ClienteRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("nuevoUsername"));

        Mockito.verify(clienteService).update(Mockito.eq(1L), Mockito.any(ClienteRequest.class));
    }

    /**
     * Verifica que intentar actualizar un cliente con un username ya en uso
     * devuelva {@code 409 Conflict}.
     */
    @Test
    @DisplayName("PUT /clientes/{id} — Debe devolver 409 si el nuevo username ya está en uso")
    void actualizarClienteUsernameEnUso() throws Exception {

        ClienteRequest request = new ClienteRequest();
        request.setUsername("existente");
        request.setPassword("password123");

        when(clienteService.update(Mockito.eq(1L), Mockito.any(ClienteRequest.class)))
                .thenThrow(new ClienteExisteException());

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Verifica que intentar actualizar un cliente inexistente devuelva {@code 404 Not Found}.
     */
    @Test
    @DisplayName("PUT /clientes/{id} — Debe devolver 404 si el cliente no existe")
    void actualizarClienteNoEncontrado() throws Exception {

        ClienteRequest request = new ClienteRequest();
        request.setUsername("cualquier");
        request.setPassword("password123");

        when(clienteService.update(Mockito.eq(99L), Mockito.any(ClienteRequest.class)))
                .thenThrow(new ClienteNoEncontradoException());

        mockMvc.perform(put("/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ──────────────────────────────────────────────────────────────────
    // PATCH /clientes/{id}/estado — Cambio de estado activo
    // ──────────────────────────────────────────────────────────────────

    /**
     * Verifica que cambiar el estado activo a {@code false} devuelva {@code 200 OK}
     * con el cliente actualizado.
     */
    @Test
    @DisplayName("PATCH /clientes/{id}/estado?activo=false — Debe desactivar el cliente y devolver 200")
    void cambiarEstadoCliente() throws Exception {

        ClienteResponse response = new ClienteResponse(1L, "cliente1", false);

        when(clienteService.cambiarEstado(1L, false)).thenReturn(response);

        mockMvc.perform(patch("/clientes/1/estado").param("activo", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.activo").value(false));

        Mockito.verify(clienteService).cambiarEstado(1L, false);
    }

    /**
     * Verifica que intentar cambiar el estado de un cliente inexistente devuelva {@code 404 Not Found}.
     */
    @Test
    @DisplayName("PATCH /clientes/{id}/estado — Debe devolver 404 si el cliente no existe")
    void cambiarEstadoClienteNoEncontrado() throws Exception {

        when(clienteService.cambiarEstado(99L, true))
                .thenThrow(new ClienteNoEncontradoException());

        mockMvc.perform(patch("/clientes/99/estado").param("activo", "true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}

