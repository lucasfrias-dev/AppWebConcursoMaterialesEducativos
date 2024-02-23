package ar.edu.unnoba.appweb_concurso_materiales_educativos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "concursos")
public class Concurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_concurso", nullable = false)
    private Long id;

    @Column(name = "edicion")
    private String edicion;

    @NotNull
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @NotNull
    @FutureOrPresent
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    /*@OneToMany(mappedBy = "concurso", fetch = FetchType.LAZY)
    private Set<Material> materiales = new HashSet<>();*/

    @OneToOne
    @JoinColumn(name = "material_ganador_id")
    private Material materialGanador;
}
