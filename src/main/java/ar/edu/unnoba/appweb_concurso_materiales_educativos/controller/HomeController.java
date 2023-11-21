package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String home() {
        return "index";
    }

    // Método para manejar las solicitudes GET a la ruta "/login"
    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        // Si hay un error, añade un mensaje de error al modelo
        if (error != null) {
            model.addAttribute("loginError", "Usuario o contraseña incorrectos");
        }
        // Añade un nuevo usuario al modelo
        model.addAttribute("user", new User());
        return "login"; // Retorna la vista "login"
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user, Model model, Authentication authentication) {
        // Intenta autenticar al usuario
        try {
            User userSession = (User) authentication.getPrincipal();
            // Redirige al usuario a una vista diferente según su rol
            return switch (userSession.getRol()) {
                case ADMINISTRADOR -> "redirect:/administrador/";
                case CONCURSANTE -> "redirect:/concursante/";
                default -> throw new Exception("Rol desconocido");
            };
        } catch (Exception e) {
            model.addAttribute("loginError", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/materiales-participantes")
    public String getMaterialesParticipantes(Model model) {
        List<Material> materialesParticipantes = materialService.getMaterialesParticipantes();
        model.addAttribute("materialesParticipantes", materialesParticipantes);
        return "materiales-participantes";
    }

    @GetMapping("/participar")
    public String registerConcursante(Model model) {
        model.addAttribute("user", new User());
        return "participar";
    }

    @PostMapping("/participar")
    public String createConcursante(@Valid @ModelAttribute("user")User user, BindingResult result, ModelMap model, @RequestParam("passwordConfirm") String passwordConfirm) {
        // Verifica si las contraseñas coinciden y si hay errores en el formulario de registro
        if (!user.getPassword().equals(passwordConfirm)) {
            result.rejectValue("password", "error.password", "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "participar";
        }

        // Intenta crear el usuario
        try {
            userService.createUser(user, User.Rol.CONCURSANTE);
        } catch (Exception e) {  // Si ocurre un error, añade el mensaje de error al modelo y retorna la vista "register"
            model.addAttribute("formError", e.getMessage());
            model.addAttribute("user", user);
            return "participar";
        }
        // Si la creación del usuario fue exitosa, redirige a la ruta "/login"
        return "redirect:/login";
    }
}