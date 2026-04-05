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

@Controller
public class ActividadController {

private final ActividadService actividadService;
private final ReservaService reservaService;
private final UsuarioRepository usuarioRepository;

public ActividadController(ActividadService actividadService,
ReservaService reservaService,
UsuarioRepository usuarioRepository) {
this.actividadService = actividadService;
this.reservaService = reservaService;
this.usuarioRepository = usuarioRepository;
}

@GetMapping("/actividades")
public String listarActividades(Model model) {
model.addAttribute("actividades", actividadService.getActividades());
return "actividades";
}

@GetMapping("/actividades/{id}")
public String verDetalle(@PathVariable Long id, Model model) {
Actividad actividad = actividadService.getActividadById(id);
if (actividad == null) {
return "redirect:/actividades";
}

Usuario usuario = getUsuarioAutenticado();

boolean yaReservada = false;
if (usuario != null) {
yaReservada = reservaService.yaTieneReservaActiva(usuario, id);
}

model.addAttribute("actividad", actividad);
model.addAttribute("yaReservada", yaReservada);

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