package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;

import java.util.List;

public interface MaterialService {

    void createMaterial(Material material, User user);

    List<Material> getMaterialesByConcursante(User user);
    void updateRechazado(Long id);
    void updateAprobado(Long id);

    List<Material> getMateriales();
    List<Material> getMaterialesParticipantes();

    List<Material> getMaterialesPendientes();

    Material getMaterial(Long id);
}
