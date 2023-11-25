package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.UserRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService{


    List<User> getAllEvaluadores();

    List<User> getAllAdministradores();

    /*List<User> getAdministrador();*/
//Guardar usuario modificado
    void save(User user);

    User createUser(User user, User.Rol rol) throws Exception;

    User findById(Long id);

    void updateUser(User user, Long id) throws Exception;

    void asignarMaterialAEvaluador(Long materialId, Long evaluadorId) throws Exception;

    public List<User> getEvaluadoresPendientes(Material material);


}
