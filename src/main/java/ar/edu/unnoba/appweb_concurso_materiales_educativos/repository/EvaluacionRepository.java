package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    //obtener evaluacion de un usuario para un material
    @Query("SELECT e FROM Evaluacion e WHERE e.evaluador = ?1 AND e.material = ?2")
    Evaluacion findByEvaluadorAndMaterial(User user, Material material);
}
