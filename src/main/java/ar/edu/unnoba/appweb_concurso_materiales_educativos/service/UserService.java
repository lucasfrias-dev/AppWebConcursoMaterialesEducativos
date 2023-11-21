package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService{
    /*List<User> getEvaluador();
    List<User> getAdministrador();*/

    User createUser(User user, User.Rol rol) throws Exception;

    public List<User> getAllUsers();

    User findById(Long id);

    void updateUser(User user, Long id);
}
