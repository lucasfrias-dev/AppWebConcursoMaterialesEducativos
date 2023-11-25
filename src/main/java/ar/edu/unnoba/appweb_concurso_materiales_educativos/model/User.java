package ar.edu.unnoba.appweb_concurso_materiales_educativos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Entity
@Table(name="usuarios")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Long id;

    @Column(name="email", nullable = false, unique = true)
    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "El email debe ser valido")
    private String email;

    @Column(name ="password")
    @NotBlank( message = "la Contraseña no puede estar vacia")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") // Validación de que la contraseña tenga al menos 8 caracteres al registrar un usuario
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "La contraseña debe contener al menos una letra minúscula, una letra mayúscula y un número") // Validación de que la contraseña tenga al menos una letra minúscula, una letra mayúscula y un número al registrar un usuario
    private String password;

    @Column(name = "nombre")
    @NotBlank(message = "El nombre no puede estar vacío") // Validación de que el nombre no esté vacío al registrar un usuario
    private String nombre;

    @Column(name = "apellido")
    @NotBlank(message = "El apellido no puede estar vacío") // Validación de que el apellido no esté vacío al registrar un usuario
    private String apellido;

    @Column(name = "rol")
    @Enumerated(EnumType.STRING)
    private Rol rol;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "concursante", fetch = FetchType.EAGER)
    private Set<Material> materialesPostulados = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "evaluador_material",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id"))
    private Set<Material> materialesAEvaluar = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "evaluador", fetch = FetchType.EAGER)
    private Set<Evaluacion> evaluacionesRealizadas = new HashSet<>();

    // Métodos de la interfaz UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.toString()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'';
    }

    // Rol es un enum que define los roles de los usuarios
    public enum Rol {
        ADMINISTRADOR,
        EVALUADOR,
        CONCURSANTE
    }


// Estas funciones me permite saber el rol del usuario//
    /*public boolean isParticipante(){
        return this.getRol().toString().equals("CONCURSANTE");
    }
    public boolean isEvaluador(){
        return this.getTipo().toString().equals("EVALUADOR");
    }
    public boolean isAdministrador(){
        return this.getRol().toString().equals("ADMINISTRADOR");
    }
   public Material getMaterialEducativo(){
        //aqui tendria que devolver el material de concursarte si existe sino retorna null//
   }
    public void setMaterialEducativo(Material nuevoMaterial) {
        //aqui tendria que setear el material de concursarte//
    }*/

}

