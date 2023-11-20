package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;

import java.util.List;

public interface MaterialService {

    List<Material> getMaterialesParticipantes();

    void createMaterial(Material material);

    List<Material> getMaterialesByConcursante(User user);
}
