package manuel.tienda.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import manuel.tienda.auth.service.JwtService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Leer header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Si no hay token → continuar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer token
        String token = authHeader.substring(7);

        // 4. Extraer username del token
        String username = jwtService.extractUsername(token);

        // 5. Si hay username y no hay auth previa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Crear autenticación (sin roles de momento)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList()
                    );

            // 7. Guardar en contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 8. Continuar la cadena
        filterChain.doFilter(request, response);
    }
}