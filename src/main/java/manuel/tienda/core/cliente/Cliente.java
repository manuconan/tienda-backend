package manuel.tienda.core.cliente;


import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Setter
public class Cliente {
    private String username;
    private String password;
    private String nombre;


}
