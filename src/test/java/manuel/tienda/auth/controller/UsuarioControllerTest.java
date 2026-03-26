package manuel.tienda.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import manuel.tienda.auth.dto.UsuarioRequest;
import manuel.tienda.auth.dto.UsuarioResponse;
import manuel.tienda.auth.entity.Usuario;
import manuel.tienda.auth.service.UsuarioService;
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

@WebMvcTest(controllers = UsuarioController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper; 

    @Test
    @DisplayName("Debe listar todos los usuarios con paginación")
    void listarUsuarios() throws Exception {

        Usuario user1 = new Usuario();
        user1.setId(1L);
        user1.setUsername("usuario1");
        user1.setEnabled(true);

        Usuario user2 = new Usuario();
        user2.setId(2L);
        user2.setUsername("usuario2");
        user2.setEnabled(true);

        Page<Usuario> page = new PageImpl<>(List.of(user1, user2));

        when(usuarioService.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].username").value("usuario1"))
                .andExpect(jsonPath("$.content[0].id").value(1));

        Mockito.verify(usuarioService).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Debe registrar un usuario correctamente")
    void registrarUsuario() throws Exception {

        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("usuario1");
        request.setPassword("password123");

        Usuario usuarioCreado = new Usuario();
        usuarioCreado.setId(1L);
        usuarioCreado.setUsername("usuario1");
        usuarioCreado.setEnabled(true);

        when(usuarioService.create(Mockito.any(Usuario.class))).thenReturn(usuarioCreado);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("usuario1"));

        Mockito.verify(usuarioService).create(Mockito.any(Usuario.class));
    }

    @Test
    @DisplayName("Debe obtener un usuario por username")
    void obtenerUsuarioPorUsername() throws Exception {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("usuario1");
        usuario.setEnabled(true);

        when(usuarioService.findByUsername("usuario1")).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/usuario1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("usuario1"));

        Mockito.verify(usuarioService).findByUsername("usuario1");
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el usuario no existe con body de error")
    void usuarioNoEncontrado() throws Exception {

        when(usuarioService.findByUsername("usuario1"))
                .thenThrow(new ClienteNoEncontradoException());

        mockMvc.perform(get("/usuarios/usuario1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());

        Mockito.verify(usuarioService).findByUsername("usuario1");
    }

    @Test
    @DisplayName("Debe retornar 409 al intentar registrar usuario duplicado")
    void registrarUsuarioDuplicado() throws Exception {

        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("usuario1");
        request.setPassword("password123");

        when(usuarioService.create(Mockito.any(Usuario.class)))
                .thenThrow(new ClienteExisteException());

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());

        Mockito.verify(usuarioService).create(Mockito.any(Usuario.class));
    }

    @Test
    @DisplayName("Debe validar datos de entrada en registro")
    void validarDatosRegistro() throws Exception {

        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("a");
        request.setPassword("123");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Debe retornar 500 si el JSON está mal formado")
    void jsonInvalido() throws Exception {

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ json mal formado }"))
                .andExpect(status().isInternalServerError());
    }
}