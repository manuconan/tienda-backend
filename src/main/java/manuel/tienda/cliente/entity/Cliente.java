package manuel.tienda.cliente.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 60)
    private String passwordHash;

    @Column(nullable = false)
    private Boolean activo = true;

    public Cliente(String username, String passwordHash) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username no puede estar vacío");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("PasswordHash no puede estar vacío");
        }
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public void updateUsername(String username) {
        if (username != null || !username.isEmpty() || !username.isBlank()) {
            this.username = username;
        }
    }

    public void updatePasswordHash(String passwordHash) {
        if (passwordHash != null || !passwordHash.isEmpty() || !passwordHash.isEmpty()) {
            this.passwordHash = passwordHash;
        }
    }
}
