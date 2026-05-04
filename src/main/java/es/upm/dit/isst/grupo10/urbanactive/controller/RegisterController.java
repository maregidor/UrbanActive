package es.upm.dit.isst.grupo10.urbanactive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.ui.Model;

import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Identificacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Nivel;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.model.Valoracion;
import es.upm.dit.isst.grupo10.urbanactive.repository.OrganizacionRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;

@Controller
public class RegisterController {

    private final UsuarioRepository usuarioRepository;
    private final OrganizacionRepository organizacionRepository;

    public RegisterController(UsuarioRepository usuarioRepository,
                              OrganizacionRepository organizacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.organizacionRepository = organizacionRepository;
    }

    @GetMapping("/register")
    public String seleccionarTipo() {
        return "register-select";
    }

    @GetMapping("/register/usuario")
    public String formUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register-usuario";
    }

    @GetMapping("/register/organizacion")
    public String formOrganizacion(Model model) {
        model.addAttribute("organizacion", new Organizacion());
        return "register-organizacion";
    }


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @PostMapping("/register/usuario")
    public String guardarUsuario(
            @RequestParam String email,
            @RequestParam String nombre,
            @RequestParam String password,
            @RequestParam Double nivelExperienciaValor,
            @RequestParam String actividadInteres,
            RedirectAttributes ra) {

        try {
            Usuario usuario = new Usuario();

            usuario.setEmail(new Email(email)); // 👈 tú construyes el objeto
            usuario.setNombre(nombre);
            usuario.setPassword(passwordEncoder.encode(password));

            Nivel nivel = new Nivel();
            nivel.setValor(nivelExperienciaValor);
            usuario.setNivelExperiencia(nivel);

            usuario.setActividadInteres(actividadInteres);

            usuarioRepository.save(usuario);
            System.out.println("Usuario guardado con ID: " + usuario.getEmail() + " y contraseña encriptada: " + usuario.getPassword());
            return "redirect:/login";

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register/usuario";
        }
    }
    

    @PostMapping("/register/organizacion")
    public String guardarOrganizacion(
            @RequestParam String email,
            @RequestParam String nombre,
            @RequestParam String password,
            @RequestParam String cif,
            @RequestParam String actividad,
            RedirectAttributes ra) {

        try {
            Organizacion org = new Organizacion();

            // 👇 igual que antes
            org.setEmail(new Email(email));
            org.setNombre(nombre);
            org.setPassword(passwordEncoder.encode(password));
            org.setCif(cif);
            org.setActividad(actividad);

        // ⚠️ IMPORTANTE: inicializar embebidos si existen
            org.setValoracion(new Valoracion());
            org.setIdentificacion(new Identificacion("CIF", cif));

            organizacionRepository.save(org);
            System.out.println("Organización guardada con ID: " + org.getEmail() + " y contraseña encriptada: " + org.getPassword());
            return "redirect:/login";

        } catch (Exception e) {
            System.out.println("Error al guardar organización: " + e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register/organizacion";
        }
    }
}