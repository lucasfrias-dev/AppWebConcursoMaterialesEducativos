package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;

import java.util.List;

public interface MaterialService {
    void createMaterial(Material material);

    List<Material> getMaterialesByConcursante(User user);
    MaterialRepository getMaterialEducativoRepository();
    void updateRechazado(Long id);
    void updateAprobado(Long id);
    List<Material> materialesEducativosEnRevision();
    List<Material> materialesEducativos();
    List<Material> getMaterialesParticipantes();


    }
