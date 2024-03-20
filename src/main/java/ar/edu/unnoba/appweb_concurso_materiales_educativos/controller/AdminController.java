package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Evaluacion;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.ConcursoService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.EvaluacionService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.email.EmailService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/administrador")
@PreAuthorize("hasRole('ADMINISTRADOR')") /*solo los administradores acceden a este controlador*/
public class AdminController {
    private final UserService userService;
    private final MaterialService materialService;
    private final ConcursoService concursoService;
    private final EvaluacionService evaluacionService;
    private final EmailService emailService;

    @Autowired
    public AdminController(UserService userService, MaterialService materialService, ConcursoService concursoService, EvaluacionService evaluacionService, EmailService emailService) {
        this.userService = userService;
        this.materialService = materialService;
        this.concursoService = concursoService;
        this.evaluacionService = evaluacionService;
        this.emailService = emailService;
    }

    /**
     * Método controlador para mostrar el panel del administrador en la vista.
     *
     * @param authentication La información de autenticación del usuario.
     * @param model El modelo utilizado para pasar datos a la vista.
     * @param error Mensaje de error a mostrar en la vista.
     * @param message Mensaje informativo a mostrar en la vista.
     * @return El nombre de la vista que muestra el panel del administrador.
     */
    @GetMapping("/index")
    public String dashboardAdministrador(Authentication authentication, Model model, @ModelAttribute("error") String error, @ModelAttribute("message") String message) {
        // Obtiene el usuario autenticado desde la información de autenticación.
        User usuario = (User) authentication.getPrincipal();

        // Agrega el usuario al modelo para que esté disponible en la vista.
        model.addAttribute("usuario", usuario);

        // Agrega el concurso actual al modelo para que esté disponible en la vista.
        model.addAttribute("concursoActual", concursoService.getConcursoActual());

        // Agrega la lista de concursos anteriores al modelo para que esté disponible en la vista.
        model.addAttribute("concursosAnteriores", concursoService.getConcursosAnteriores());

        // Agrega un mensaje de error al modelo para que esté disponible en la vista.
        model.addAttribute("error", error);
        // Agrega un mensaje al modelo para que esté disponible en la vista.
        model.addAttribute("message", message);

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
     * Método controlador para mostrar el formulario de creación de un nuevo concurso en la vista.
     *
     * @param model El modelo utilizado para pasar datos a la vista.
     * @return El nombre de la vista que muestra el formulario de creación de un nuevo concurso.
     */
    @GetMapping("/new-concurso")
    public String ShowCreateConcurso(Model model) {
        // Agrega un nuevo objeto Concurso al modelo para inicializar el formulario.
        model.addAttribute("concurso", new Concurso());

        // Devuelve el nombre de la vista que mostrará el formulario de creación de un nuevo concurso.
        return "administrador/new-concurso";
    }

    /**
     * Método controlador para crear un nuevo concurso.
     *
     * @param concurso El objeto Concurso a crear, obtenido del formulario.
     * @param result El resultado de la validación del objeto Concurso.
     * @param model El modelo utilizado para pasar datos a la vista en caso de error.
     * @param redirectAttributes Los atributos de redirección para agregar mensajes flash.
     * @return Una redirección a la página de inicio del administrador si el concurso se crea con éxito,
     *         de lo contrario, muestra nuevamente el formulario de creación de concurso con mensajes de error.
     */
    @PostMapping("/new-concurso")
    public String createConcurso(@Valid @ModelAttribute("concurso") Concurso concurso, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // Verifica si hay errores de validación en el objeto Concurso.
        if (result.hasErrors()) {
            // Si hay errores, vuelve a mostrar el formulario de creación de concurso con los errores.
            model.addAttribute("concurso", concurso);
            return "administrador/new-concurso";
        }

        // Si no hay errores de validación, crea el concurso utilizando el servicio correspondiente.
        concursoService.createConcurso(concurso);

        // Agrega un mensaje de éxito como atributo flash para que esté disponible después de la redirección.
        redirectAttributes.addFlashAttribute("success", "Concurso creado exitosamente");

        // Redirige al usuario a la página de inicio del administrador.
        return "redirect:/administrador/index";
    }

    /**
     * Método controlador para cerrar un concurso específico.
     *
     * @param edicion La edición del concurso a cerrar, obtenida como parte de la URL.
     * @param redirectAttributes Los atributos de redirección para agregar mensajes flash.
     * @return Una redirección a la página de inicio del administrador con un mensaje de éxito si el concurso se cierra con éxito,
     *         de lo contrario, redirige con un mensaje de error.
     */
    @PostMapping("/concurso/{edicion}/cerrar")
    public String cerrarConcurso(@PathVariable String edicion, RedirectAttributes redirectAttributes) {
        // Reemplaza cualquier guión en el nombre de la edición con un espacio para manejar correctamente los nombres compuestos.
        edicion = edicion.replace("-", " ");
        try {
            // Intenta cerrar el concurso utilizando el servicio correspondiente.
            concursoService.cerrarConcurso(edicion);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Si se produce una excepción durante el cierre del concurso, agrega un mensaje de error como atributo flash.
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Redirige al usuario a la página de inicio del administrador.
            return "redirect:/administrador/index";
        }
        // Si el concurso se cierra con éxito, agrega un mensaje de éxito como atributo flash.
        redirectAttributes.addFlashAttribute("message", "Concurso cerrado exitosamente.");
        // Redirige al usuario a la página de inicio del administrador.
        return "redirect:/administrador/index";
    }

    /**
     * Método controlador para mostrar el formulario de reabrir un concurso específico.
     *
     * @param edicion La edición del concurso a reabrir, obtenida como parte de la URL.
     * @param model El modelo utilizado para pasar datos a la vista.
     * @return El nombre de la vista que muestra el formulario de reabrir concurso.
     */
    @GetMapping("/concurso/{edicion}/reabrir")
    public String reabrirConcurso(@PathVariable String edicion, Model model) {
        // Reemplaza cualquier guión en el nombre de la edición con un espacio para manejar correctamente los nombres compuestos.
        edicion = edicion.replace("-", " ");
        // Obtiene el concurso específico por su edición utilizando el servicio correspondiente.
        Concurso concurso = concursoService.getConcursoByEdicion(edicion);
        // Agrega el concurso al modelo para que esté disponible en la vista.
        model.addAttribute("concurso", concurso);
        // Devuelve el nombre de la vista que muestra el formulario de reabrir concurso.
        return "administrador/reabrir-concurso";
    }

    /**
     * Método controlador para reabrir un concurso específico.
     *
     * @param edicion La edición del concurso a reabrir, obtenida como parte de la URL.
     * @param fechaFin La nueva fecha de finalización del concurso, obtenida como parámetro de solicitud.
     * @param redirectAttributes Los atributos de redirección para agregar mensajes flash.
     * @return Una redirección a la página de inicio del administrador con un mensaje de éxito si el concurso se reabre con éxito,
     *         de lo contrario, redirige con un mensaje de error.
     */
    @PostMapping("/concurso/{edicion}/reabrir")
    public String reabrirConcurso(@PathVariable String edicion, @RequestParam("fechaFin") LocalDateTime fechaFin, RedirectAttributes redirectAttributes) {
        // Reemplaza cualquier guión en el nombre de la edición con un espacio para manejar correctamente los nombres compuestos.
        edicion = edicion.replace("-", " ");
        try {
            // Intenta reabrir el concurso con la nueva fecha de finalización utilizando el servicio correspondiente.
            concursoService.reabrirConcurso(edicion, fechaFin);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Si se produce una excepción durante la reapertura del concurso, agrega un mensaje de error como atributo flash.
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Redirige al usuario a la página de inicio del administrador.
            return "redirect:/administrador/index";
        }
        // Si el concurso se reabre con éxito, agrega un mensaje de éxito como atributo flash.
        redirectAttributes.addFlashAttribute("message", "Concurso reabierto exitosamente.");
        // Redirige al usuario a la página de inicio del administrador.
        return "redirect:/administrador/index";
    }

    /**
     * Método controlador para mostrar todos los materiales asociados a un concurso específico.
     *
     * @param model El modelo utilizado para pasar datos a la vista.
     * @param edicion La edición del concurso para la cual se mostrarán los materiales, obtenida como parte de la URL.
     * @return El nombre de la vista que muestra todos los materiales asociados al concurso.
     */
    @GetMapping("/materiales/{edicion}/all")
    @Transactional
    public String showMateriales(Model model, @PathVariable String edicion) {
        // Reemplaza cualquier guión en el nombre de la edición con un espacio para manejar correctamente los nombres compuestos.
        edicion = edicion.replace("-", " ");

        // Obtiene el concurso específico por su edición utilizando el servicio correspondiente.
        Concurso concurso = concursoService.getConcursoByEdicion(edicion);

        // Obtiene todos los materiales asociados al concurso utilizando el servicio correspondiente.
        List<Material> materiales = materialService.getMaterialesByConcurso(concurso);

        // Crea un mapa para almacenar las evaluaciones por material.
        Map<Long, List<Evaluacion>> evaluacionesPorMaterial = new HashMap<>();
        // Itera sobre cada material para obtener sus evaluaciones y las almacena en el mapa.
        for (Material material : materiales) {
            List<Evaluacion> evaluaciones = evaluacionService.getEvaluacionesByMaterial(material);
            evaluacionesPorMaterial.put(material.getId(), evaluaciones);
        }

        // Agrega los materiales y las evaluaciones por material al modelo para que estén disponibles en la vista.
        model.addAttribute("materiales", materiales);
        model.addAttribute("evaluacionesPorMaterial", evaluacionesPorMaterial);

        // Devuelve el nombre de la vista que muestra todos los materiales asociados al concurso.
        return "administrador/materiales";
    }

    /**
     * Controlador para mostrar todos los materiales pendientes de aprobación en la interfaz de administrador.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @param edicion La edición del concurso para la cual se mostrarán los materiales pendientes de aprobación, obtenida como parte de la URL.
     * @return El nombre de la vista que mostrará la lista de materiales pendientes de aprobación.
     */
    @GetMapping("/materiales/{edicion}/pendientes-de-aprobacion")
    public String showMaterialesPendientesAprobacion(Model model, @PathVariable String edicion) {
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
     * @param edicion La edición del concurso para la cual se mostrarán los materiales pendientes de evaluación, obtenida como parte de la URL.
     * @return El nombre de la vista que mostrará la lista de materiales pendientes de evaluación.
     */
    @GetMapping("/materiales/{edicion}/pendientes-de-evaluacion")
    @Transactional
    public String showMaterialesPendientesEvaluacion(Model model, @PathVariable String edicion) {
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

        // Obtiene el concurso actual para construir la URL de redirección.
        Concurso concurso = concursoService.getConcursoActual();
        // Reemplaza los espacios en blanco en la edición del concurso con guiones para construir la URL.
        String edicion = concurso.getEdicion().replace(" ", "-");
        // Redirige a la página que muestra la lista de materiales pendientes de aprobación.
        return "redirect:/administrador/materiales/" + edicion + "/pendientes-de-aprobacion";
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

        // Obtiene el concurso actual para construir la URL de redirección.
        Concurso concurso = concursoService.getConcursoActual();
        // Reemplaza los espacios en blanco en la edición del concurso con guiones para construir la URL.
        String edicion = concurso.getEdicion().replace(" ", "-");
        // Redirige a la página que muestra la lista de materiales pendientes de aprobación.
        return "redirect:/administrador/materiales/" + edicion + "/pendientes-de-aprobacion";
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
            emailService.sendEmail(user.getEmail(), "Haz sido dado de alta como Evaluador en el CED", "¡Bienvenido a la plataforma de concursos de materiales educativos! Ya forma parte del equipo de evaluadores del CED.");
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
            emailService.sendEmail(user.getEmail(), "Haz sido dado de alta como Administrador en el CED", "¡Bienvenido a la plataforma de concursos de materiales educativos! Ya forma parte del equipo de administradores del CED.");
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
    public String showEvaludoresDisponibles(Model model) {
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
    public String bajaUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Realiza la baja del usuario utilizando el servicio userService.
            userService.bajaUsuario(id);
        } catch (IllegalArgumentException e) {
            // Agrega un mensaje de error al modelo para que esté disponible en la vista.
            redirectAttributes.addFlashAttribute("error", e.getMessage());

            // Redirige a la misma página de baja de usuario.
            return "redirect:/administrador/usuarios/" + id + "/bajaUsuario";
        }

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
        model.addAttribute("materiales", materialService.getMaterialesParticipantesByConcurso(concursoService.getConcursoActual()));
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

            // Obtiene la dirección de correo electrónico del evaluador.
            String email = userService.findById(evaluadorId).getEmail();
            String nombre = userService.findById(evaluadorId).getNombre() + " " +
                    userService.findById(evaluadorId).getApellido();

            // Envía un correo electrónico al evaluador para notificarle la asignación del material.
            emailService.sendEmail(email, "Asignación de material", "Estimado/a " + nombre + ",\n\n" +
                    "Se le ha asignado un material para su evaluación. Por favor, inicie sesión en la plataforma del CED " +
                    "para revisar el material y realizar su evaluación.\n\n" +
                    "Atentamente,\n" +
                    "El equipo de Concurso de Materiales Educativos - CED");

        } catch (Exception e) {
            e.printStackTrace();
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


    @GetMapping("/materiales/{edicion}/participantes")
    @Transactional
    public String showMaterialesAprobados(Model model, @PathVariable String edicion, RedirectAttributes redirectAttrs, HttpServletRequest request) {
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
            // Obtiene la lista de materiales ganadores del concurso actual desde el servicio materialService.
            List<Material> materialesGanadores = materialService.getMaterialesGanadores(concurso);
            // Agrega la lista de materiales ganadores al modelo para que esté disponible en la vista.
            model.addAttribute("materialesGanadores", materialesGanadores);
            // Agrega la edición al modelo para que esté disponible en la vista.
            model.addAttribute("edicion", edicion);

            // Crea un mapa para almacenar las evaluaciones por material.
            Map<Long, List<Evaluacion>> evaluacionesPorMaterial = new HashMap<>();
            // Itera sobre cada material para obtener sus evaluaciones y las almacena en el mapa.
            for (Material material : materialesParticipantes) {
                List<Evaluacion> evaluaciones = evaluacionService.getEvaluacionesByMaterial(material);
                evaluacionesPorMaterial.put(material.getId(), evaluaciones);
            }
            // Agrega las evaluaciones por material al modelo para que estén disponibles en la vista.
            model.addAttribute("evaluacionesPorMaterial", evaluacionesPorMaterial);
        } else {
            // Si la edición es nula, agrega null al modelo para indicarlo.
            model.addAttribute("edicion", null);
        }

        //Agrega el mensaje del redirect
        if (redirectAttrs.getFlashAttributes().containsKey("message")) {
            model.addAttribute("message", redirectAttrs.getFlashAttributes().get("message"));
        }

        // Devuelve el nombre de la vista que representa la lista de materiales participantes.
        return "administrador/materiales-participantes";
    }

    @PostMapping("/materiales/{edicion}/participantes/{idmaterial}/ganador")
    @Transactional
    public String ganadorMaterial(@PathVariable("idmaterial") Long materialId, @PathVariable("edicion") String edicion, RedirectAttributes redirectAttrs){

        // Establece el material como ganador.
        materialService.setMaterialGanador(materialId);
        // Agrega un mensaje de éxito como atributo flash para que esté disponible después de la redirección.
        redirectAttrs.addFlashAttribute("message", "Material declarado como ganador con éxito");

        // Redirige a la página que muestra la lista de materiales participantes.
        return "redirect:/administrador/materiales/" + edicion + "/participantes";
    }

    @PostMapping("/materiales/{edicion}/participantes/{materialId}/remove-ganador")
    public String deshacerGanadorMaterial(@PathVariable("materialId") Long materialId, @PathVariable("edicion") String edicion) {
        // Se llama al servicio materialService para quitar el material como ganador.
        materialService.removeMaterialGanador(materialId);
        // Después de quitar el material como ganador, se redirige al usuario a la página de materiales participantes de esa edición del concurso.
        return "redirect:/administrador/materiales/" + edicion + "/participantes";
    }
}
