package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Nivel;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String loginProcess(@RequestParam String email,
                             @RequestParam String nombre,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            // Crear usuario con los datos del formulario
            Email emailObj = new Email(email);
            Usuario usuario = new Usuario(emailObj, nombre, new Nivel(5.0));
            
            // Guardar usuario en la base de datos si no existe
            if (!usuarioRepository.existsById(emailObj)) {
                usuarioRepository.save(usuario);
            } else {
                // Si ya existe, obtener el usuario de la base de datos
                usuario = usuarioRepository.findById(emailObj).orElse(usuario);
            }
            
            // Guardar usuario en sesión
            session.setAttribute("usuarioLogueado", usuario);
            
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Bienvenido " + nombre + "!");
            return "redirect:/actividades";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Email inválido: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensajeExito", "Has cerrado sesión correctamente");
        return "redirect:/login";
    }
}
