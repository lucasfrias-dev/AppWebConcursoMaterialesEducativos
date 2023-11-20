package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/concursante")
@PreAuthorize("hasRole('ROLE_CONCURSANTE')")
public class ConcursanteController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String panelConcursante(Model model) {
        model.addAttribute("user", new User());
        return "concursante/panel-concursante";
    }

    @GetMapping("/cargar-material")
    public String showCargarMaterialForm(Model model) {
        model.addAttribute("material", new Material());
        return "concursante/cargar-material";
    }

    @PostMapping("/cargar-material")
    public String cargarMaterial(@Valid @ModelAttribute("material") Material material, BindingResult result, Model model, Authentication authentication) {
        if (result.hasErrors()) {
            return "concursante/cargar-material";
        }

        User sessionUser = (User) authentication.getPrincipal();
        material.setConcursante(sessionUser);

        materialService.createMaterial(material);
        model.addAttribute("message", "Material cargado correctamente.");
        return "concursante/confirmacion-carga";
    }

    /*@GetMapping("/mis-materiales")
    public String getMisMateriales(Model model, Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();

        // Busca al usuario en la base de datos
        User user = userService.findById(sessionUser.getId());

        // Obtiene los materiales postulados por el usuario
        List<Material> misMateriales = materialService.getMaterialesByConcursante(user);

        // Añade los materiales al modelo
        model.addAttribute("misMateriales", misMateriales);

        // Devuelve la vista de los materiales del usuario
        return "concursante/mis-materiales";
    }*/

    @GetMapping("/mis-materiales")
    public ModelAndView getMyMaterials(Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        List<Material> materiales = materialService.getMaterialesByConcursante(sessionUser);
        ModelAndView modelAndView = new ModelAndView("concursante/mis-materiales");
        modelAndView.addObject("materiales", materiales);
        return modelAndView;
    }

    @GetMapping("/profile/{id}")
    public String showProfile(Model model, @PathVariable("id") Long id) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "concursante/profile";
    }

    @GetMapping("/profile/{id}/edit")
    public String showEditProfile(Model model, @PathVariable Long id) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "concursante/edit-profile";
    }

    @PostMapping("/profile/{id}/edit")
    public String updateProfile(@ModelAttribute User user, @PathVariable("id") Long id) {
        userService.updateUser(user, id);
        return "redirect:/profile/" + id;
    }
}
