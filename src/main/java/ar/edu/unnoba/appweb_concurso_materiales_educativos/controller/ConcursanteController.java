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
@PreAuthorize("hasRole('CONCURSANTE')")
public class ConcursanteController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private UserService userService;

    //Esta es la pantalla de inicio o index//
    @GetMapping("/index")
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

    @GetMapping("/mis-materiales")
    public ModelAndView showMisMateriales(Authentication authentication) {
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

    @GetMapping("/profile/edit")
    public String showEditProfile(Model model, Authentication authentication) {
        User sessionUser = (User) authentication.getPrincipal();
        model.addAttribute("user", sessionUser); // Añade al usuario al modelo
        return "/concursante/edit-profile"; // Devuelve la vista del formulario de edición del perfil
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("user") User updatedUser, Authentication authentication, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/concursante/edit-profile"; // Redirige de vuelta al formulario de edición del perfil si hay errores de validación
        }

        User sessionUser = (User) authentication.getPrincipal(); // Obtiene al usuario en sesión
        updatedUser.setPassword(sessionUser.getPassword()); // Copia la contraseña del usuario en sesión al usuario actualizado
        // Actualiza la información del usuario
        try {
            userService.updateUser(updatedUser, sessionUser.getId());
        } catch (Exception e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return "/concursante/edit-profile"; // Redirige de vuelta al formulario de edición del perfil
        }

        return "redirect:concursante/profile"; // Redirige de vuelta a la página del perfil
    }
}
