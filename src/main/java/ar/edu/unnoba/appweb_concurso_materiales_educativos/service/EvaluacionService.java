package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;

import java.util.List;

public interface EvaluacionService {

    //Crea una nueva evaluación y la relaciona con un evaluador y un material.
    Evaluacion createEvaluacion(Evaluacion evaluacion, User evaluador, Material material);

    //Recupera una evaluación específica asociada a un usuario y a un material.
    Evaluacion getEvaluacionByUserAndMaterial(User user, Material material);

    List<Evaluacion> getEvaluacionesByMaterial(Material material);
}
