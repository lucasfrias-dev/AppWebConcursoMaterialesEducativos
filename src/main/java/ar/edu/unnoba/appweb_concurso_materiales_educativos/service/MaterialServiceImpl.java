package ar.edu.unnoba.appweb_concurso_materiales_educativos.service;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialRepository materialRepository;
    @Override
    public MaterialRepository getMaterialEducativoRepository(){
        return materialRepository;
    }
    @Override
    public void createMaterial(Material material) {
        materialRepository.save(material);
    }

    @Override
    public List<Material> getMaterialesByConcursante(User user) {
        return materialRepository.findByConcursante(user);
    }

    public List<Material> materialesEducativos(){
        return materialRepository.findAll();
    }

    @Override
    public List<Material> getMaterialesParticipantes() {
        return materialRepository.findByAprobadoTrue();
    }

    public List<Material> materialesEducativosEnRevision(){
        List<Material> matED = new ArrayList<Material>();
        for(Material me : materialRepository.findAll()){
            if (me.getEstado().equals("Revision")){
                matED.add(me);
            }
        }
        return matED;
    }
    public void updateAprobado(Long id){
        Material materialEducativo1=getMaterialEducativoRepository().getById(id);
        materialEducativo1.setAprobado();
        getMaterialEducativoRepository().save(materialEducativo1);
    }
    public void updateRechazado(Long id){
        Material materialEducativo1=getMaterialEducativoRepository().getById(id);
        materialEducativo1.setRechazado();
        getMaterialEducativoRepository().save(materialEducativo1);
    }

}
