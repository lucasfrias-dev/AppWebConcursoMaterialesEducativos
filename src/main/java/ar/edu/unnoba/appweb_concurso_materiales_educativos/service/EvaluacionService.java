package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;

public interface EvaluacionService {

    Evaluacion createEvaluacion(Evaluacion evaluacion, User evaluador, Material material);

    //obtener evaluacion de un usuario para un material
    Evaluacion getEvaluacionByUserAndMaterial(User user, Material material);
}
