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

import java.util.List;

@Controller
@RequestMapping("/concursante")
@PreAuthorize("hasRole('ROLE_CONCURSANTE')")
public class ConcursanteController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private UserService userService;

    /*@GetMapping("/")
    public String panelConcursante(Model model) {
        model.addAttribute("user", new User());
        return "concursante/panel-concursante";
    }*/

    //Esta es la pantalla de inicio o index//
    @GetMapping("/")
    public String userInSession(Authentication authentication, Model model) {
        User usuario= (User) authentication.getPrincipal();
        model.addAttribute("usuario", usuario);
        return "concursante/panel-concursante";
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

    @GetMapping("/postular-material")
    public String showPostularMaterial(Model model) {
        model.addAttribute("material", new Material());
        return "/concursante/postular-material";
    }

    @PostMapping("/postular-material")
    public String createMaterial(@Valid @ModelAttribute("material") Material material, BindingResult result, Model model, Authentication authentication){
        if (result.hasErrors()) {
            model.addAttribute("material", material);
            return "concursante/postular-material";
        }

        User sessionUser = (User) authentication.getPrincipal();
        materialService.createMaterial(material, sessionUser);
        return "redirect:/concursante/mis-materiales";
    }

    /*Ver materiar del usuario en sesion*//*
    @PreAuthorize("#hasRole('ROLE_CONCURSANTE')")  *//*Solo los administradores pueden acceder*//*
    @GetMapping("/materialview")
    public String publicarMaterial(Model model, Authentication authentication) {
        User usuario= (User) authentication.getPrincipal();
        Material materialEducativo= usuario.getMaterialEducativo();
        model.addAttribute("material", materialEducativo);
        return "/users/materialview";
    }*/

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
        ModelAndView modelAndView = new ModelAndView("concursante/mis-materiales");
        modelAndView.addObject("materiales", materialService.getMaterialesByConcursante(sessionUser));
        return modelAndView;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        model.addAttribute("user", sessionUser);
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
