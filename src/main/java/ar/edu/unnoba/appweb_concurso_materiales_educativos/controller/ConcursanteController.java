package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
    //Esta es la pantalla de inicio o index//
    @GetMapping("/")
    public String userInSession(Authentication authentication, Model model) {
        User usuario= (User) authentication.getPrincipal();
        model.addAttribute("usuario", usuario);
        return "users/index";
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
    /*crear Nuevo Material*/
    @PreAuthorize("#authentication.principal.isParticipante()")  /*solo los participante pueden cargar material*/
    @GetMapping("/material")
    public String newMaterial(Model model, Authentication authentication) {
        User usuario= (User) authentication.getPrincipal();
        String name= usuario.getUsername();
        if(userService.loadUserByUsername(name).getMaterialEducativo() == null) {
            model.addAttribute("material", new Material());
            return "users/material";
        }
        else{
            model.addAttribute("material", usuario.getMaterialEducativo());
            return "users/materialcargado";
        }
    }
    @PreAuthorize("#authentication.principal.isParticipante()")
    @PostMapping
    public String createMaterial(@ModelAttribute Material material, Authentication authentication){
        User usuario= (User) authentication.getPrincipal();
        material.setEnRevision();
        Material nuevoMaterial = materialService.getMaterialEducativoRepository().save(material);
        User u2= userService.getUsuarioRepository().findOneByUsername(usuario.getUsername());
        u2.setMaterialEducativo(nuevoMaterial);
        userService.getUsuarioRepository().save(u2);
        return "redirect:/material";

    }
    /*Ver materiar del usuario en sesion*/
    @PreAuthorize("#authentication.principal.isParticipante()")  /*Solo los administradores pueden acceder*/
    @GetMapping("/materialview")
    public String publicarMaterial(Model model, Authentication authentication) {
        User usuario= (User) authentication.getPrincipal();
        Material materialEducativo= usuario.getMaterialEducativo();
        model.addAttribute("material", materialEducativo);
        return "/users/materialview";
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
