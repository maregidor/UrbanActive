package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoOrganizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoUsuario;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.ActividadRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.SeguimientoOrganizacionRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.SeguimientoUsuarioRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MisSeguidosController {

    private final UsuarioRepository usuarioRepository;
    private final SeguimientoUsuarioRepository seguimientoUsuarioRepository;
    private final SeguimientoOrganizacionRepository seguimientoOrganizacionRepository;
    private final ActividadRepository actividadRepository;

    public MisSeguidosController(UsuarioRepository usuarioRepository,
                                 SeguimientoUsuarioRepository seguimientoUsuarioRepository,
                                 SeguimientoOrganizacionRepository seguimientoOrganizacionRepository,
                                 ActividadRepository actividadRepository) {
        this.usuarioRepository = usuarioRepository;
        this.seguimientoUsuarioRepository = seguimientoUsuarioRepository;
        this.seguimientoOrganizacionRepository = seguimientoOrganizacionRepository;
        this.actividadRepository = actividadRepository;
    }

    @GetMapping("/mis-seguidos")
    public String verMisSeguidos(Model model) {
        Usuario usuarioActual = getUsuarioAutenticado();

        if (usuarioActual == null) {
            return "redirect:/login";
        }

        List<Usuario> usuariosSeguidos = seguimientoUsuarioRepository.findBySeguidorConSeguido(usuarioActual)
                .stream()
                .map(SeguimientoUsuario::getSeguido)
                .collect(Collectors.toList());

        List<Organizacion> organizacionesSeguidas = seguimientoOrganizacionRepository.findBySeguidorConOrganizacion(usuarioActual)
                .stream()
                .map(SeguimientoOrganizacion::getOrganizacion)
                .collect(Collectors.toList());

        List<Actividad> actividades = new ArrayList<>();

        if (!usuariosSeguidos.isEmpty()) {
            actividades.addAll(actividadRepository.findByUsuarioOrganizadorIn(usuariosSeguidos));
        }

        if (!organizacionesSeguidas.isEmpty()) {
            actividades.addAll(actividadRepository.findByOrganizacionIn(organizacionesSeguidas));
        }

        actividades.sort(Comparator.comparing(
                Actividad::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        model.addAttribute("usuariosSeguidos", usuariosSeguidos);
        model.addAttribute("organizacionesSeguidas", organizacionesSeguidas);
        model.addAttribute("actividades", actividades);

        return "mis-seguidos";
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
            return null;
        }

        return usuarioRepository.findById(new Email(auth.getName())).orElse(null);
    }
}