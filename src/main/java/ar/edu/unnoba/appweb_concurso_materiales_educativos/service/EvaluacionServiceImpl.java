package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.EvaluacionRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluacionServiceImpl implements EvaluacionService{

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Evaluacion createEvaluacion(Evaluacion evaluacion, User evaluador, Material material) {
        evaluacion.setEvaluador(evaluador);
        evaluacion.setMaterial(material);
        material.getEvaluaciones().add(evaluacion);
        evaluador.getEvaluacionesRealizadas().add(evaluacion);
        evaluacionRepository.save(evaluacion);
        materialRepository.save(material);
        userRepository.save(evaluador);
        return evaluacion;
    }

    //obtener evaluacion de un usuario para un material
    @Override
    public Evaluacion getEvaluacionByUserAndMaterial(User user, Material material) {
        return evaluacionRepository.findByEvaluadorAndMaterial(user, material);
    }
}
