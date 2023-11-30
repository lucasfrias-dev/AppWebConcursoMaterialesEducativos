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

    /**
     * Controlador para mostrar el panel del concursante.
     *
     * @param authentication La información de autenticación que contiene al usuario autenticado.
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará el panel del concursante.
     */
    @GetMapping("/index")
    public String dashboardConcursante(Authentication authentication, Model model) {
        // Obtiene al usuario autenticado desde la información de autenticación.
        User usuario = (User) authentication.getPrincipal();

        // Agrega el usuario al modelo para que esté disponible en la vista.
        model.addAttribute("usuario", usuario);

        // Devuelve el nombre de la vista que mostrará el panel del concursante.
        return "concursante/panel-concursante";
    }


    /**
     * Controlador para procesar la operación de cierre de sesión.
     *
     * @param request  La solicitud HTTP recibida.
     * @param response La respuesta HTTP que se enviará.
     * @return Una redirección a la página de inicio de sesión con un parámetro indicando que se ha cerrado la sesión.
     */
    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        // Obtiene la información de autenticación actual del contexto de seguridad.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si hay una autenticación válida, realiza la operación de cierre de sesión.
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        // Redirige a la página de inicio de sesión con un parámetro indicando que se ha cerrado la sesión.
        return "redirect:/login?logout";
    }


    /**
     * Controlador para mostrar la página de postulación de material por parte del concursante.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la página de postulación de material.
     */
    @GetMapping("/postular-material")
    public String showPostularMaterial(Model model) {
        // Agrega un nuevo objeto Material al modelo para que esté disponible en la vista.
        model.addAttribute("material", new Material());

        // Devuelve el nombre de la vista que mostrará la página de postulación de material.
        return "concursante/postular-material";
    }


    /**
     * Controlador para procesar la creación y postulación de un nuevo material por parte del concursante.
     *
     * @param material        El objeto Material que se ha vinculado desde el formulario.
     * @param result          El objeto BindingResult que contiene los resultados de la validación.
     * @param model           El modelo que se utilizará para pasar datos a la vista.
     * @param authentication  La información de autenticación que contiene al concursante autenticado.
     * @return Una redirección a la página que muestra los materiales del concursante o la página de postulación en caso de errores.
     */
    @PostMapping("/postular-material")
    public String createMaterial(
            @Valid @ModelAttribute("material") Material material,
            BindingResult result,
            Model model,
            Authentication authentication
    ) {
        // Verifica si hay errores de validación en el formulario.
        if (result.hasErrors()) {
            // Si hay errores, agrega el objeto Material y retorna a la página de postulación de material.
            model.addAttribute("material", material);
            return "concursante/postular-material";
        }

        // Obtiene al concursante autenticado desde la información de autenticación.
        User sessionUser = (User) authentication.getPrincipal();

        // Crea y postula el nuevo material utilizando el servicio materialService.
        materialService.createMaterial(material, sessionUser);

        // Redirige a la página que muestra los materiales del concursante.
        return "redirect:/concursante/mis-materiales";
    }

    /**
     * Controlador para mostrar la página de materiales postulados por el concursante autenticado.
     *
     * @param authentication La información de autenticación que contiene al concursante autenticado.
     * @return Un objeto ModelAndView que representa la vista de los materiales del concursante.
     */
    @GetMapping("/mis-materiales")
    public ModelAndView showMisMateriales(Authentication authentication) {
        // Obtiene al concursante autenticado desde la información de autenticación.
        User sessionUser = (User) authentication.getPrincipal();

        // Crea un objeto ModelAndView asociado a la vista "concursante/mis-materiales".
        ModelAndView modelAndView = new ModelAndView("concursante/mis-materiales");

        // Agrega la lista de materiales postulados por el concursante al modelo para que esté disponible en la vista.
        modelAndView.addObject("materiales", materialService.getMaterialesByConcursante(sessionUser));

        // Retorna el objeto ModelAndView.
        return modelAndView;
    }


    /**
     * Controlador para mostrar la página de perfil del concursante autenticado.
     *
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @param authentication La información de autenticación que contiene al concursante autenticado.
     * @return El nombre de la vista que mostrará la página de perfil del concursante.
     */
    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        // Obtiene al concursante autenticado desde la información de autenticación.
        User sessionUser = (User) authentication.getPrincipal();

        // Agrega el concursante al modelo para que esté disponible en la vista.
        model.addAttribute("user", sessionUser);

        // Devuelve el nombre de la vista que mostrará la página de perfil del concursante.
        return "concursante/profile";
    }

}
