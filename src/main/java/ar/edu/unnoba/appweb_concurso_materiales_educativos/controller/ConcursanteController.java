package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.ConcursoService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/concursante")
@PreAuthorize("hasRole('CONCURSANTE')")
public class ConcursanteController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ConcursoService concursoService;

    /**
     * Controlador para mostrar el panel del concursante.
     *
     * @param authentication La información de autenticación que contiene al usuario autenticado.
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista que mostrará el panel del concursante.
     */
    @GetMapping("/index")
    public String dashboardConcursante(Authentication authentication, Model model) {
        // Obtiene al usuario autenticado.
        User usuario = (User) authentication.getPrincipal();

        // Agrega el usuario al modelo.
        model.addAttribute("usuario", usuario);

        // Obtiene la edición del concurso actual, si existe.
        Concurso concurso = concursoService.getConcursoActual();
        String edicionActual = (concurso != null) ? concurso.getEdicion() : "null";
        model.addAttribute("edicionActual", edicionActual);

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
        // Obtiene el concurso actual.
        Concurso concursoActual = concursoService.getConcursoActual();

        // Agrega un objeto material vacío al modelo.
        model.addAttribute("material", new Material());

        // Si no hay un concurso vigente, muestra un mensaje de error en la vista de postulación de material.
        if (concursoActual == null) {
            model.addAttribute("error", "No puedes postular ningún material porque no hay un concurso vigente.");
        }

        // Devuelve el nombre de la vista que mostrará la página de postulación de material.
        return "concursante/postular-material";
    }


    /**
     * Controlador para procesar la creación y postulación de un nuevo material por parte del concursante.
     *
     * @param material        El objeto Material vinculado desde el formulario.
     * @param result          El resultado de la validación del objeto Material.
     * @param model           El modelo que se utilizará para pasar datos a la vista.
     * @param authentication  La información de autenticación del concursante.
     * @return Una redirección a la página que muestra los materiales del concursante o la página de postulación en caso de errores.
     */
    @Transactional
    @PostMapping("/postular-material")
    public String createMaterial(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute("material") Material material,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        // Verifica si hay errores de validación.
        if (result.hasErrors()) {
            // Devuelve a la vista de postulación de material con los errores de validación.
            return "concursante/postular-material";
        }

        // Obtiene el usuario autenticado.
        User sessionUser = (User) authentication.getPrincipal();
        // Obtiene el concurso actual.
        Concurso concurso = concursoService.getConcursoActual();

        // Verifica si hay un concurso vigente.
        if (concurso == null) {
            // Muestra un mensaje de error si no hay un concurso vigente y devuelve a la vista de postulación de material.
            return "redirect:/concursante/mis-materiales";
        }

        try {
            if (!file.isEmpty()) {
                // Guarda el archivo en el directorio de recursos estáticos
                String fileName = file.getOriginalFilename();
                String uploadsDir = "src/main/resources/static/file/";
                Path uploadPath = Paths.get(uploadsDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);

                // Actualiza la ruta del archivo en el objeto Material para que sea relativa a la raíz de la aplicación
                String relativeFilePath = "/file/" + fileName;
                material.setArchivo(relativeFilePath);
            }

            // Crea y postula el material utilizando el servicio.
            materialService.createMaterial(material, sessionUser, concurso);

            // Agrega un mensaje de éxito para mostrar en la página de materiales del concursante.
            redirectAttributes.addFlashAttribute("successMessage", "Material postulado exitosamente.");

            // Redirige a la página que muestra los materiales del concursante.
            return "redirect:/concursante/mis-materiales";
        } catch (IOException e) {
            // Maneja el error de procesamiento del archivo adjunto
            model.addAttribute("error", "Error al procesar el archivo adjunto: " + e.getMessage());
            return "concursante/postular-material";
        }
    }


    /**
     * Controlador para mostrar la página de materiales postulados por el concursante autenticado.
     *
     * @param authentication La información de autenticación que contiene al concursante autenticado.
     * @return Un objeto ModelAndView que representa la vista de los materiales del concursante.
     */
    @Transactional
    @GetMapping("/mis-materiales")
    public ModelAndView showMisMateriales(Authentication authentication) {
        // Obtiene al concursante autenticado desde la información de autenticación.
        User sessionUser = (User) authentication.getPrincipal();

        // Crea un objeto ModelAndView asociado a la vista "concursante/mis-materiales".
        ModelAndView modelAndView = new ModelAndView("concursante/mis-materiales");

        // Obtiene todos los materiales postulados por el concursante.
        List<Material> materialesConcursante = materialService.getMaterialesByConcursante(sessionUser);

        // Obtiene la edición del concurso actual, si existe.
        Concurso concursoActual = concursoService.getConcursoActual();
        String edicionActual = (concursoActual != null) ? concursoActual.getEdicion() : null;

        // Filtra los materiales del concursante según la edición del concurso.
        List<Material> materialesEdicionActual = new ArrayList<>();
        List<Material> materialesEdicionesAnteriores;
        if (edicionActual != null) {
            // Filtra los materiales postulados por el concursante para la edición actual del concurso.
            materialesEdicionActual = materialesConcursante.stream()
                    .filter(material -> material.getConcurso() != null && material.getConcurso().getEdicion().equals(edicionActual))
                    .toList();

            // Filtra los materiales postulados por el concursante para ediciones anteriores del concurso.
            materialesEdicionesAnteriores = materialesConcursante.stream()
                    .filter(material -> material.getConcurso() != null && !material.getConcurso().getEdicion().equals(edicionActual))
                    .toList();
        } else {
            // Si no hay una edición actual del concurso, todos los materiales son considerados como ediciones anteriores.
            materialesEdicionesAnteriores = materialesConcursante;
        }

        // Agrega las listas de materiales al modelo para que estén disponibles en la vista.
        modelAndView.addObject("materialesEdicionActual", materialesEdicionActual);
        modelAndView.addObject("materialesEdicionesAnteriores", materialesEdicionesAnteriores);

        // Retorna el objeto ModelAndView que representa la vista de los materiales del concursante.
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
