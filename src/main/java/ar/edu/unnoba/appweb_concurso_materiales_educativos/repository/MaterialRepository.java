package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
     * Método de consulta para obtener una lista de materiales que no han sido evaluados pero han sido aprobados.
     *
     * @return Una lista de materiales que cumplen con los criterios de no evaluado y aprobado.
     */
    List<Material> findMaterialsByEvaluadoIsFalseAndAprobadoIsTrue();


    /**
     * Método de consulta para obtener una lista de materiales asociados a un concursante específico.
     *
     * @param user El concursante cuyos materiales se desean recuperar.
     * @return Una lista de materiales asociados al concursante especificado.
     */
    List<Material> findByConcursante(User user);


    /**
     * Método de consulta para obtener una lista de materiales que aún no han sido aprobados ni rechazados.
     *
     * @return Una lista de materiales que no tienen la propiedad "aprobado" establecida.
     */
    List<Material> findMaterialsByAprobadoIsNull();

}
