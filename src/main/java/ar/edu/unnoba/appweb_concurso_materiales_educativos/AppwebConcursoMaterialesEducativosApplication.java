package ar.edu.unnoba.appweb_concurso_materiales_educativos;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class AppwebConcursoMaterialesEducativosApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppwebConcursoMaterialesEducativosApplication.class, args);
	}

	@Bean
	public CommandLineRunner setupDefaultUser(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		return args -> {
			User user = userRepository.findByEmail("admin@example.com");
			if (user == null) {
				user = new User();
				user.setEmail("admin@example.com");
				user.setPassword(passwordEncoder.encode("admin"));
				user.setNombre("Admin");
				user.setApellido("Admin");
				user.setRol(User.Rol.ADMINISTRADOR);
				userRepository.save(user);
			}
		};
	}

}
