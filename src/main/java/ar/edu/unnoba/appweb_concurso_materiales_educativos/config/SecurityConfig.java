package ar.edu.unnoba.appweb_concurso_materiales_educativos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    final UserDetailsService userService;

    @Autowired
    public SecurityConfig(UserDetailsService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .userDetailsService(userService)
                .authorizeHttpRequests((requests) -> requests // Configura las solicitudes autorizadas
                        // Permite el acceso a ciertos recursos sin autenticación
                        .requestMatchers("/", "/webjars/**", "/resources/**","/css/**", "/js/**", "/login", "/materiales-participantes/**", "/materiales-ganadores/**","/participar", "/download/**", "/view/**").permitAll()
                        .requestMatchers("/concursante/**").hasRole("CONCURSANTE")
                        .requestMatchers("/administrador/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/evaluador/**").hasRole("EVALUADOR")
                        .anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud
                )
                // Configura el inicio de sesión basado en formularios
                .formLogin((form) -> form
                        .permitAll() // Permite el acceso a la página de inicio de sesión sin autenticación
                        // Configura la página de inicio de sesión y la página a la que se redirige después de un inicio de sesión exitoso
                        .loginPage("/login").permitAll()
                        // Configura los parámetros de nombre de usuario y contraseña
                        .usernameParameter("email").passwordParameter("password")
                        // apunta a la funcion login despues del inicio de secion//
                        .defaultSuccessUrl("/default", true) // Redirige a la página por defecto después de un inicio de sesión exitoso
                        .failureHandler((request, response, exception) -> { // Configura el manejador de errores
                            String error = null;
                            if (exception instanceof DisabledException || exception instanceof InternalAuthenticationServiceException) {
                                error = "disabled";
                            } else if (exception instanceof BadCredentialsException) {
                                error = "bad_credentials";
                            }
                            response.sendRedirect("/login?error=" + error);
                        }))

                .logout((logout) -> logout // Configura el cierre de sesión
                        .permitAll() // Permite el acceso a la página de cierre de sesión sin autenticación
                        .logoutSuccessUrl("/login"))  // Configura la página a la que se redirige después de un cierre de sesión exitoso
                .build(); // Construye la cadena de filtros de seguridad
    }

    // Método para crear un codificador de contraseñas BCrypt
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
