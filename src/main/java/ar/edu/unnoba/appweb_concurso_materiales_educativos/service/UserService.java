package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService{

    User createUser(User user, User.Rol rol) throws Exception;

    public List<User> getAllUsers();

    User findById(Long id);

    User authenticate(String email, String password) throws Exception;

    void updateUser(User user, Long id);
}
