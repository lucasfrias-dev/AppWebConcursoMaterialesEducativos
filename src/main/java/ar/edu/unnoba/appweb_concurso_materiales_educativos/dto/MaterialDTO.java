package ar.edu.unnoba.appweb_concurso_materiales_educativos.dto;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class MaterialDTO implements Serializable {
    private Long id;
    private String titulo;
    private String descripcion;
    private String tipoMaterial;
    private String disciplina;
    private String urlVideoPresentacion;
    private String autores;
    private User concursante;

}

