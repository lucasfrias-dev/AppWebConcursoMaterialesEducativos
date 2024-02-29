package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.dto.MaterialDTO;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;

import java.util.List;
import java.util.Set;

public interface MaterialService {

    // Crea un nuevo material y lo asocia con un usuario como concursante.
    Material createMaterial(Material material, User user, Concurso concurso);

    List<Material> getMaterialesByConcurso(Concurso concurso);

    // Recupera una lista de materiales asociados a un concursante específico.
    List<Material> getMaterialesByConcursante(User user);

    // Rechaza un material educativo por su identificación.
    void rechazarMaterial(Long id);

    // Aprueba un material educativo por su identificación.
    void aprobarMaterial(Long id);

    // Recupera una lista de todos los materiales en el sistema
    List<Material> getMateriales();

    List<Material> getMaterialesParticipantesByConcurso(Concurso concurso);

    // Recupera una lista de materiales que han sido aprobados para participar.
    List<Material> getMaterialesParticipantes();

    // Recupera una lista de materiales que están pendientes de aprobación.
    List<MaterialDTO> getMaterialesPendientesAprobacion();

    // Recupera una lista de materiales que han sido aprobados pero aún no han sido evaluados.
    List<Material> getMaterialesPendientesEvaluacion();

    // Recupera un material específico por su identificación.
    Material getMaterial(Long id);

    // Recupera un conjunto de materiales asignados a un usuario específico.
    Set<Material> getMaterialesAsignados(User user);

    // Actualiza el estado de evaluación de un material educativo.
    void updateEvaluado(Long materialId);
    void darLikeMaterial(Material material);
    public void setMaterialGanador(Concurso concurso, Material material);
}
