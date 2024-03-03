package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.ConcursoService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.io.File;

@Controller
@RequestMapping("/")
public class HomeController {

    private final MaterialService materialService;

    private final UserService userService;

    private final ConcursoService concursoService;

    @Autowired
    public HomeController(MaterialService materialService, UserService userService, ConcursoService concursoService) {
        this.materialService = materialService;
        this.userService = userService;
        this.concursoService = concursoService;
    }

    /**
     * Controlador para redirigir a la página principal del sistema.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que representa la página principal del sistema.
     */
    @GetMapping("/")
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

    /**
     * Controlador para mostrar la página de inicio de sesión.
     *
     * @param error          El tipo de error que se produjo durante el inicio de sesión.
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @param authentication La autenticación del usuario que ha iniciado sesión.
     * @return El nombre de la vista que representa la página de inicio de sesión.
     */
    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error, Model model, Authentication authentication) {
        // Si el usuario ya está autenticado, redirige a la página de inicio correspondiente
        if (authentication != null && authentication.isAuthenticated()) {
            return defaultAfterLogin(authentication);
        }

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
     * Controlador para redirigir a la página por defecto después de un inicio de sesión exitoso.
     *
     * @param authentication La autenticación del usuario que ha iniciado sesión.
     * @return La URL de redirección después de un inicio de sesión exitoso.
     */
    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        // Verifica el rol del usuario y redirige a la URL correspondiente.
        if (authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
            return "redirect:/administrador/index";
        } else if (authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_CONCURSANTE"))) {
            return "redirect:/concursante/index";
        } else if (authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_EVALUADOR"))) {
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
     * @param redirectAttrs Los atributos de redirección que se utilizarán para enviar mensajes al cliente.
     * @return El nombre de la vista que representa la lista de materiales participantes.
     */
    @GetMapping("/materiales-participantes/{edicion}")
    public String showMaterialesParticipantes(Model model, @PathVariable String edicion,RedirectAttributes redirectAttrs, HttpServletRequest request) {
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
            List<Material> materialesGanadores = materialService.getMaterialGanador(concurso);
            model.addAttribute("materialesGanadores", materialesGanadores);
            // Agrega la edición al modelo para que esté disponible en la vista.
            model.addAttribute("edicion", edicion);
        } else {
            // Si la edición es nula, agrega null al modelo para indicarlo.
            model.addAttribute("edicion", null);
        }

        //Agrega el mensaje del redirect
        if (redirectAttrs.getFlashAttributes().containsKey("message")) {
            model.addAttribute("message", redirectAttrs.getFlashAttributes().get("message"));
        }

        // Devuelve el nombre de la vista que representa la lista de materiales participantes.
        return "materiales-participantes";
    }


    /**
     * Controlador para dar "like" a un material participante.
     *
     * @param idmaterial     El id del material al que se le dará "like".
     * @param response       La respuesta HTTP que se utilizará para enviar cookies al cliente.
     * @param request        La solicitud HTTP que se utilizará para obtener cookies del cliente.
     * @param redirectAttrs  Los atributos de redirección que se utilizarán para enviar mensajes al cliente.
     * @return La URL de redirección después de dar "like" a un material.
     */
    @Transactional
    @PostMapping("/materiales-participantes/{idmaterial}/like")
    public String likeMaterial(@PathVariable Long idmaterial, HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        // Obtener material educativo
        Material material = materialService.getMaterial(idmaterial);

        // Obtener el valor de 'edicion'
        String edicion = material.getConcurso().getEdicion();
        edicion = edicion.replace(" ", "-");

        // Verifica si el usuario ya ha dado "like" a este material.
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("like_" + idmaterial)) {
                    redirectAttrs.addFlashAttribute("message", "Ya has dado like a este material.");
                    return "redirect:/materiales-participantes/" + edicion;
                }
            }
        }

        // El usuario no ha dado "like" a este material, así que incrementamos el contador de "likes" del material.
        materialService.darLikeMaterial(material);

        // Crea una nueva cookie para recordar que el usuario ya ha dado "like" a este material.
        Cookie likeCookie = new Cookie("like_" + idmaterial, "true");
        likeCookie.setMaxAge(60 * 60 * 24 * 365); // La cookie expira después de un año.
        response.addCookie(likeCookie);

        //Agrega mensaje al redirect
        redirectAttrs.addFlashAttribute("message", "Sumaste un like al material " + material.getTitulo() + ". Gracias por tu voto!");

        return "redirect:/materiales-participantes/" + edicion;
    }


    /**
     * Controlador para descargar un archivo de un material.
     *
     * @param id El id del material del cual se descargará el archivo.
     * @return La respuesta HTTP que contiene el archivo para descargar.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) throws IOException {
        // Obtiene el material con el id especificado desde el servicio materialService.
        Material material = materialService.getMaterial(id);
        // Verifica si el material o su archivo son nulos.
        if (material == null || material.getArchivo() == null) {
            throw new FileNotFoundException("No se encontró el archivo para descargar.");
        }

        // Obtiene el archivo del material.
        String filePath = material.getArchivo();
        File file = new File(filePath);

        // Verifica si el archivo existe.
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        // Crea un recurso de InputStream para el archivo.
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // Devuelve una respuesta con el archivo para descargar.
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
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
