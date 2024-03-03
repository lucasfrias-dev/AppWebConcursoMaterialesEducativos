package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.EvaluacionRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final EvaluacionRepository evaluacionRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MaterialRepository materialRepository, EvaluacionRepository evaluacionRepository) {
        this.userRepository = userRepository;
        this.materialRepository = materialRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    /**
     * Carga detalles de un usuario basado en su nombre de usuario (correo electrónico).
     *
     * @param username El nombre de usuario (correo electrónico) del usuario a cargar.
     * @return Detalles del usuario, que incluyen roles y permisos.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el nombre de usuario proporcionado.
     * @throws DisabledException Si la cuenta del usuario está desactivada.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca al usuario por su correo electrónico en el repositorio de usuarios.
        User user = userRepository.findByEmail(username);

        // Si el usuario no se encuentra, lanza una excepción UsernameNotFoundException.
        if(user == null){
            throw new UsernameNotFoundException("El usuario " + username + " no existe");
        }

        // Si la cuenta del usuario no está activa, lanza una excepción DisabledException.
        if (!user.isActive()) {
            throw new DisabledException("Esta cuenta ha sido desactivada");
        }

        // Devuelve los detalles del usuario, que incluyen roles y permisos.
        return  user;
    }


    /**
     * Crea un nuevo usuario con el rol especificado.
     *
     * @param user El nuevo usuario que se va a crear.
     * @param rol El rol que se asignará al nuevo usuario.
     * @return El usuario creado.
     * @throws Exception Si el correo electrónico del usuario ya existe en la base de datos.
     */
    @Override
    @Transactional
    public User createUser(User user, User.Rol rol) throws Exception {
        // Verifica si el correo electrónico del usuario ya existe en la base de datos.
        // si no existe, crea el usuario
        if (!emailExists(user.getEmail())) {
            // Encripta la contraseña del usuario usando BCrypt.
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

            // Establece el rol del usuario.
            user.setRol(rol);

            // Guarda el usuario en la base de datos y obtiene el usuario guardado.
            user = userRepository.save(user);
        } else {
            // Si el correo electrónico ya existe, lanza una excepción.
            throw new Exception("El correo electrónico del usuario ya existe en la base de datos.");
        }

        // Devuelve el usuario creado.
        return user;
    }

    /**
     * Verifica si un correo electrónico ya está registrado en el sistema.
     *
     * @param email El correo electrónico a verificar.
     * @return true si el correo electrónico ya está registrado, false si no.
     * @throws Exception Si el correo electrónico ya está registrado, se lanza una excepción.
     */
    private boolean emailExists(String email) throws Exception {
        // Busca un usuario por su correo electrónico en el repositorio de usuarios.
        User user = userRepository.findByEmail(email);

        // Si el usuario existe, lanza una excepción indicando que el correo electrónico ya está registrado.
        if (user != null) {
            throw new Exception("El correo electrónico ya está registrado");
        }

        // Si el usuario no existe, devuelve false, indicando que el correo electrónico no está registrado.
        return false;
    }


    /**
     * Recupera un usuario por su identificación.
     *
     * @param id La identificación del usuario a recuperar.
     * @return El usuario asociado a la identificación proporcionada.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con la identificación proporcionada.
     */
    @Override
    public User findById(Long id) {
        // Utiliza el repositorio de usuarios para buscar el usuario por su identificación.
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe"));
    }

    /**
     * Asigna un material a un evaluador específico.
     *
     * @param materialId La identificación del material a asignar.
     * @param evaluadorId La identificación del evaluador al cual asignar el material.
     */
    @Override
    @Transactional
    public void asignarMaterialAEvaluador(Long materialId, Long evaluadorId) {
        // Busca el material por su identificación en el repositorio de materiales.
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró material con id. " + materialId));

        // Busca el evaluador por su identificación en el repositorio de usuarios.
        User evaluador = userRepository.findById(evaluadorId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró evaluador con id. " + evaluadorId));

        // Verifica si el material ya está asignado al evaluador.
        if (material.getEvaluadores().contains(evaluador)) {
            throw new IllegalStateException("El material ya está asignado al evaluador");
        }

        // Asigna el material al evaluador y viceversa.
        material.getEvaluadores().add(evaluador);
        evaluador.getMaterialesAEvaluar().add(material);

        // Guarda los cambios en el repositorio de materiales y usuarios.
        materialRepository.save(material);
        userRepository.save(evaluador);
    }

    /**
     * Recupera una lista de todos los evaluadores registrados en el sistema.
     *
     * @return Una lista de usuarios con el rol de evaluador.
     */
    @Override
    public List<User> getAllEvaluadores() {
        // Utiliza el método findAllEvaluadores del repositorio de usuarios
        // para recuperar la lista de todos los usuarios con el rol de evaluador.
        return userRepository.findAllEvaluadores();
    }

    /**
     * Recupera una lista de todos los administradores registrados en el sistema.
     *
     * @return Una lista de usuarios con el rol de administrador.
     */
    @Override
    public List<User> getAllAdministradores() {
        // Utiliza el método findAllAdministradores del repositorio de usuarios
        // para recuperar la lista de todos los usuarios con el rol de administrador.
        return userRepository.findAllAdministradores();
    }

    /**
     * Recupera una lista de evaluadores pendientes para un material específico.
     *
     * @param material El material para el cual se recuperarán los evaluadores pendientes.
     * @return Una lista de usuarios que son evaluadores pendientes para el material.
     */
    @Override
    public List<User> getEvaluadoresPendientes(Material material) {
        // Utiliza el método findEvaluadoresPendientes del repositorio de usuarios
        // para recuperar la lista de usuarios que son evaluadores pendientes para el material.
        return userRepository.findEvaluadoresPendientesByMaterial(material);
    }


    /**
     * Verifica si un usuario ha evaluado un material específico.
     *
     * @param usuario El usuario cuya evaluación se verificará.
     * @param material El material para el cual se verificará si el usuario lo ha evaluado.
     * @return true si el usuario ha evaluado el material, false si no.
     */
    @Override
    public boolean haEvaluadoMaterial(User usuario, Material material) {
        Evaluacion evaluacion = evaluacionRepository.findByEvaluadorAndMaterial(usuario, material);
        return evaluacion != null;
    }

    /**
     * Desactiva un usuario en el sistema.
     *
     * @param id La identificación del usuario a desactivar.
     */
    @Override
    @Transactional
    public void bajaUsuario(Long id) {
        // Busca el usuario por su identificación en el repositorio de usuarios.
        User user = userRepository.findById(id).get();

        // Verifica si el usuario es un evaluador o un concursante.
        if (user.getRol() != User.Rol.EVALUADOR && user.getRol() != User.Rol.CONCURSANTE) {
            throw new IllegalArgumentException("Solo los usuarios que son evaluadores o concursantes pueden ser dados de baja.");
        }

        // Establece el estado activo del usuario como falso.
        user.setActive(false);

        // Guarda los cambios en el repositorio de usuarios.
        userRepository.save(user);
    }


    /**
     * Activa un usuario en el sistema.
     *
     * @param id La identificación del usuario a activar.
     */
    @Override
    @Transactional
    public void altaUsuario(Long id) {
        // Busca el usuario por su identificación en el repositorio de usuarios.
        User user = userRepository.findById(id).get();

        // Establece el estado activo del usuario como verdadero.
        user.setActive(true);

        // Guarda los cambios en el repositorio de usuarios.
        userRepository.save(user);
    }

    /**
     * Recupera una lista de todos los usuarios registrados en el sistema.
     *
     * @return Una lista que contiene todos los usuarios.
     */
    @Override
    public List<User> findAll() {
        // Utiliza el método findAll del repositorio de usuarios
        // para recuperar la lista de todos los usuarios registrados.
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario con la colección 'materialesAEvaluar' inicializada.
     *
     * @param userId El identificador del usuario.
     * @return El usuario con la colección 'materialesAEvaluar' inicializada.
     */
    @Override
    @Transactional
    public User getUsuarioConMaterialesAsignados(Long userId) {
        // Busca al usuario en la base de datos.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Inicializa la colección 'materialesAEvaluar'.
        user.getMaterialesAEvaluar().size();

        // Retorna el usuario con la colección 'materialesAEvaluar' inicializada.
        return user;
    }

}
