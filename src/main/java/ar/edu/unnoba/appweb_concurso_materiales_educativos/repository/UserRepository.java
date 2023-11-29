package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String username);

    @Query("SELECT u FROM User u WHERE u.rol = 'EVALUADOR'")
    List<User> findAllEvaluadores();

    @Query("SELECT u FROM User u JOIN u.materialesAEvaluar m LEFT JOIN m.evaluaciones e WHERE m = :material AND (e IS NULL OR e.evaluador != u)")
    List<User> findEvaluadoresPendientes(@Param("material") Material material);

    @Query("SELECT u FROM User u WHERE u.rol = 'ADMINISTRADOR'")
    List<User> findAllAdministradores();
}
