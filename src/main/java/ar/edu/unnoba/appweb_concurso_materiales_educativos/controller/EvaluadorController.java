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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/evaluador")
@PreAuthorize("hasRole('EVALUADOR')")
public class EvaluadorController {

    private final MaterialService materialService;

    private final UserService userService;

    private final EvaluacionService evaluacionService;

    @Autowired
    public EvaluadorController(MaterialService materialService, UserService userService, EvaluacionService evaluacionService) {
        this.materialService = materialService;
        this.userService = userService;
        this.evaluacionService = evaluacionService;
    }

    /**
     * Controlador para mostrar el panel del evaluador autenticado.
     *
     * @param authentication La información de autenticación que contiene al evaluador autenticado.
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará el panel del evaluador.
     */
    @GetMapping("/index")
    public String dashboardEvaluador(Authentication authentication, Model model) {
        // Obtiene al evaluador autenticado desde la información de autenticación.
        User usuario = (User) authentication.getPrincipal();

        // Agrega al evaluador al modelo para que esté disponible en la vista.
        model.addAttribute("usuario", usuario);

        // Obtiene la lista de materiales asignados que aún no han sido evaluados por el evaluador.
        List<Material> materialesPendientes = materialService.getMaterialesAsignadosPendientes(usuario.getId());

        // Agrega la lista de materiales pendientes al modelo para que esté disponible en la vista.
        model.addAttribute("materialesPendientes", materialesPendientes);

        // Devuelve el nombre de la vista que mostrará el panel del evaluador.
        return "evaluador/panel-evaluador";
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
     * Controlador para mostrar la página de materiales asignados al evaluador autenticado.
     *
     * @param authentication La información de autenticación que contiene al evaluador autenticado.
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la página de materiales asignados al evaluador.
     */
    @GetMapping("/materiales/asignados")
    @Transactional
    public String showMaterialesAsignados(Authentication authentication, Model model) {
        // Obtiene al evaluador autenticado desde la información de autenticación.
        User usuario = (User) authentication.getPrincipal();

        // Recarga el usuario con la colección 'materialesAEvaluar' inicializada.
        usuario = userService.getUsuarioConMaterialesAsignados(usuario.getId());

        // Agrega al evaluador al modelo para que esté disponible en la vista.
        model.addAttribute("usuario", usuario);

        // Obtiene los materiales asignados al evaluador desde el servicio materialService.
        Set<Material> materiales = materialService.getMaterialesAsignados(usuario);

        // Calcula una lista de booleanos que indica si el evaluador ha evaluado cada material.
        User finalUsuario = usuario;
        List<Boolean> fueEvaluado = materiales.stream()
                .map(material -> userService.haEvaluadoMaterial(finalUsuario, material))
                .collect(Collectors.toList());

        // Agrega la lista de materiales y la lista de evaluaciones al modelo para que estén disponibles en la vista.
        model.addAttribute("materiales", materiales);
        model.addAttribute("hasUserEvaluated", fueEvaluado);

        // Devuelve el nombre de la vista que mostrará la página de materiales asignados al evaluador.
        return "evaluador/materiales-asignados";
    }


    /**
     * Controlador para mostrar el detalle de un material asignado al evaluador autenticado.
     * Con la posibilidad de enviar una evaluación al material. En caso de que el evaluador
     * ya haya evaluado el material, se mostrará la evaluación realizada anteriormente.
     *
     * @param id             El identificador del material cuyo detalle se mostrará.
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @param authentication La información de autenticación que contiene al evaluador autenticado.
     * @return El nombre de la vista que mostrará el detalle del material asignado al evaluador.
     */
    @GetMapping("/materiales/asignados/{id}")
    public String showDetalleMaterial(@PathVariable Long id, Model model, Authentication authentication) {
        // Obtiene al evaluador autenticado desde la información de autenticación.
        User usuario = (User) authentication.getPrincipal();

        // Obtiene el material con el identificador proporcionado desde el servicio materialService.
        Material material = materialService.getMaterial(id);

        // Agrega el material al modelo para que esté disponible en la vista.
        model.addAttribute("material", material);

        // Verifica si el evaluador ha evaluado el material.
        boolean haEvaluado = userService.haEvaluadoMaterial(usuario, material);

        // Agrega un booleano al modelo indicando si el evaluador ha evaluado el material.
        model.addAttribute("haEvaluado", haEvaluado);

        // Si el evaluador ha evaluado el material, obtiene y agrega la evaluación al modelo.
        // De lo contrario, agrega una nueva evaluación vacía al modelo para que la complete.
        if (haEvaluado) {
            Evaluacion evaluacion = evaluacionService.getEvaluacionByUserAndMaterial(usuario, material);
            model.addAttribute("evaluacion", evaluacion);
        } else {
            model.addAttribute("evaluacion", new Evaluacion());
        }

        // Devuelve el nombre de la vista que mostrará el detalle del material asignado al evaluador.
        return "evaluador/detalle-material";
    }


    /**
     * Controlador para crear una evaluación de un material asignado al evaluador autenticado.
     *
     * @param id             El identificador del material al que se asociará la evaluación.
     * @param evaluacion     El objeto Evaluacion que contiene los datos de la nueva evaluación.
     * @param authentication La información de autenticación que contiene al evaluador autenticado.
     * @return Una redirección a la página que muestra los materiales asignados al evaluador.
     */
    @PostMapping("/materiales/asignados/{id}/createEvaluacion")
    @Transactional // Se usa transacción para asegurar la integridad de la base de datos.
    public String createEvaluacion(@PathVariable Long id, @ModelAttribute Evaluacion evaluacion, Authentication authentication) {
        // Obtiene al evaluador autenticado desde la información de autenticación.
        User evaluador = (User) authentication.getPrincipal();

        // Obtiene el material asociado al identificador proporcionado.
        Material material = materialService.getMaterial(id);

        // Crea la evaluación y la asocia al evaluador y al material.
        evaluacionService.createEvaluacion(evaluacion, evaluador, material);

        // Actualiza el estado del material para indicar que ha sido evaluado.
        materialService.updateEvaluado(id);

        // Redirige a la página que muestra los materiales asignados al evaluador.
        return "redirect:/evaluador/materiales/asignados";
    }


    /**
     * Controlador para mostrar el perfil del evaluador autenticado.
     *
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @param authentication La información de autenticación que contiene al evaluador autenticado.
     * @return El nombre de la vista que mostrará el perfil del evaluador.
     */
    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        // Obtiene al evaluador autenticado desde la información de autenticación.
        User sessionUser = (User) authentication.getPrincipal();

        // Agrega al evaluador al modelo para que esté disponible en la vista.
        model.addAttribute("user", sessionUser);

        // Devuelve el nombre de la vista que mostrará el perfil del evaluador.
        return "evaluador/profile";
    }

}
