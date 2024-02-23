package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.ConcursoService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private ConcursoService concursoService;

    /**
     * Controlador para redirigir a la página principal del sistema.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que representa la página principal del sistema.
     */
    @GetMapping("/index")
    public String home(Model model) {
        // Obtiene el concurso actual desde el servicio concursoService.
        Concurso concurso = concursoService.getConcursoActual();

        // Agrega el nombre de la edición del concurso actual al modelo para que esté disponible en la vista.
        model.addAttribute("edicion", concurso.getEdicion());

        // Agrega el año de finalización del concurso actual al modelo para que esté disponible en la vista.
        model.addAttribute("anio", concursoService.getConcursoActual().getFechaFin().getYear());

        // Agrega la lista de concursos anteriores al modelo para que esté disponible en la vista.
        model.addAttribute("concursosAnteriores", concursoService.getConcursosAnteriores());

        // Devuelve el nombre de la vista que representa la página principal del sistema.
        return "index";
    }

    /**+
     * Controlador para mostrar la página de inicio de sesión.
     *
     * @param error El parámetro opcional que indica el tipo de error ocurrido durante el intento de inicio de sesión.
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que representa la página de inicio de sesión.
     */
    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        // Verifica el tipo de error y agrega un mensaje correspondiente al modelo.
        if ("disabled".equals(error)) {
            model.addAttribute("loginError", "Esta cuenta ha sido desactivada");
        } else if ("bad_credentials".equals(error)) {
            model.addAttribute("loginError", "Usuario o contraseña incorrectos");
        }

        // Agrega un objeto User al modelo para su uso en el formulario de inicio de sesión.
        model.addAttribute("user", new User());

        // Devuelve el nombre de la vista que representa la página de inicio de sesión.
        return "login";
    }


    /**
     * Controlador para redirigir a diferentes páginas después de iniciar sesión, según el rol del usuario.
     *
     * @param request La solicitud HTTP que contiene la información sobre el usuario autenticado.
     * @return La URL de redirección según el rol del usuario.
     */
    @GetMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        // Verifica el rol del usuario y redirige a la URL correspondiente.
        if (request.isUserInRole("ADMINISTRADOR")) {
            return "redirect:/administrador/index";
        } else if (request.isUserInRole("CONCURSANTE")) {
            return "redirect:/concursante/index";
        } else if (request.isUserInRole("EVALUADOR")) {
            return "redirect:/evaluador/index";
        } else {
            // Si el usuario no tiene un rol reconocido, redirige a la página de inicio de sesión.
            return "redirect:/login";
        }
    }


    /**
     * Controlador para mostrar la lista de materiales participantes de una edición específica del concurso.
     *
     * @param model   El modelo que se utilizará para pasar datos a la vista.
     * @param edicion La edición del concurso de la cual se mostrarán los materiales participantes.
     * @return El nombre de la vista que representa la lista de materiales participantes.
     */
    @GetMapping("/materiales-participantes/{edicion}")
    public String showMaterialesParticipantes(Model model, @PathVariable String edicion) {
        // Verifica si la edición no es nula.
        if (!"null".equals(edicion)) {
            // Reemplaza los guiones con espacios en la edición.
            edicion = edicion.replace("-", " ");
            // Obtiene el concurso asociado a la edición desde el servicio concursoService.
            Concurso concurso = concursoService.getConcursoByEdicion(edicion);
            // Obtiene la lista de materiales participantes del concurso actual desde el servicio materialService.
            List<Material> materialesParticipantes = materialService.getMaterialesParticipantesByConcurso(concurso);

            // Agrega la lista de materiales participantes al modelo para que esté disponible en la vista.
            model.addAttribute("materialesParticipantes", materialesParticipantes);
            // Agrega la edición al modelo para que esté disponible en la vista.
            model.addAttribute("edicion", edicion);
        } else {
            // Si la edición es nula, agrega null al modelo para indicarlo.
            model.addAttribute("edicion", null);
        }

        // Devuelve el nombre de la vista que representa la lista de materiales participantes.
        return "materiales-participantes";
    }


    /**
     * Controlador para mostrar la página de registro de concursantes.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que representa la página de registro de concursantes.
     */
    @GetMapping("/participar")
    public String registerConcursante(Model model) {
        // Agrega un objeto User al modelo para su uso en el formulario de registro de concursantes.
        model.addAttribute("user", new User());

        // Devuelve el nombre de la vista que representa la página de registro de concursantes.
        return "/participar";
    }


    /**
     * Controlador para procesar el formulario de registro de concursantes.
     *
     * @param user             El objeto User que contiene la información del concursante.
     * @param result           El objeto BindingResult que contendrá los errores de validación.
     * @param model            El modelo que se utilizará para pasar datos a la vista.
     * @param passwordConfirm  La confirmación de la contraseña del concursante.
     * @return La URL de redirección después de procesar el formulario.
     */
    @PostMapping("/participar")
    public String createConcursante(@Valid @ModelAttribute("user") User user, BindingResult result, ModelMap model, @RequestParam("passwordConfirm") String passwordConfirm) {
        // Verifica si las contraseñas coinciden y si hay errores en el formulario de registro.
        if (!user.getPassword().equals(passwordConfirm)) {
            result.rejectValue("password", "error.password", "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) {
            // Si hay errores, agrega el objeto User y devuelve la vista de registro de concursantes.
            model.addAttribute("user", user);
            return "/participar";
        }

        // Intenta crear el usuario como concursante.
        try {
            userService.createUser(user, User.Rol.CONCURSANTE);
        } catch (Exception e) {  // Si ocurre un error, añade el mensaje de error al modelo y retorna la vista de registro de concursantes.
            result.rejectValue("email", "error.email", e.getMessage());
            model.addAttribute("user", user);
            return "/participar";
        }

        // Si la creación del usuario fue exitosa, redirige a la ruta "/login".
        return "redirect:/login";
    }

}
