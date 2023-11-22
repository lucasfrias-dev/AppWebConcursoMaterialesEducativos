package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

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
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/administrador")
@PreAuthorize("hasRole('ROLE_ADMINISTRADOR')") /*solo los administradores acceden a este controlador*/
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
    @GetMapping("/")
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

    /*mostrar materiales en revision*/
    @GetMapping("/materiales/pendientes")
    public String showMaterialesPendientes(Model model) {
        model.addAttribute("materiales", materialService.getMaterialesPendientes());
        return "administrador/materiales-pendientes";
    }

    /*@GetMapping("/{id}/material")
    public String viewMaterial(@PathVariable("id") Long id, Model model) {
        model.addAttribute("material", materialService.getMaterial(id));
        return "admin/material";
    }*/

    @PostMapping("materiales/{id}/aprobar")
    public String aprobarMaterial(@PathVariable("id") Long id) {
        materialService.updateAprobado(id);
        return "redirect:/administrador/materiales-pendientes";
    }

    @GetMapping("materiales/{id}/rechazar")
    public String rechazarMaterial(@PathVariable("id") Long id) {
        materialService.updateRechazado(id);
        return "redirect:administrador/materiales-pendientes";
    }
    //Cargar los Usuario de tipo Evaluador//
    @GetMapping("/register-evaluador")
    public String registerEvaludor(Model model) {
        model.addAttribute("evaluador", new User());
        return "redirect:administrador/register-evaluador";
    }

    @PostMapping("/register-evaluador")
    public String createEvaluador(@Valid @ModelAttribute("user") User user) throws Exception {
        userService.createUser(user, User.Rol.EVALUADOR);
        return "administrador/register-evaluador";
    }

    @GetMapping("/evaluadores-registrados")
    public String evaludoresDisponibles(Model model) {
        model.addAttribute("evaluadores", userService.getAllEvaluadores());
        return "redirect:administrador/evaluadores-registrados";
    }
    //Ver descripcion de los evaluadores//
    @GetMapping("/{id}/ver-evaludor")
    public String verEvaluador(@PathVariable("id") Long id, Model model) {
        model.addAttribute("evaluador", userService.findById(id));
        return "redirect:administrador/ver-evaludor";
    }
    //funcion para asignar los materiales a los evaluadores//

    @GetMapping("/materiales/asignar-material-evaluador")
    public String showAssignMaterialForm(Model model) {
        model.addAttribute("materiales", materialService.getMateriales());
        model.addAttribute("evaluadores", userService.getAllEvaluadores());
        return "administrador/asignar-material-evaluador";
    }

    @PostMapping("/materiales/asignar-material-evaluador")
    public ResponseEntity<String> asignarMaterial(@RequestParam Long materialId, @RequestParam Long evaluadorId) {
        /*Material material=materialService.getMaterial(id2);
        User user=userService.findById(id);
        user.getMaterialesAEvaluar().add(material);
        userService.save(user);*/ //Esto va en la parte de servicio
        userService.asignarMaterialAEvaluador(materialId, evaluadorId);
        return ResponseEntity.ok("Material asignado al evaluador exitosamente");
    }
}
