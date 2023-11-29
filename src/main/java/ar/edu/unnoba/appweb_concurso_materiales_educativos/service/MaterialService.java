package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;

import java.util.List;
import java.util.Set;

public interface MaterialService {

    Material createMaterial(Material material, User user);


    List<Material> getMaterialesByConcursante(User user);

    void rechazarMaterial(Long id);

    void aprobarMaterial(Long id);

    List<Material> getMateriales();

    List<Material> getMaterialesParticipantes();

    List<Material> getMaterialesPendientesAprobacion();

    List<Material> getMaterialesPendientesEvaluacion();

    Material getMaterial(Long id);

    Set<Material> getMaterialesAsignados(User user);

    //Un material esta evaluado si tiene tantas evaluaciones como evaluadores
    void estaEvaluado(Material material);
}
