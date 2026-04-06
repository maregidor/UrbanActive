package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadService;
import es.upm.dit.isst.grupo10.urbanactive.service.ReservaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.upm.dit.isst.grupo10.urbanactive.dto.ActividadContexto;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadContextService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ActividadController {

private final ActividadService actividadService;
private final ReservaService reservaService;
private final UsuarioRepository usuarioRepository;

private final ActividadContextService actividadContextService;

public ActividadController(ActividadService actividadService,
                           ReservaService reservaService,
                           UsuarioRepository usuarioRepository,
                           ActividadContextService actividadContextService) {
    this.actividadService = actividadService;
    this.reservaService = reservaService;
    this.usuarioRepository = usuarioRepository;
    this.actividadContextService = actividadContextService;
}

@GetMapping("/actividades")
public String listarActividades(Model model) {
model.addAttribute("actividades", actividadService.getActividades());
return "actividades";
}

@GetMapping("/actividades/{id}")
public String verDetalle(@PathVariable Long id,
                         @RequestParam(required = false) Double userLat,
                         @RequestParam(required = false) Double userLon,
                         Model model) {
    Actividad actividad = actividadService.getActividadById(id);
    if (actividad == null) {
        return "redirect:/actividades";
    }

    Usuario usuario = getUsuarioAutenticado();

    boolean yaReservada = false;
    if (usuario != null) {
        yaReservada = reservaService.yaTieneReservaActiva(usuario, id);
    }

    GeoPoint userPoint = null;
    if (userLat != null && userLon != null) {
        userPoint = new GeoPoint(userLat, userLon, "Mi ubicación");
    }

    ActividadContexto contexto = actividadContextService.getContexto(actividad, userPoint);

    model.addAttribute("actividad", actividad);
    model.addAttribute("yaReservada", yaReservada);
    model.addAttribute("contexto", contexto);

    return "actividad-detalle";
}

private Usuario getUsuarioAutenticado() {
Authentication auth = SecurityContextHolder.getContext().getAuthentication();

if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
return null;
}

return usuarioRepository.findById(new Email(auth.getName())).orElse(null);
}
}