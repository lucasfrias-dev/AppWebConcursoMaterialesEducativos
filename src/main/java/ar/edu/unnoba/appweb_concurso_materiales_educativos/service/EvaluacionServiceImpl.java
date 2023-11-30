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


    /**
     * Crea una nueva evaluación y la relaciona con un evaluador y un material.
     *
     * @param evaluacion La evaluación que se va a crear.
     * @param evaluador El usuario que realiza la evaluación.
     * @param material El material que está siendo evaluado.
     * @return La evaluación creada.
     */
    @Override
    public Evaluacion createEvaluacion(Evaluacion evaluacion, User evaluador, Material material) {
        // Establece el evaluador y el material para la evaluación.
        evaluacion.setEvaluador(evaluador);
        evaluacion.setMaterial(material);

        // Agrega la evaluación al conjunto de evaluaciones del material.
        material.getEvaluaciones().add(evaluacion);

        // Agrega la evaluación al conjunto de evaluaciones realizadas por el evaluador.
        evaluador.getEvaluacionesRealizadas().add(evaluacion);

        // Guarda la evaluación en el repositorio de evaluaciones.
        evaluacionRepository.save(evaluacion);

        // Guarda el material en el repositorio de materiales.
        materialRepository.save(material);

        // Guarda el evaluador en el repositorio de usuarios.
        userRepository.save(evaluador);

        // Devuelve la evaluación creada.
        return evaluacion;
    }

    /**
     * Recupera una evaluación específica asociada a un usuario y a un material.
     *
     * @param user El usuario asociado a la evaluación.
     * @param material El material asociado a la evaluación.
     * @return La evaluación asociada al usuario y al material, o null si no se encuentra.
     */
    @Override
    public Evaluacion getEvaluacionByUserAndMaterial(User user, Material material) {
        // Utiliza el método findByEvaluadorAndMaterial del repositorio de evaluaciones
        // para recuperar la evaluación asociada al usuario y al material.
        return evaluacionRepository.findByEvaluadorAndMaterial(user, material);
    }
}
