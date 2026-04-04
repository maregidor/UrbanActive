package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        return "redirect:/actividades";
    }
}
