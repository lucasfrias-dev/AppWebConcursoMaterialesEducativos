package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    /**
     * Método de consulta para encontrar un material por su identificador (ID).
     *
     * @param id El identificador del material que se desea buscar.
     * @return El material con el identificador especificado o null si no se encuentra.
     */
    Material findMaterialById(Long id);

    /**
     * Método de consulta para obtener una lista de materiales aprobados.
     *
     * @return Una lista de materiales que han sido aprobados.
     */
    List<Material> findMaterialsByAprobadoIsTrue();

    /**
     * Busca todos los materiales que no han sido evaluados, han sido aprobados y pertenecen a un concurso específico.
     *
     * @param concurso El concurso al que pertenecen los materiales.
     * @return Una lista de materiales que cumplen con los criterios de no evaluados, aprobados y pertenecientes al concurso especificado.
     */
    @Query("SELECT m FROM Material m LEFT JOIN FETCH m.evaluadores WHERE m.evaluado = FALSE AND m.aprobado = TRUE AND m.concurso = :concurso")
    List<Material> findMaterialsByEvaluadoIsFalseAndAprobadoIsTrueAndConcurso(Concurso concurso);

    /**
     * Busca todos los materiales que aún no han sido aprobados y pertenecen a un concurso específico.
     *
     * @param concurso El concurso al que pertenecen los materiales.
     * @return Una lista de materiales que aún no han sido aprobados y pertenecen al concurso especificado.
     */
    @Query("SELECT m FROM Material m WHERE m.aprobado IS NULL AND m.concurso = :concurso")
    List<Material> findMaterialsByAprobadoIsNullAndConcurso(Concurso concurso);

    /**
     * Método de consulta para obtener una lista de materiales asociados a un concursante específico.
     *
     * @param user El concursante cuyos materiales se desean recuperar.
     * @return Una lista de materiales asociados al concursante especificado.
     */
    @Query("SELECT m FROM Material m WHERE m.concursante = :user")
    List<Material> findAllByConcursante(User user);

    /**
     * Método de consulta para obtener una lista de materiales asociados a un concurso específico.
     *
     * @param concurso El concurso cuyos materiales se desean recuperar.
     * @return Una lista de materiales asociados al concurso especificado.
     */
    @Query("SELECT m FROM Material m LEFT JOIN FETCH m.evaluadores WHERE m.concurso = :concurso")
    List<Material> findAllByConcurso(Concurso concurso);
    
    /**
     * Método de consulta para obtener una lista de materiales ganadores asociados a un concurso específico.
     *
     * @param concurso El concurso cuyos materiales ganadores se desean recuperar.
     * @return Una lista de materiales ganadores asociados al concurso especificado.
     */
    @Query("SELECT m FROM Material m WHERE m.ganador = TRUE AND m.concurso = :concurso")
    List<Material> findMaterialesGanadoresByConcurso(Concurso concurso);


    /**
     * Consulta para obtener los materiales pendientes de evaluación para un evaluador específico.
     *
     * @param userId El identificador del evaluador.
     * @return Una lista de materiales pendientes de evaluación para el evaluador.
     */
    @Query("SELECT m FROM Material m" +  // Selecciona los materiales
            " JOIN FETCH m.evaluadores e" +  // Realiza una unión para cargar anticipadamente los evaluadores asociados a los materiales
            " JOIN FETCH m.concurso" +  // Realiza una unión para cargar anticipadamente los concursos asociados a los materiales
            " WHERE e.id = :userId" +  // Filtra los materiales por el ID del evaluador
            " AND m.id NOT IN (" +  // Excluye los materiales que ya han sido evaluados por el evaluador
            "    SELECT ev.material.id FROM Evaluacion ev WHERE ev.evaluador.id = :userId" +  // Subconsulta para obtener los materiales ya evaluados por el evaluador
            " )")
    List<Material> findMaterialesPendientesEvaluadorById(@Param("userId") Long userId);
}
