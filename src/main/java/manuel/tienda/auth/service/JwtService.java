package manuel.tienda.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;
import manuel.tienda.cliente.entity.Cliente;

import java.security.Key;
import java.util.Date;

/**
 * Servicio responsable de generar y validar tokens JWT utilizados por el sistema.
 */
@Service
public class JwtService {

    // Clave HMAC para firma HS256. Debe tener al menos 32 caracteres.
    private static final String SECRET = "clave_secreta_super_segura_de_al_menos_32_caracteres_largos_123";

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Genera un token JWT con el usuario autenticado y su rol.
     *
     * @param cliente cliente autenticado
     * @return token JWT firmado
     */
    public String generateToken(Cliente cliente) {

        if (cliente.getRole() == null) {
            throw new IllegalStateException("El cliente no tiene rol asignado");
        }

        return Jwts.builder()
                .setSubject(cliente.getUsername())
                .claim("role", cliente.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el username contenido en el token.
     *
     * @param token token JWT
     * @return username del sujeto del token
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrae el rol incluido como claim en el token.
     *
     * @param token token JWT
     * @return nombre del rol
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Valida firma, estructura y expiracion del token.
     *
     * @param token token JWT
     * @return {@code true} si el token es valido; {@code false} en caso contrario
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene todos los claims de un token JWT.
     *
     * @param token token JWT
     * @return claims parseados
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}