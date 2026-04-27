package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoOrganizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.SeguimientoUsuario;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.ActividadRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.OrganizacionRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.SeguimientoOrganizacionRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.SeguimientoUsuarioRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PerfilController {

private final ActividadRepository actividadRepository;
private final OrganizacionRepository organizacionRepository;
private final UsuarioRepository usuarioRepository;
private final SeguimientoOrganizacionRepository seguimientoOrganizacionRepository;
private final SeguimientoUsuarioRepository seguimientoUsuarioRepository;

public PerfilController(ActividadRepository actividadRepository,
OrganizacionRepository organizacionRepository,
UsuarioRepository usuarioRepository,
SeguimientoOrganizacionRepository seguimientoOrganizacionRepository,
SeguimientoUsuarioRepository seguimientoUsuarioRepository) {
this.actividadRepository = actividadRepository;
this.organizacionRepository = organizacionRepository;
this.usuarioRepository = usuarioRepository;
this.seguimientoOrganizacionRepository = seguimientoOrganizacionRepository;
this.seguimientoUsuarioRepository = seguimientoUsuarioRepository;
}

@GetMapping("/usuarios/{email}")
public String verPerfilUsuario(@PathVariable String email, Model model) {
Optional<Usuario> usuarioOpt = usuarioRepository.findById(new Email(email));

if (usuarioOpt.isEmpty()) {
return "redirect:/actividades";
}

Usuario perfil = usuarioOpt.get();
Usuario usuarioActual = getUsuarioAutenticado();

boolean esMiPerfil = usuarioActual != null
&& usuarioActual.getEmail().equals(perfil.getEmail());

boolean yaLeSigue = usuarioActual != null
&& !esMiPerfil
&& seguimientoUsuarioRepository.existsBySeguidorAndSeguido(usuarioActual, perfil);

long numSeguidores = seguimientoUsuarioRepository.countBySeguido(perfil);
long numSeguidos = seguimientoUsuarioRepository.countBySeguidor(perfil);
long numActividades = actividadRepository.countByUsuarioOrganizador(perfil);

model.addAttribute("perfil", perfil);
model.addAttribute("esMiPerfil", esMiPerfil);
model.addAttribute("yaLeSigue", yaLeSigue);
model.addAttribute("numSeguidores", numSeguidores);
model.addAttribute("numSeguidos", numSeguidos);
model.addAttribute("numActividades", numActividades);
model.addAttribute("actividades", actividadRepository.findByUsuarioOrganizador(perfil));

return "perfil-usuario";
}

@GetMapping("/organizaciones/{slug}")
public String verPerfilOrganizacion(@PathVariable String slug, Model model) {
Optional<Organizacion> organizacionOpt = organizacionRepository.findBySlug(slug);

if (organizacionOpt.isEmpty()) {
return "redirect:/actividades";
}

Organizacion organizacion = organizacionOpt.get();
Usuario usuarioActual = getUsuarioAutenticado();

boolean yaLaSigue = usuarioActual != null
&& seguimientoOrganizacionRepository.existsBySeguidorAndOrganizacion(usuarioActual, organizacion);

long numSeguidores = seguimientoOrganizacionRepository.countByOrganizacion(organizacion);

model.addAttribute("organizacion", organizacion);
model.addAttribute("yaLaSigue", yaLaSigue);
model.addAttribute("numSeguidores", numSeguidores);
model.addAttribute("actividades", actividadRepository.findByOrganizacion(organizacion));

return "perfil-organizacion";
}

@PostMapping("/organizaciones/{slug}/seguir")
public String seguirOrganizacion(@PathVariable String slug, Model model) {
Optional<Organizacion> organizacionOpt = organizacionRepository.findBySlug(slug);
Usuario usuarioActual = getUsuarioAutenticado();

if (organizacionOpt.isEmpty() || usuarioActual == null) {
return "redirect:/actividades";
}

Organizacion organizacion = organizacionOpt.get();

boolean yaExiste = seguimientoOrganizacionRepository
.existsBySeguidorAndOrganizacion(usuarioActual, organizacion);

if (!yaExiste) {
seguimientoOrganizacionRepository
.save(new SeguimientoOrganizacion(usuarioActual, organizacion));
}

model.addAttribute("nombreSeguido", organizacion.getNombre());
model.addAttribute("mensajeExito", "Ahora sigues a esta organización.");
model.addAttribute("slugOrganizacion", organizacion.getSlug());

return "seguido-confirmacion";
}

@PostMapping("/organizaciones/{slug}/dejar-seguir")
public String dejarSeguirOrganizacion(@PathVariable String slug) {
Optional<Organizacion> organizacionOpt = organizacionRepository.findBySlug(slug);
Usuario usuarioActual = getUsuarioAutenticado();

if (organizacionOpt.isPresent() && usuarioActual != null) {
seguimientoOrganizacionRepository
.findBySeguidorAndOrganizacion(usuarioActual, organizacionOpt.get())
.ifPresent(seguimientoOrganizacionRepository::delete);
}

return "redirect:/organizaciones/" + slug;
}

@PostMapping("/usuarios/{email}/seguir")
public String seguirUsuario(@PathVariable String email, Model model) {
Optional<Usuario> usuarioPerfilOpt = usuarioRepository.findById(new Email(email));
Usuario usuarioActual = getUsuarioAutenticado();

if (usuarioPerfilOpt.isEmpty() || usuarioActual == null) {
return "redirect:/actividades";
}

Usuario usuarioPerfil = usuarioPerfilOpt.get();
boolean esMiPerfil = usuarioActual.getEmail().equals(usuarioPerfil.getEmail());
boolean yaExiste = seguimientoUsuarioRepository
.existsBySeguidorAndSeguido(usuarioActual, usuarioPerfil);

if (!esMiPerfil && !yaExiste) {
seguimientoUsuarioRepository
.save(new SeguimientoUsuario(usuarioActual, usuarioPerfil));
}

model.addAttribute("nombreSeguido", usuarioPerfil.getNombre());
model.addAttribute("mensajeExito", "Ahora sigues a este usuario.");
model.addAttribute("emailUsuario", usuarioPerfil.getEmail().getDireccion());

return "seguido-confirmacion";
}

@PostMapping("/usuarios/{email}/dejar-seguir")
public String dejarSeguirUsuario(@PathVariable String email) {
Optional<Usuario> usuarioPerfilOpt = usuarioRepository.findById(new Email(email));
Usuario usuarioActual = getUsuarioAutenticado();

if (usuarioPerfilOpt.isPresent() && usuarioActual != null) {
seguimientoUsuarioRepository
.findBySeguidorAndSeguido(usuarioActual, usuarioPerfilOpt.get())
.ifPresent(seguimientoUsuarioRepository::delete);
}

return "redirect:/usuarios/" + email;
}

private Usuario getUsuarioAutenticado() {
Authentication auth = SecurityContextHolder.getContext().getAuthentication();

if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
return null;
}

return usuarioRepository.findById(new Email(auth.getName())).orElse(null);
}
}
