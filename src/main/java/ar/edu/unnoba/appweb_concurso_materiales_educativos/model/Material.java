package ar.edu.unnoba.appweb_concurso_materiales_educativos.model;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "La descripcion es obligatoria")
    @Column(name = "descripcion")
    private String descripcion;

    @NotBlank(message = "El tipo de material es obligatorio")
    @Column(name = "tipo_material")
    private String tipoMaterial;

    @NotBlank(message = "La disciplina es obligatoria")
    @Column(name = "disciplina")
    private String disciplina;

    @NotBlank(message = "El link es obligatorio")
    @Column(name = "autores")
    private String autores;

    @Column(name = "aprobado")
    private Boolean aprobado;

    /*private String estado;*/ //Coloque esta variable para saver el estado actual de material de manera mas simple//

    @Column(name = "evaluado")
    private Boolean evaluado;

    @Column(name = "likes")
    private int likes = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User concursante;

    @ManyToMany(mappedBy = "materialesAEvaluar")
    private Set<User> evaluadores = new HashSet<>();

    @OneToMany(mappedBy = "material")
    private Set<Evaluacion> evaluaciones = new HashSet<>();


    /*public void setEnRevision(){
        this.setEstado("Revision");
    }
    public void setAprobado(){
        this.setEstado("Aprobado");
    }
    public void setRechazado(){
        this.setEstado("Rechazado");
    }*/

}
