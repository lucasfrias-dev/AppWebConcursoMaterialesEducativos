package ar.edu.unnoba.appweb_concurso_materiales_educativos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="materiales")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Material implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material", nullable = false)
    private Long id;

    @Column(name = "titulo")
    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 100, message = "El título no puede tener más de 100 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(name = "descripcion")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;

    @NotBlank(message = "El tipo de material es obligatorio")
    @Column(name = "tipo_material")
    private String tipoMaterial;

    @NotBlank(message = "La disciplina es obligatoria")
    @Column(name = "disciplina")
    private String disciplina;

    @NotBlank(message = "Los autores son obligatorios")
    @Column(name = "autores")
    private String autores;

    @Column(name = "aprobado")
    private Boolean aprobado;

    @Column(name = "evaluado")
    private Boolean evaluado = false;

    @Column(name = "archivo")
    private String archivoPdf;

    @NotBlank(message = "El link del video de presentación es obligatorio")
    @Column(name = "url_video_presentacion")
    @URL(message = "El link del video de presentación debe ser una URL válida")
    private String urlVideoPresentacion;

    @Column(name = "likes")
    private int likes = 0;

    @Column(name = "ganador")
    private boolean ganador = false;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User concursante;

    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "evaluador_material",
        joinColumns = @JoinColumn(name = "material_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonManagedReference
    private Set<User> evaluadores = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concurso_id")
    private Concurso concurso;
}
