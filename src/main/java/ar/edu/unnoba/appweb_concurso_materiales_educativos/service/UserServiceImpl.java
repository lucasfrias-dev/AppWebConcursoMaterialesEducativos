package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("El usuario " + username + " no existe");
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
    public void updateUser(User user, Long id) {
        User userDB = userRepository.findById(id).get();
        userDB.setNombre(user.getUsername());
        userDB.setApellido(user.getUsername());
        userDB.setEmail(user.getEmail());
        userRepository.save(userDB);
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

    public List<User> allEvaluador(){
        return userRepository.findAllEvaluador();
    }

    public void save(User user) {
        userRepository.save(user);
    }
    //Da todos los usuarios evaluadores//
   /* public List<User> getEvaluador() {
        List<User> usuario=new ArrayList<User>();
        for (User User : this.userRepository.findAll()){
            if(User.getRol().toString().equals("EVALUADOR")){
                usuario.add(User);
            }
        }
        return usuario;
    }
//Da todos los usuarios administradores//
    @Override
    public List<User> getAdministrador() {
        List<User> usuario=new ArrayList<User>();
        for (User User : this.userRepository.findAll()){
            if(User.getTipo().equals("Administrador")){
                usuario.add(User);
            }
        }
        return usuario;
    }*/
}
