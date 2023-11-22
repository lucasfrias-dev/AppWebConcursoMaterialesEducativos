package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Material createMaterial(Material material, User user) {
        /*material.setEnRevision();*/
        material.setConcursante(user);
        user.getMaterialesPostulados().add(material);
        return materialRepository.save(material);
    }

    @Override
    public List<Material> getMaterialesByConcursante(User user) {
        return materialRepository.findByConcursante(user);
    }

    @Override
    public List<Material> getMateriales(){
        return materialRepository.findAll();
    }

    @Override
    public List<Material> getMaterialesParticipantes() {
        return materialRepository.findMaterialsByAprobadoIsTrue();
    }

    @Override
    public List<Material> getMaterialesPendientes(){
       return materialRepository.findMaterialsByAprobadoIsNull();
    }

    @Override
    public Material getMaterial(Long id) {
        return materialRepository.findMaterialById(id);
    }

    @Override
    public List<Material> getMaterialesAsignados(User user) {
        return (List<Material>) user.getMaterialesAEvaluar();
    }

    @Override
    public void updateAprobado(Long id){
        Material material = materialRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Material educativo no encontrado con id: " + id));
        material.setAprobado(true);
        materialRepository.save(material);
    }

    @Override
    public void updateRechazado(Long id){
        Material material = materialRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Material educativo no encontrado con id: " + id));
        material.setAprobado(false);
        materialRepository.save(material);
    }

}
