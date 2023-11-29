package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MaterialRepository materialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("El usuario " + username + " no existe");
        }

        if (!user.isActive()) {
            throw new DisabledException("Esta cuenta ha sido desactivada");
        }

        return  user;
    }

    @Override
    public User createUser(User user, User.Rol rol) throws Exception {
        // Verifica si el email del usuario ya existe en la lista de usuarios
        if(!emailExists(user.getEmail())){
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword())); // Encripta la contraseña del usuario
            user.setRol(rol); //establece el rol
            // Guarda el usuario en la base de datos y obtén el usuario guardado
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("El usuario no existe"));
    }

    @Override
    public void updateUser(User updateUser, Long id) throws Exception{
        User userDB = userRepository.findById(id).get();

        if(!emailExists(updateUser.getEmail())){
            userDB.setEmail(updateUser.getEmail());
        }

        userDB.setNombre(updateUser.getNombre());
        userDB.setApellido(updateUser.getApellido());
        userRepository.save(userDB);
    }
    @Override
    public void asignarMaterialAEvaluador(Long materialId, Long evaluadorId) throws Exception{
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró material con id. " + materialId));
        User evaluador = userRepository.findById(evaluadorId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró evaluador con id. " + evaluadorId));

        if (material.getEvaluadores().contains(evaluador)) {
            throw new IllegalStateException("El material ya está asignado al evaluador");
        }

        material.getEvaluadores().add(evaluador);
        evaluador.getMaterialesAEvaluar().add(material);
        materialRepository.save(material);
        userRepository.save(evaluador);
    }

    private boolean emailExists(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        // Si el usuario existe, lanza una excepción
        if (user != null){
            throw new Exception("El email ya está registrado"); // Lanza una excepción
        }
        // Si el usuario no existe, devuelve false
        return false;
    }

    @Override
    public List<User> getAllEvaluadores(){
        return userRepository.findAllEvaluadores();
    }

    @Override
    public List<User> getAllAdministradores() {
        return userRepository.findAllAdministradores();
    }

    @Override
    public List<User> getEvaluadoresPendientes(Material material) {
        return userRepository.findEvaluadoresPendientes(material);
    }

    //TODO: Verificar si el usuario ya evaluó el material
    @Override
    public boolean haEvaluadoMaterial(User usuario, Material material) {
        return material.getEvaluaciones().stream()
                .anyMatch(evaluacion -> evaluacion.getEvaluador().equals(usuario));
    }

    @Override
    public void bajaUsuario(Long id) {
        User user = userRepository.findById(id).get();
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void altaUsuario(Long id) {
        User user = userRepository.findById(id).get();
        user.setActive(true);
        userRepository.save(user);
    }
}
