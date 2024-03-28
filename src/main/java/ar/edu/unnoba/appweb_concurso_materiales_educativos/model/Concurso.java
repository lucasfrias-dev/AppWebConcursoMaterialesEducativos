package ar.edu.unnoba.appweb_concurso_materiales_educativos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "concursos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @Column(name = "fecha_fin_postulaciones")
    private LocalDateTime fechaFinPostulaciones;

    @NotNull
    @Column(name = "fecha_inicio_votacion")
    private LocalDateTime fechaInicioVotacion;

    @NotNull
    @Column(name = "fecha_fin_votacion")
    private LocalDateTime fechaFinVotacion;

    @NotNull
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
}
