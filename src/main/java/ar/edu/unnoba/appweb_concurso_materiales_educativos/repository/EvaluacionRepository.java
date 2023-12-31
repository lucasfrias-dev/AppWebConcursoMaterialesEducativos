package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    /**
     * Busca una evaluación por un evaluador específico y un material específico.
     *
     * @param user     El evaluador para el cual se buscará la evaluación.
     * @param material El material para el cual se buscará la evaluación.
     * @return La evaluación asociada al evaluador y material proporcionados, o null si no se encuentra.
     */
    @Query("SELECT e FROM Evaluacion e WHERE e.evaluador = ?1 AND e.material = ?2")
    Evaluacion findByEvaluadorAndMaterial(User user, Material material);

}
