package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/administrador")
@PreAuthorize("hasRole('ADMINISTRADOR')") /*solo los administradores acceden a este controlador*/
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private MaterialService materialService;

    /**
     * Controlador para la página de inicio ("/index") que muestra el panel del administrador.
     *
     * @param authentication La información de autenticación del usuario.
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará el panel del administrador.
     */
    @GetMapping("/index")
    public String dashboardAdministrador(Authentication authentication, Model model) {
        // Obtiene el usuario autenticado desde la información de autenticación.
        User usuario = (User) authentication.getPrincipal();

        // Agrega el usuario al modelo para que esté disponible en la vista.
        model.addAttribute("usuario", usuario);

        // Devuelve el nombre de la vista que mostrará el panel del administrador.
        return "administrador/panel-administrador";
    }


    /**
     * Controlador para la página de cierre de sesión ("/logout").
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @return Una redirección a la página de inicio de sesión con un parámetro indicando el cierre de sesión.
     */
    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        // Obtiene la información de autenticación del contexto de seguridad.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si hay un usuario autenticado, realiza el proceso de cierre de sesión.
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        // Redirige a la página de inicio de sesión con un parámetro indicando el cierre de sesión.
        return "redirect:/login?logout";
    }


    /**
     * Controlador para mostrar todos los materiales en la interfaz de administrador.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la lista de materiales.
     */
    @GetMapping("/materiales/all")
    public String showMateriales(Model model) {
        // Agrega la lista de materiales al modelo para que esté disponible en la vista.
        model.addAttribute("materiales", materialService.getMateriales());

        // Devuelve el nombre de la vista que mostrará la lista de materiales.
        return "administrador/materiales";
    }


    /**
     * Controlador para mostrar todos los materiales pendientes de aprobación en la interfaz de administrador.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la lista de materiales pendientes de aprobación.
     */
    @GetMapping("/materiales/pendientes-de-aprobacion")
    public String showMaterialesPendientesAprobaccion(Model model) {
        // Agrega la lista de materiales pendientes de aprobación al modelo para que esté disponible en la vista.
        model.addAttribute("materiales", materialService.getMaterialesPendientesAprobacion());

        // Devuelve el nombre de la vista que mostrará la lista de materiales pendientes de aprobación.
        return "administrador/materiales-pendientes-aprobacion";
    }


    /**
     * Controlador para mostrar todos los materiales pendientes de evaluación en la interfaz de administrador.
     * Incluye información sobre los evaluadores pendientes asociados a cada material.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la lista de materiales pendientes de evaluación.
     */
    @GetMapping("/materiales/pendientes-de-evaluacion")
    public String showMaterialesPendientesEvaluacion(Model model) {
        // Obtiene la lista de materiales pendientes de evaluación.
        List<Material> materiales = materialService.getMaterialesPendientesEvaluacion();

        // Crea un mapa para asociar cada material con la lista de evaluadores pendientes para ese material.
        Map<Material, List<User>> evaluadoresPendientes = new HashMap<>();
        for (Material material : materiales) {
            evaluadoresPendientes.put(material, userService.getEvaluadoresPendientes(material));
        }

        // Agrega la lista de materiales y el mapa de evaluadores pendientes al modelo para que estén disponibles en la vista.
        model.addAttribute("materiales", materiales);
        model.addAttribute("evaluadoresPendientes", evaluadoresPendientes);

        // Devuelve el nombre de la vista que mostrará la lista de materiales pendientes de evaluación.
        return "administrador/materiales-pendientes-evaluacion";
    }


    /**
     * Controlador para aprobar un material específico.
     *
     * @param id La identificación del material a aprobar, obtenida del path de la URL.
     * @return Una redirección a la página que muestra la lista de materiales pendientes de aprobación.
     */
    @PostMapping("/materiales/{id}/aprobar")
    public String aprobarMaterial(@PathVariable("id") Long id) {
        // Aprobar el material utilizando el servicio materialService.
        materialService.aprobarMaterial(id);

        // Redirige a la página que muestra la lista de materiales pendientes de aprobación.
        return "redirect:/administrador/materiales/pendientes-de-aprobacion";
    }


    /**
     * Controlador para rechazar un material específico.
     *
     * @param id La identificación del material a rechazar, obtenida del path de la URL.
     * @return Una redirección a la página que muestra la lista de materiales pendientes de aprobación.
     */
    @PostMapping("/materiales/{id}/rechazar")
    public String rechazarMaterial(@PathVariable("id") Long id) {
        // Rechazar el material utilizando el servicio materialService.
        materialService.rechazarMaterial(id);

        // Redirige a la página que muestra la lista de materiales pendientes de aprobación.
        return "redirect:/administrador/materiales/pendientes-de-aprobacion";
    }

    /**
     * Controlador para mostrar la página de registro de un nuevo evaluador.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la página de registro de un nuevo evaluador.
     */
    @GetMapping("/register/evaluador")
    public String registerEvaluador(Model model) {
        // Agrega un objeto User vacío al modelo para que se pueda vincular con el formulario de registro.
        model.addAttribute("evaluador", new User());

        // Devuelve el nombre de la vista que mostrará la página de registro de un nuevo evaluador.
        return "administrador/register-evaluador";
    }


    /**
     * Controlador para procesar la creación de un nuevo evaluador.
     *
     * @param user             El objeto User que contiene los datos del evaluador, vinculado desde el formulario.
     * @param result           El objeto BindingResult que maneja los errores de validación.
     * @param model            El modelo que se utilizará para pasar datos a la vista.
     * @param passwordConfirm  La confirmación de la contraseña, obtenida desde el formulario.
     * @return Una redirección a la página que muestra la lista de evaluadores registrados o la vista de registro si hay errores.
     */
    @PostMapping("/register/evaluador")
    public String createEvaluador(
            @Valid @ModelAttribute("evaluador") User user,
            BindingResult result,
            ModelMap model,
            @RequestParam("passwordConfirm") String passwordConfirm
    ) {
        // Verifica si las contraseñas coinciden y si hay errores en el formulario de registro.
        if (!user.getPassword().equals(passwordConfirm)) {
            result.rejectValue("password", "error.password", "Las contraseñas no coinciden");
        }

        // Si hay errores, retorna a la vista de registro con los errores.
        if (result.hasErrors()) {
            model.addAttribute("evaluador", user);
            return "administrador/register-evaluador";
        }

        // Intenta crear el usuario evaluador.
        try {
            userService.createUser(user, User.Rol.EVALUADOR);
        } catch (Exception e) {
            // Si ocurre un error durante la creación, añade el mensaje de error al modelo y retorna a la vista de registro.
            result.rejectValue("email", "error.email", e.getMessage());
            model.addAttribute("evaluador", user);
            return "administrador/register-evaluador";
        }

        // Redirige a la página que muestra la lista de evaluadores registrados.
        return "redirect:/administrador/evaluadores/registrados";
    }


    /**
     * Controlador para mostrar la página de registro de un nuevo administrador.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la página de registro de un nuevo administrador.
     */
    @GetMapping("/register/admin")
    public String registerAdministrador(Model model) {
        // Agrega un objeto User vacío al modelo para que se pueda vincular con el formulario de registro.
        model.addAttribute("admin", new User());

        // Devuelve el nombre de la vista que mostrará la página de registro de un nuevo administrador.
        return "administrador/register-admin";
    }


    /**
     * Controlador para procesar la creación de un nuevo administrador.
     *
     * @param user             El objeto User que contiene los datos del administrador, vinculado desde el formulario.
     * @param result           El objeto BindingResult que maneja los errores de validación.
     * @param model            El modelo que se utilizará para pasar datos a la vista.
     * @param passwordConfirm  La confirmación de la contraseña, obtenida desde el formulario.
     * @return Una redirección a la página que muestra la lista de administradores registrados o la vista de registro si hay errores.
     */
    @PostMapping("/register/admin")
    public String createAdministrador(
            @Valid @ModelAttribute("admin") User user,
            BindingResult result,
            ModelMap model,
            @RequestParam("passwordConfirm") String passwordConfirm
    ) {
        // Verifica si las contraseñas coinciden y si hay errores en el formulario de registro.
        if (!user.getPassword().equals(passwordConfirm)) {
            result.rejectValue("password", "error.password", "Las contraseñas no coinciden");
        }

        // Si hay errores, retorna a la vista de registro con los errores.
        if (result.hasErrors()) {
            model.addAttribute("admin", user);
            return "administrador/register-admin";
        }

        // Intenta crear el usuario administrador.
        try {
            userService.createUser(user, User.Rol.ADMINISTRADOR);
        } catch (Exception e) {
            // Si ocurre un error durante la creación, añade el mensaje de error al modelo y retorna a la vista de registro.
            result.rejectValue("email", "error.email", e.getMessage());
            model.addAttribute("admin", user);
            return "administrador/register-admin";
        }

        // Redirige a la página que muestra la lista de administradores registrados.
        return "redirect:/administrador/admin/registrados";
    }


    /**
     * Controlador para mostrar la lista de evaluadores registrados en la interfaz de administrador.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la lista de evaluadores registrados.
     */
    @GetMapping("/evaluadores/registrados")
    public String showeEvaludoresDisponibles(Model model) {
        // Agrega la lista de evaluadores registrados al modelo para que esté disponible en la vista.
        model.addAttribute("evaluadores", userService.getAllEvaluadores());

        // Devuelve el nombre de la vista que mostrará la lista de evaluadores registrados.
        return "administrador/evaluadores-registrados";
    }


    /**
     * Controlador para mostrar la lista de administradores registrados en la interfaz de administrador.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la lista de administradores registrados.
     */
    @GetMapping("/admin/registrados")
    public String showAdministradoresDisponibles(Model model) {
        // Agrega la lista de administradores registrados al modelo para que esté disponible en la vista.
        model.addAttribute("administradores", userService.getAllAdministradores());

        // Devuelve el nombre de la vista que mostrará la lista de administradores registrados.
        return "administrador/admin-registrados";
    }


    /**
     * Controlador para mostrar la lista de todos los usuarios en la interfaz de administrador.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la lista de todos los usuarios.
     */
    @GetMapping("/usuarios/all")
    public String showUsers(Model model) {
        // Agrega la lista de todos los usuarios al modelo para que esté disponible en la vista.
        model.addAttribute("usuarios", userService.findAll());

        // Devuelve el nombre de la vista que mostrará la lista de todos los usuarios.
        return "administrador/all-users";
    }


    /**
     * Controlador para mostrar la página de baja de un usuario específico en la interfaz de administrador.
     *
     * @param id    La identificación del usuario, obtenida del path de la URL.
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la página de baja de un usuario específico.
     */
    @GetMapping("/usuarios/{id}/bajaUsuario")
    public String showBajaUsuario(@PathVariable Long id, Model model) {
        // Obtiene el usuario con la identificación proporcionada.
        User usuario = userService.findById(id);

        // Agrega el usuario al modelo para que esté disponible en la vista.
        model.addAttribute("usuario", usuario);

        // Devuelve el nombre de la vista que mostrará la página de baja de un usuario específico.
        return "administrador/baja-user";
    }


    /**
     * Controlador para procesar la baja de un usuario específico en la interfaz de administrador.
     *
     * @param id La identificación del usuario a dar de baja, obtenida del path de la URL.
     * @return Una redirección a la página que muestra la lista de todos los usuarios.
     */
    @PostMapping("/usuarios/{id}/bajaUsuario")
    public String bajaUsuario(@PathVariable Long id) {
        // Realiza la baja del usuario utilizando el servicio userService.
        userService.bajaUsuario(id);

        // Redirige a la página que muestra la lista de todos los usuarios.
        return "redirect:/administrador/usuarios/all";
    }


    /**
     * Controlador para mostrar la página de alta de un usuario específico en la interfaz de administrador.
     *
     * @param id    La identificación del usuario, obtenida del path de la URL.
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la página de alta de un usuario específico.
     */
    @GetMapping("/usuarios/{id}/altaUsuario")
    public String showAltaUsuario(@PathVariable Long id, Model model) {
        // Obtiene el usuario con la identificación proporcionada.
        User usuario = userService.findById(id);

        // Agrega el usuario al modelo para que esté disponible en la vista.
        model.addAttribute("usuario", usuario);

        // Devuelve el nombre de la vista que mostrará la página de alta de un usuario específico.
        return "administrador/alta-user";
    }

    /**
     * Controlador para procesar el alta de un usuario específico en la interfaz de administrador.
     *
     * @param id La identificación del usuario a dar de alta, obtenida del path de la URL.
     * @return Una redirección a la página que muestra la lista de todos los usuarios.
     */
    @PostMapping("/usuarios/{id}/altaUsuario")
    public String altaUsuario(@PathVariable Long id) {
        // Realiza el alta del usuario utilizando el servicio userService.
        userService.altaUsuario(id);

        // Redirige a la página que muestra la lista de todos los usuarios.
        return "redirect:/administrador/usuarios/all";
    }


    /**
     * Controlador para mostrar la página de asignación de material a evaluadores en la interfaz de administrador.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará la página de asignación de material a evaluadores.
     */
    @GetMapping("/evaluadores/asignar-material")
    public String showAsignarMaterial(Model model) {
        // Agrega la lista de materiales y la lista de evaluadores al modelo para que estén disponibles en la vista.
        model.addAttribute("materiales", materialService.getMateriales());
        model.addAttribute("evaluadores", userService.getAllEvaluadores());

        // Devuelve el nombre de la vista que mostrará la página de asignación de material a evaluadores.
        return "administrador/asignar-material-evaluador";
    }


    /**
     * Controlador para procesar la asignación de un material a un evaluador en la interfaz de administrador.
     *
     * @param materialId   La identificación del material a asignar, obtenida del path de la URL.
     * @param evaluadorId  La identificación del evaluador al que se asignará el material, obtenida del path de la URL.
     * @return Una respuesta ResponseEntity con un mensaje indicando el resultado de la asignación.
     */
    @PostMapping("/{materialId}/{evaluadorId}/asignar-material")
    public ResponseEntity<String> asignarMaterial(
            @PathVariable("materialId") Long materialId,
            @PathVariable("evaluadorId") Long evaluadorId
    ) {
        try {
            // Intenta asignar el material al evaluador utilizando el servicio userService.
            userService.asignarMaterialAEvaluador(materialId, evaluadorId);
        } catch (Exception e) {
            // Si ocurre un error durante la asignación, devuelve una respuesta ResponseEntity con un mensaje de error.
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        // Si la asignación es exitosa, devuelve una respuesta ResponseEntity con un mensaje de éxito.
        return ResponseEntity.ok("Material asignado al evaluador exitosamente");
    }


    /**
     * Controlador para mostrar la página de perfil del usuario autenticado en la interfaz de administrador.
     *
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @param authentication La información de autenticación que contiene al usuario autenticado.
     * @return El nombre de la vista que mostrará la página de perfil del usuario.
     */
    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        // Obtiene al usuario autenticado desde la información de autenticación.
        User sessionUser = (User) authentication.getPrincipal();

        // Agrega el usuario al modelo para que esté disponible en la vista.
        model.addAttribute("user", sessionUser);

        // Devuelve el nombre de la vista que mostrará la página de perfil del usuario.
        return "administrador/profile";
    }

}
