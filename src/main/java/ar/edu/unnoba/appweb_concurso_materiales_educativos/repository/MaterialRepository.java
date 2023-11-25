package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findAll();

    Material findMaterialById(Long id);
    List<Material> findMaterialsByAprobadoIsTrue();

    List<Material> findMaterialsByEvaluadoIsFalseAndAprobadoIsTrue();

    List<Material> findByConcursante(User user);

    List<Material> findMaterialsByAprobadoIsNull();
}
