package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Crea un nuevo material y lo asocia con un usuario como concursante.
     *
     * @param material El material que se va a crear y postular.
     * @param user El usuario que está creando y postulando el material.
     * @return El material creado y postulado.
     */
    @Override
    public Material createMaterial(Material material, User user) {
        // Establece al usuario como concursante del material.
        material.setConcursante(user);

        // Agrega el material a la lista de materiales postulados por el usuario.
        user.getMaterialesPostulados().add(material);

        // Guarda el material en el repositorio de materiales.
        materialRepository.save(material);

        // Guarda el usuario en el repositorio de usuarios.
        userRepository.save(user);

        // Devuelve el material creado y postulado.
        return material;
    }

    /**
     * Recupera una lista de materiales asociados a un concursante específico.
     *
     * @param user El concursante para el cual se recuperarán los materiales.
     * @return Una lista de materiales asociados al concursante, o una lista vacía si no hay materiales.
     */
    @Override
    public List<Material> getMaterialesByConcursante(User user) {
        // Utiliza el método findByConcursante del repositorio de materiales
        // para recuperar la lista de materiales asociados al concursante.
        return materialRepository.findByConcursante(user);
    }

    /**
     * Recupera una lista de todos los materiales disponibles.
     *
     * @return Una lista de todos los materiales, o una lista vacía si no hay materiales.
     */
    @Override
    public List<Material> getMateriales() {
        // Utiliza el método findAll del repositorio de materiales
        // para recuperar la lista de todos los materiales.
        return materialRepository.findAll();
    }

    /**
     * Recupera una lista de materiales que han sido aprobados para participar.
     *
     * @return Una lista de materiales aprobados para participar, o una lista vacía si no hay materiales aprobados.
     */
    @Override
    public List<Material> getMaterialesParticipantes() {
        // Utiliza el método findMaterialsByAprobadoIsTrue del repositorio de materiales
        // para recuperar la lista de materiales que han sido aprobados para participar.
        return materialRepository.findMaterialsByAprobadoIsTrue();
    }

    /**
     * Recupera una lista de materiales que están pendientes de aprobación.
     *
     * @return Una lista de materiales pendientes de aprobación, o una lista vacía si no hay materiales pendientes.
     */
    @Override
    public List<Material> getMaterialesPendientesAprobacion() {
        // Utiliza el método findMaterialsByAprobadoIsNull del repositorio de materiales
        // para recuperar la lista de materiales que están pendientes de aprobación.
        return materialRepository.findMaterialsByAprobadoIsNull();
    }


    /**
     * Recupera una lista de materiales que han sido aprobados pero aún no han sido evaluados.
     *
     * @return Una lista de materiales pendientes de evaluación, o una lista vacía si no hay materiales pendientes.
     */
    @Override
    public List<Material> getMaterialesPendientesEvaluacion() {
        // Utiliza el método findMaterialsByEvaluadoIsFalseAndAprobadoIsTrue del repositorio de materiales
        // para recuperar la lista de materiales que han sido aprobados pero aún no han sido evaluados.
        return materialRepository.findMaterialsByEvaluadoIsFalseAndAprobadoIsTrue();
    }

    /**
     * Recupera un material específico por su identificación.
     *
     * @param id La identificación del material a recuperar.
     * @return El material asociado a la identificación proporcionada, o null si no se encuentra.
     */
    @Override
    public Material getMaterial(Long id) {
        // Utiliza el método findMaterialById del repositorio de materiales
        // para recuperar el material asociado a la identificación proporcionada.
        return materialRepository.findMaterialById(id);
    }

    /**
     * Recupera un conjunto de materiales asignados a un usuario específico.
     *
     * @param user El usuario para el cual se recuperarán los materiales asignados.
     * @return Un conjunto de materiales asignados al usuario.
     */
    @Override
    public Set<Material> getMaterialesAsignados(User user) {
        // Devuelve el conjunto de materiales asignados al usuario
        // accediendo a la propiedad materialesAEvaluar del usuario.
        return user.getMaterialesAEvaluar();
    }

    /**
     * Aprueba un material educativo por su identificación.
     *
     * @param id La identificación del material educativo a aprobar.
     * @throws IllegalArgumentException Si no se encuentra el material educativo con la identificación proporcionada.
     */
    @Override
    public void aprobarMaterial(Long id) {
        // Utiliza el repositorio de materiales para buscar el material por su identificación.
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material educativo no encontrado con id: " + id));

        // Establece el estado de aprobación del material como verdadero.
        material.setAprobado(true);

        // Guarda los cambios en el repositorio de materiales.
        materialRepository.save(material);
    }


    /**
     * Rechaza un material educativo por su identificación.
     *
     * @param id La identificación del material educativo a rechazar.
     * @throws IllegalArgumentException Si no se encuentra el material educativo con la identificación proporcionada.
     */
    @Override
    public void rechazarMaterial(Long id) {
        // Utiliza el repositorio de materiales para buscar el material por su identificación.
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material educativo no encontrado con id: " + id));

        // Establece el estado de aprobación del material como falso.
        material.setAprobado(false);

        // Guarda los cambios en el repositorio de materiales.
        materialRepository.save(material);
    }


    /**
     * Actualiza el estado de evaluación de un material educativo.
     *
     * @param material El material educativo cuyo estado de evaluación se actualizará.
     */
    @Override
    public void updateEvaluado(Material material) {
        // Establece el estado de evaluación del material basándose en si el número de evaluaciones es igual al número de evaluadores.
        material.setEvaluado(material.getEvaluaciones().size() == material.getEvaluadores().size());

        // Guarda los cambios en el repositorio de materiales.
        materialRepository.save(material);
    }

}
