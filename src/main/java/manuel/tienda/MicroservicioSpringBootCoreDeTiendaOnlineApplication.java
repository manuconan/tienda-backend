package manuel.tienda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MicroservicioSpringBootCoreDeTiendaOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioSpringBootCoreDeTiendaOnlineApplication.class, args);
	}

}
