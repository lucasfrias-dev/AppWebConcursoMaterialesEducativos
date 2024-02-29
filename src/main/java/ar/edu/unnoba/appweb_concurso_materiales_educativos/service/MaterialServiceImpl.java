package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.dto.MaterialDTO;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.ConcursoRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.EvaluacionRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private ConcursoRepository concursoRepository;
    @Autowired
    private EvaluacionRepository evaluacionRepository;

    /**
     * Crea un nuevo material y lo asocia con un usuario y un concurso.
     *
     * @param material El material que se va a crear y asociar.
     * @param user     El usuario que será el concursante del material.
     * @param concurso El concurso al que pertenecerá el material.
     * @return El material creado y asociado.
     */
    @Override
    @Transactional
    public Material createMaterial(Material material, User user, Concurso concurso) {
        // Establece al usuario como concursante del material.
        material.setConcursante(user);
        material.setConcurso(concurso);

        /*concurso.getMateriales().add(material);
        concursoRepository.save(concurso);*/

        /*// Agrega el material a la lista de materiales postulados por el usuario.
        user.getMaterialesPostulados().add(material);*/

        /*// Guarda el usuario en el repositorio de usuarios.
        userRepository.save(user);*/

        //Guarda el material en el repositorio de materiales y lo retorna.
        return materialRepository.save(material);
    }

    @Transactional
    @Override
    public void darLikeMaterial(Material material){
        // Dar Like al material
        material.setLikes(material.getLikes() + 1);
        // Guardar modificacion del material
        materialRepository.save(material);
    }

    /**
     * Obtiene una lista de materiales asociados a un concurso específico.
     *
     * @param concurso El concurso del cual se desean recuperar los materiales.
     * @return Una lista de materiales asociados al concurso especificado.
     */
    @Override
    public List<Material> getMaterialesByConcurso(Concurso concurso) {
        return materialRepository.findAllByConcurso(concurso);
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
        return materialRepository.findAllByConcursante(user);
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
     * Obtiene una lista de materiales participantes en un concurso específico.
     *
     * @param concurso El concurso del cual se desean recuperar los materiales participantes.
     * @return Una lista de materiales participantes asociados al concurso especificado.
     */
    @Transactional
    @Override
    public List<Material> getMaterialesParticipantesByConcurso(Concurso concurso) {

        // Obtiene todos los materiales aprobados en general.
        List<Material> materialesAprobados = materialRepository.findMaterialsByAprobadoIsTrue();

        // Obtiene todos los materiales asociados al concurso específico.
        List<Material> materialesConcurso = getMaterialesByConcurso(concurso);

        // Filtra los materiales aprobados para mantener solo los que están asociados al concurso.
        materialesAprobados.removeIf(material -> !materialesConcurso.contains(material));

        return materialesAprobados;
    }


    /**
     * Obtiene una lista de materiales participantes en el concurso actual.
     *
     * @return Una lista de materiales participantes en el concurso actual.
     */
    @Override
    public List<Material> getMaterialesParticipantes() {
        // Obtiene todos los materiales aprobados en general.
        List<Material> materialesAprobados = materialRepository.findMaterialsByAprobadoIsTrue();

        // Obtiene el concurso actual.
        Concurso concurso = concursoRepository.findCurrentConcurso(LocalDateTime.now());

        // Obtiene todos los materiales asociados al concurso actual.
        List<Material> materialesConcursoActual = getMaterialesByConcurso(concurso);

        // Filtra los materiales aprobados para mantener solo los asociados al concurso actual.
        materialesAprobados.removeIf(material -> !materialesConcursoActual.contains(material));

        return materialesAprobados;
    }


    /**
     * Obtiene una lista de materiales pendientes de aprobación para el concurso actual.
     *
     * @return Una lista de objetos MaterialDTO que representan los materiales pendientes de aprobación.
     */
    @Override
    public List<MaterialDTO> getMaterialesPendientesAprobacion() {
        // Obtiene el concurso actual.
        Concurso concurso = concursoRepository.findCurrentConcurso(LocalDateTime.now());

        // Obtiene una lista de materiales sin aprobar asociados al concurso actual.
        List<Material> materialesSinAprobar = materialRepository.findMaterialsByAprobadoIsNullAndConcurso(concurso);

        // Crea una lista para almacenar los materiales DTO.
        List<MaterialDTO> materialesDTO = new ArrayList<>();

        // Itera sobre los materiales sin aprobar para crear los objetos MaterialDTO correspondientes.
        for (Material material : materialesSinAprobar) {
            MaterialDTO dto = new MaterialDTO();
            dto.setId(material.getId());
            dto.setTitulo(material.getTitulo());
            dto.setDescripcion(material.getDescripcion());
            dto.setDisciplina(material.getDisciplina());
            dto.setTipoMaterial(material.getTipoMaterial());
            dto.setUrlVideoPresentacion(material.getUrlVideoPresentacion());
            dto.setConcursante(material.getConcursante());
            dto.setAutores(material.getAutores());

            materialesDTO.add(dto);
        }

        return materialesDTO;
    }



    /**
     * Obtiene una lista de materiales pendientes de evaluación.
     *
     * @return Una lista de materiales pendientes de evaluación.
     */
    @Override
    public List<Material> getMaterialesPendientesEvaluacion() {
        // Obtiene el concurso actual.
        Concurso concurso = concursoRepository.findCurrentConcurso(LocalDateTime.now());

        // Busca los materiales pendientes de evaluación para el concurso actual.
        return materialRepository.findMaterialsByEvaluadoIsFalseAndAprobadoIsTrueAndConcurso(concurso);
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
    @Transactional
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
    @Transactional
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
     * @param materialId El identificador del material educativo cuyo estado de evaluación se actualizará.
     */
    @Override
    @Transactional
    public void updateEvaluado(Long materialId) {
        // Carga el material desde la base de datos.
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        // Inicializa el contador de evaluaciones del material.
        int totalEvaluacionesMaterial = 0;
        // Recorre cada evaluador del material.
        for (User evaluador : material.getEvaluadores()){
            // Busca si el evaluador ha realizado una evaluación para este material.
            Evaluacion evaluacion = evaluacionRepository.findByEvaluadorAndMaterial(evaluador, material);
            // Si se encuentra una evaluación para el evaluador actual, aumenta el contador de evaluaciones.
            if (evaluacion != null) {
                totalEvaluacionesMaterial += 1;
            }
        }
        // Obtiene el total de evaluadores asignados al material.
        int totalEvaluadoresMaterial = material.getEvaluadores().size();

        // Establece el estado de evaluación del material basándose en si el número de evaluaciones es igual al número de evaluadores.
        material.setEvaluado(totalEvaluacionesMaterial == totalEvaluadoresMaterial);

        // Guarda los cambios en el repositorio de materiales.
        materialRepository.save(material);
    }

    /**
     * Agrega un material a la lista de materiales ganadores de un concurso y guarda los cambios en la base de datos.
     *
     * @param concurso El concurso al que se agregarán los materiales ganadores.
     * @param material El material que se agregará a la lista de ganadores del concurso.
     */
    public void setMaterialGanador(Concurso concurso, Material material){
        // Agrega el material proporcionado a la lista de materiales ganadores del concurso
        Set<Material> concurso1=concurso.getMaterialesGanadores();
        concurso1.add(material);
        concurso.setMaterialesGanadores(concurso1);
        // Guarda el concurso actualizado en la base de datos
        concursoRepository.save(concurso);
    }

}
