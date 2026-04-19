package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Identificacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoOrganizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.OrganizacionRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.SeguimientoOrganizacionRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PerfilController {

    private final OrganizacionRepository organizacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final SeguimientoOrganizacionRepository seguimientoOrganizacionRepository;

    public PerfilController(OrganizacionRepository organizacionRepository,
                            UsuarioRepository usuarioRepository,
                            SeguimientoOrganizacionRepository seguimientoOrganizacionRepository) {
        this.organizacionRepository = organizacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.seguimientoOrganizacionRepository = seguimientoOrganizacionRepository;
    }

    @GetMapping("/organizaciones/{tipo}/{numero}")
    public String verPerfilOrganizacion(@PathVariable String tipo,
                                        @PathVariable String numero,
                                        Model model) {
        Identificacion id = new Identificacion(tipo, numero);
        Optional<Organizacion> organizacionOpt = organizacionRepository.findById(id);

        if (organizacionOpt.isEmpty()) {
            return "redirect:/actividades";
        }

        Organizacion perfil = organizacionOpt.get();
        Usuario usuarioActual = getUsuarioAutenticado();

        boolean yaLaSigue = false;
        if (usuarioActual != null) {
            yaLaSigue = seguimientoOrganizacionRepository
                    .existsBySeguidorAndOrganizacion(usuarioActual, perfil);
        }

        long numSeguidores = seguimientoOrganizacionRepository.countByOrganizacion(perfil);

        // Mantiene compatibilidad con la plantilla actual y con futuros usos del nombre "perfil".
        model.addAttribute("perfil", perfil);
        model.addAttribute("organizacion", perfil);
        model.addAttribute("yaLaSigue", yaLaSigue);
        model.addAttribute("numSeguidores", numSeguidores);

        return "perfil-organizacion";
    }

    @PostMapping("/organizaciones/{tipo}/{numero}/seguir")
    public String seguirOrganizacion(@PathVariable String tipo,
                                     @PathVariable String numero) {
        Identificacion id = new Identificacion(tipo, numero);
        Optional<Organizacion> organizacionOpt = organizacionRepository.findById(id);
        Usuario usuarioActual = getUsuarioAutenticado();

        if (organizacionOpt.isPresent() && usuarioActual != null) {
            Organizacion organizacion = organizacionOpt.get();

            boolean yaExiste = seguimientoOrganizacionRepository
                    .existsBySeguidorAndOrganizacion(usuarioActual, organizacion);

            if (!yaExiste) {
                seguimientoOrganizacionRepository
                        .save(new SeguimientoOrganizacion(usuarioActual, organizacion));
            }
        }

        return "redirect:/organizaciones/" + tipo + "/" + numero;
    }

    @PostMapping("/organizaciones/{tipo}/{numero}/dejar-seguir")
    public String dejarSeguirOrganizacion(@PathVariable String tipo,
                                          @PathVariable String numero) {
        Identificacion id = new Identificacion(tipo, numero);
        Optional<Organizacion> organizacionOpt = organizacionRepository.findById(id);
        Usuario usuarioActual = getUsuarioAutenticado();

        if (organizacionOpt.isPresent() && usuarioActual != null) {
            seguimientoOrganizacionRepository
                    .deleteBySeguidorAndOrganizacion(usuarioActual, organizacionOpt.get());
        }

        return "redirect:/organizaciones/" + tipo + "/" + numero;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
            return null;
        }

        return usuarioRepository.findById(new Email(auth.getName())).orElse(null);
    }
}
