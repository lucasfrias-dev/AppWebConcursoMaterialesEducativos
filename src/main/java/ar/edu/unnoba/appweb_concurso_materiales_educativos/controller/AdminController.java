package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.User;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.MaterialService;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrador")
@PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private MaterialService materialService;

    @GetMapping("/")
    public String panelAdministrador(Model model) {
        model.addAttribute("user", new User());
        return "administrador/panel-administrador";
    }

    //Esta es la pantalla de inicio o index//
    @GetMapping("/index")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public String userInSession(Authentication authentication, Model model) {
        User usuario= (User) authentication.getPrincipal();
        model.addAttribute("usuario", usuario);
        return "admin/index";
    }

    /*mostrar materiales en revision*/
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/materiales/pendientes")
    public String showMaterialesPendientes(Model model) {
        model.addAttribute("materiales", materialService.getMaterialesPendientes());
        return "admin/administrar-material";
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")  /*solo los administradores pueden aprovar materiales*/
    @GetMapping("/{id}/material")
    public String viewMaterial(@PathVariable("id") Long id, Model model) {
        model.addAttribute("material", materialService.getMaterial(id));
        return "admin/material";
    }

    /*aprobar Material*/
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")  /*solo los administradores pueden aprovar materiales*/
    @GetMapping("/{id}/aprobar")
    public String aprobarMaterial(@PathVariable("id") Long id) {
        materialService.updateAprobado(id);
        return "redirect:/admin/admin/administrarmaterial";
    }

    /*aprobar Material*/
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")  /*solo los administradores pueden aprovar materiales*/
    @GetMapping("/{id}/rechazar")
    public String rechazarMaterial(@PathVariable("id") Long id) {
        materialService.updateRechazado(id);
        return "redirect:/admin/admin/administrarmaterial";
    }
}