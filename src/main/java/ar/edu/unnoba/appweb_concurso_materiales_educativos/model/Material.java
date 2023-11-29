package ar.edu.unnoba.appweb_concurso_materiales_educativos.model;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="materiales")
public class Material implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material", nullable = false)
    private Long id;

    @Column(name = "titulo")
    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(name = "descripcion")
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

    @Column(name = "likes")
    private int likes = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private User concursante;

    @ManyToMany(mappedBy = "materialesAEvaluar", fetch = FetchType.EAGER)
    private Set<User> evaluadores = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "material", fetch = FetchType.EAGER)
    private Set<Evaluacion> evaluaciones = new HashSet<>();

}
