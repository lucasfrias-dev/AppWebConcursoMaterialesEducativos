package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/")
public class LoginController {
    @GetMapping("/redirect")
    public String login(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.isParticipante()) {
            return "redirect:/users/index";
        }
        if (user.isAdministrador()) {
            return "redirect:/admin/admin/index";
        }
        else {
            return "redirect:/login?error";
        }
    }
}