package es.upm.dit.isst.grupo10.urbanactive.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.ui.Model;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.OrganizacionRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final OrganizacionRepository organizacionRepository;

    public PerfilController(UsuarioRepository usuarioRepository,
                            OrganizacionRepository organizacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.organizacionRepository = organizacionRepository;
    }

    @GetMapping("/perfil/usuario")
    public String perfilUsuario(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuario", usuario);
        return "perfil-usuario";
    }

    @PostMapping("/perfil/usuario")
    public String actualizarUsuario(Usuario usuario, HttpSession session) {
        usuarioRepository.save(usuario);
        session.setAttribute("usuarioLogueado", usuario);
        return "redirect:/perfil/usuario";
    }

    @GetMapping("/perfil/organizacion")
    public String perfilOrganizacion(HttpSession session, Model model) {
        Organizacion org = (Organizacion) session.getAttribute("organizacionLogueada");
        model.addAttribute("organizacion", org);
        return "perfil-organizacion";
    }

    @PostMapping("/perfil/organizacion")
    public String actualizarOrganizacion(Organizacion org, HttpSession session) {
        organizacionRepository.save(org);
        session.setAttribute("organizacionLogueada", org);
        return "redirect:/perfil/organizacion";
    }
}