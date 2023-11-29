package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;


import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.EvaluacionService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/evaluador")
@PreAuthorize("hasRole('EVALUADOR')")
public class EvaluadorController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private UserService userService;

    @Autowired
    private EvaluacionService evaluacionService;

    //Esta es la pantalla de inicio o index//
    @GetMapping("/index")
    public String userInSession(Authentication authentication, Model model) {
        User usuario = (User) authentication.getPrincipal();
        model.addAttribute("usuario", usuario);
        return "evaluador/panel-evaluador";
    }

    /*Cerrar sesion*/
    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    //metodo para que el evaluador pueda ver sus materiales asignados
    @GetMapping("/materiales/asignados")
    public String showMaterialesAsignados(Authentication authentication, Model model) {
        User usuario = (User) authentication.getPrincipal();
        model.addAttribute("usuario", usuario);
        Set<Material> materiales = materialService.getMaterialesAsignados(usuario);
        List<Boolean> fueEvaluado = materiales.stream()
                .map(material -> userService.haEvaluadoMaterial(usuario, material))
                .collect(Collectors.toList());
        model.addAttribute("materiales", materiales);
        model.addAttribute("hasUserEvaluated", fueEvaluado);
        return "evaluador/materiales-asignados";
    }

    @GetMapping("/materiales/asignados/{id}")
    public String showDetalleMaterial(@PathVariable Long id, Model model, Authentication authentication){
        User usuario = (User) authentication.getPrincipal();
        Material material = materialService.getMaterial(id);
        model.addAttribute("material", material);
        boolean haEvaluado = userService.haEvaluadoMaterial(usuario, material);
        model.addAttribute("haEvaluado", haEvaluado);
        if (haEvaluado) {
            Evaluacion evaluacion = evaluacionService.getEvaluacionByUserAndMaterial(usuario, material);
            model.addAttribute("evaluacion", evaluacion);
        } else {
            model.addAttribute("evaluacion", new Evaluacion());
        }
        return "evaluador/detalle-material";
    }

    @PostMapping("/materiales/asignados/{id}/createEvaluacion")
    public String createEvaluacion(@PathVariable Long id, @ModelAttribute Evaluacion evaluacion, Authentication authentication) {
        User evaluador = (User) authentication.getPrincipal();
        Material material = materialService.getMaterial(id);
        evaluacionService.createEvaluacion(evaluacion, evaluador, material);
        materialService.estaEvaluado(material);
        return "redirect:/evaluador/materiales/asignados";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        model.addAttribute("user", sessionUser);
        return "evaluador/profile";
    }
}
