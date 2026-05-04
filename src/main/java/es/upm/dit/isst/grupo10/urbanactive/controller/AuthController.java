package es.upm.dit.isst.grupo10.urbanactive.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    public AuthController() {
        
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        return "login";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensajeExito", "Has cerrado sesión correctamente");
        return "redirect:/login";
    }
}
