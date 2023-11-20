package ar.edu.unnoba.appweb_concurso_materiales_educativos.model;

import jakarta.persistence.*;

@Entity
public class Evaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion", nullable = false)
    private Long id;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "nota")
    private int nota;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User evaluador;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;
}
