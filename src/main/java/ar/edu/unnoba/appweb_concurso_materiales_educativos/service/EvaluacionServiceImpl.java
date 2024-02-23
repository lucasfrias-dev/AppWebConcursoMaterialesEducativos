package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.EvaluacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EvaluacionServiceImpl implements EvaluacionService{

    @Autowired
    private EvaluacionRepository evaluacionRepository;


    /**
     * Crea una nueva evaluación y la asocia con un evaluador y un material específicos.
     *
     * @param evaluacionData El objeto Evaluacion que contiene los datos de la nueva evaluación.
     * @param evaluador      El usuario que realiza la evaluación.
     * @param material       El material que se está evaluando.
     * @return La evaluación creada.
     */
    @Override
    @Transactional
    public Evaluacion createEvaluacion(Evaluacion evaluacionData, User evaluador, Material material) {
        // Crea una nueva instancia de Evaluacion.
        Evaluacion evaluacion = new Evaluacion();

        // Copia los datos de la evaluación desde el objeto evaluacionData.
        evaluacion.setComentario(evaluacionData.getComentario());
        evaluacion.setNota(evaluacionData.getNota());

        // Establece el evaluador y el material para la evaluación.
        evaluacion.setEvaluador(evaluador);
        evaluacion.setMaterial(material);

        // Guarda la evaluación en el repositorio de evaluaciones y la devuelve.
        return evaluacionRepository.save(evaluacion);
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

    /**
     * Obtiene todas las evaluaciones asociadas a un material específico.
     *
     * @param material El material del cual se desean obtener las evaluaciones.
     * @return Una lista de evaluaciones asociadas al material especificado.
     */
    @Override
    public List<Evaluacion> getEvaluacionesByMaterial(Material material) {
        return evaluacionRepository.findAllByMaterial(material);
    }


}
