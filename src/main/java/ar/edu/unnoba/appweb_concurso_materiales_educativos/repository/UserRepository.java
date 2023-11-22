package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    /*@Query
    User findOneByUsername(String username);*/
    User findByEmail(String username);
    User findUserById(Long id);
    @Query("SELECT u FROM User u WHERE u.rol = 'EVALUADOR'")
    List<User> findAllEvaluadores();

}
