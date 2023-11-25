package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Material;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
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

    /*@GetMapping("/panel-administrador")
    public String panelAdministrador(Model model) {
        model.addAttribute("user", new User());
        return "administrador/panel-administrador";
    }*/

    //Esta es la pantalla de inicio o index//
    @GetMapping("/index")
    public String userInSession(Authentication authentication, Model model) {
        User usuario = (User) authentication.getPrincipal();
        model.addAttribute("usuario", usuario);
        return "administrador/panel-administrador";
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

    //Ver todos los materiales
    @GetMapping("/materiales/all")
    public String showMateriales(Model model) {
        model.addAttribute("materiales", materialService.getMateriales());
        return "administrador/materiales";
    }

    /*@GetMapping("/{id}/material")
    public String viewMaterial(@PathVariable("id") Long id, Model model) {
        model.addAttribute("material", materialService.getMaterial(id));
        return "admin/material";
    }*/

    /*mostrar materiales en revision*/
    @GetMapping("/materiales/pendientes-de-aprobacion")
    public String showMaterialesPendientesAprobaccion(Model model) {
        model.addAttribute("materiales", materialService.getMaterialesPendientesAprobacion());
        return "administrador/materiales-pendientes-aprobacion";
    }

    @GetMapping("/materiales/pendientes-de-evaluacion")
    public String showMaterialesPendientesEvaluacion(Model model) {
        List<Material> materiales = materialService.getMaterialesPendientesEvaluacion();
        Map<Material, List<User>> evaluadoresPendientes = new HashMap<>();
        for (Material material : materiales) {
            evaluadoresPendientes.put(material, userService.getEvaluadoresPendientes(material));
        }
        model.addAttribute("materiales", materiales);
        model.addAttribute("evaluadoresPendientes", evaluadoresPendientes);
        return "administrador/materiales-pendientes-evaluacion";
    }

    @PostMapping("/materiales/{id}/aprobar")
    public String aprobarMaterial(@PathVariable("id") Long id) {
        materialService.aprobarMaterial(id);
        return "redirect:/administrador/materiales/pendientes-de-aprobacion";
    }

    @PostMapping("/materiales/{id}/rechazar")
    public String rechazarMaterial(@PathVariable("id") Long id) {
        materialService.rechazarMaterial(id);
        return "redirect:/administrador/materiales/pendientes-de-aprobacion";
    }

    //Cargar los Usuario de tipo Evaluador//
    @GetMapping("/register/evaluador")
    public String registerEvaluador(Model model) {
        model.addAttribute("evaluador", new User());
        return "administrador/register-evaluador";
    }

    @PostMapping("/register/evaluador")
    public String createEvaluador(@Valid @ModelAttribute("evaluador") User user, BindingResult result, ModelMap model, @RequestParam("passwordConfirm") String passwordConfirm) {
        // Verifica si las contraseñas coinciden y si hay errores en el formulario de registro
        if (!user.getPassword().equals(passwordConfirm)) {
            result.rejectValue("password", "error.password", "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) {
            model.addAttribute("evaluador", user);
            return "administrador/register-evaluador";
        }

        // Intenta crear el usuario
        try {
            userService.createUser(user, User.Rol.EVALUADOR);
        } catch (Exception e) {  // Si ocurre un error, añade el mensaje de error al modelo y retorna la vista "register"
            result.rejectValue("email", "error.email", e.getMessage());
            model.addAttribute("evaluador", user);
            return "administrador/register-evaluador";
        }

        return "redirect:/administrador/evaluadores/registrados";
    }

    @GetMapping("/register/admin")
    public String registerAdministrador(Model model) {
        model.addAttribute("admin", new User());
        return "administrador/register-admin";
    }

    @PostMapping("/register/admin")
    public String createAdministrador(@Valid @ModelAttribute("admin") User user, BindingResult result, ModelMap model, @RequestParam("passwordConfirm") String passwordConfirm) {
        // Verifica si las contraseñas coinciden y si hay errores en el formulario de registro
        if (!user.getPassword().equals(passwordConfirm)) {
            result.rejectValue("password", "error.password", "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) {
            model.addAttribute("admin", user);
            return "administrador/register-admin";
        }

        // Intenta crear el usuario
        try {
            userService.createUser(user, User.Rol.ADMINISTRADOR);
        } catch (Exception e) {  // Si ocurre un error, añade el mensaje de error al modelo y retorna la vista "register"
            result.rejectValue("email", "error.email", e.getMessage());
            model.addAttribute("admin", user);
            return "administrador/register-admin";
        }

        return "redirect:/administrador/admin/registrados";
    }

    @GetMapping("/evaluadores/registrados")
    public String evaludoresDisponibles(Model model) {
        model.addAttribute("evaluadores", userService.getAllEvaluadores());
        return "administrador/evaluadores-registrados";
    }

    @GetMapping("/admin/registrados")
    public String administradoresDisponibles(Model model) {
        model.addAttribute("administradores", userService.getAllAdministradores());
        return "administrador/admin-registrados";
    }

    //funcion para asignar los materiales a los evaluadores//
    @GetMapping("/evaluadores/asignar-material")
    public String showAssignMaterialForm(Model model) {
        model.addAttribute("materiales", materialService.getMateriales());
        model.addAttribute("evaluadores", userService.getAllEvaluadores());
        return "administrador/asignar-material-evaluador";
    }

    @PostMapping("/{materialId}/{evaluadorId}/asignar-material")
    public ResponseEntity<String> asignarMaterial(@PathVariable("materialId") Long materialId, @PathVariable("evaluadorId") Long evaluadorId) {

        try {
            userService.asignarMaterialAEvaluador(materialId, evaluadorId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Material asignado al evaluador exitosamente");
    }
}
