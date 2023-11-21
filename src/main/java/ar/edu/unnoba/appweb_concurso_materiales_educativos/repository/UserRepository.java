package ar.edu.unnoba.appweb_concurso_materiales_educativos.repository;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    /*@Query
    User findOneByUsername(String username);*/
    User findByEmail(String username);
    User findUserById(Long id);

}
