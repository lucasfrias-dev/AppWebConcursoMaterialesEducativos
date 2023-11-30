package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Método de consulta para encontrar un usuario por su dirección de correo electrónico.
     *
     * @param username La dirección de correo electrónico del usuario que se desea buscar.
     * @return El usuario con la dirección de correo electrónico especificada o null si no se encuentra.
     */
    User findByEmail(String username);


    /**
     * Método de consulta para obtener una lista de todos los usuarios con el rol de "EVALUADOR".
     *
     * @return Una lista de usuarios que tienen el rol de "EVALUADOR".
     */
    @Query("SELECT u FROM User u WHERE u.rol = 'EVALUADOR'")
    List<User> findAllEvaluadores();

    /**
     * Método de consulta para obtener una lista de todos los usuarios con el rol de "ADMINISTRADOR".
     *
     * @return Una lista de usuarios que tienen el rol de "ADMINISTRADOR".
     */
    @Query("SELECT u FROM User u WHERE u.rol = 'ADMINISTRADOR'")
    List<User> findAllAdministradores();


    /**
     * Busca evaluadores pendientes para un material específico.
     *
     * @param material El material para el cual se buscarán evaluadores pendientes.
     * @return Una lista de usuarios (evaluadores) pendientes para el material.
     */
    @Query("SELECT u FROM User u JOIN u.materialesAEvaluar m LEFT JOIN m.evaluaciones e WHERE m = :material AND (e IS NULL OR e.evaluador != u)")
    List<User> findEvaluadoresPendientes(@Param("material") Material material);

}
