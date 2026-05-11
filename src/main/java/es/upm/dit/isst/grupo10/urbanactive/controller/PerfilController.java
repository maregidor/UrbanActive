package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Nivel;
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
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // PERFIL USUARIO LOGUEADO

    @GetMapping("/mi-perfil")
    public String verMiPerfil(Model model) {
        Usuario usuario = getUsuarioAutenticado();

        if (usuario == null) {
            return "redirect:/login";
        }

        List<Usuario> seguidores = seguimientoUsuarioRepository.findBySeguido(usuario)
                .stream()
                .map(SeguimientoUsuario::getSeguidor)
                .collect(Collectors.toList());

        List<Usuario> seguidos = seguimientoUsuarioRepository.findBySeguidor(usuario)
                .stream()
                .map(SeguimientoUsuario::getSeguido)
                .collect(Collectors.toList());

        model.addAttribute("usuario", usuario);
        model.addAttribute("seguidores", seguidores);
        model.addAttribute("seguidos", seguidos);
        model.addAttribute("numSeguidores", seguidores.size());
        model.addAttribute("numSeguidos", seguidos.size());

        return "mi-perfil";
    }

    @GetMapping("/mi-perfil/editar")
    public String mostrarEditarMiPerfil(Model model) {
        Usuario usuario = getUsuarioAutenticado();

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "editar-mi-perfil";
    }

    @PostMapping("/mi-perfil/editar")
    public String guardarEditarMiPerfil(@RequestParam String nombre,
                                        @RequestParam double nivelExperiencia) {
        Usuario usuario = getUsuarioAutenticado();

        if (usuario == null) {
            return "redirect:/login";
        }

        usuario.setNombre(nombre);

        Nivel nivel = new Nivel();
        nivel.setValor(nivelExperiencia);
        usuario.setNivelExperiencia(nivel);

        usuarioRepository.save(usuario);

        return "redirect:/mi-perfil";
    }


    // PERFIL DE OTRO USUARIO

    @GetMapping("/usuarios/{slug}")
    public String verPerfilUsuario(@PathVariable String slug,
                                   Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findBySlug(slug);

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
        List<Actividad> actividades = actividadRepository.findByUsuarioOrganizador(perfil);
        long numActividades = actividades.size();

        model.addAttribute("perfil", perfil);
        model.addAttribute("esMiPerfil", esMiPerfil);
        model.addAttribute("yaLeSigue", yaLeSigue);
        model.addAttribute("numSeguidores", numSeguidores);
        model.addAttribute("numSeguidos", numSeguidos);
        model.addAttribute("numActividades", numActividades);
        model.addAttribute("actividades", actividades);
        return "perfil-usuario";
    }

    @PostMapping("/usuarios/{slug}/seguir")
    public String seguirUsuario(@PathVariable String slug) {
        Optional<Usuario> usuarioPerfilOpt = usuarioRepository.findBySlug(slug);
        Usuario usuarioActual = getUsuarioAutenticado();

        if (usuarioPerfilOpt.isPresent() && usuarioActual != null) {
            Usuario usuarioPerfil = usuarioPerfilOpt.get();
            boolean esMiPerfil = usuarioActual.getEmail().equals(usuarioPerfil.getEmail());
            boolean yaExiste = seguimientoUsuarioRepository
                    .existsBySeguidorAndSeguido(usuarioActual, usuarioPerfil);

            if (!esMiPerfil && !yaExiste) {
                seguimientoUsuarioRepository
                        .save(new SeguimientoUsuario(usuarioActual, usuarioPerfil));
            }
        }

        return "redirect:/usuarios/" + slug;
    }

    @PostMapping("/usuarios/{slug}/dejar-seguir")
    public String dejarSeguirUsuario(@PathVariable String slug) {
        Optional<Usuario> usuarioPerfilOpt = usuarioRepository.findBySlug(slug);
        Usuario usuarioActual = getUsuarioAutenticado();

        if (usuarioPerfilOpt.isPresent() && usuarioActual != null) {
            seguimientoUsuarioRepository
                    .findBySeguidorAndSeguido(usuarioActual, usuarioPerfilOpt.get())
                    .ifPresent(seguimientoUsuarioRepository::delete);
        }

        return "redirect:/usuarios/" + slug;
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

    
    // PERFIL ORGANIZACION

    @GetMapping("/organizaciones/{slug}")
    public String verPerfilOrganizacion(@PathVariable String slug,
                                        Model model) {
        Optional<Organizacion> organizacionOpt = organizacionRepository.findBySlug(slug);

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
        List<Actividad> actividades = actividadRepository.findByOrganizacion(perfil);

        // Mantiene compatibilidad con la plantilla actual y con futuros usos del nombre "perfil".
        model.addAttribute("perfil", perfil);
        model.addAttribute("organizacion", perfil);
        model.addAttribute("yaLaSigue", yaLaSigue);
        model.addAttribute("numSeguidores", numSeguidores);
        model.addAttribute("actividades", actividades);

        return "perfil-organizacion";
    }

    @PostMapping("/organizaciones/{slug}/seguir")
    public String seguirOrganizacion(@PathVariable String slug) {
        Optional<Organizacion> organizacionOpt = organizacionRepository.findBySlug(slug);
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

        return "redirect:/organizaciones/" + slug;
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

    @GetMapping("/organizacion/editar")
    public String mostrarFormularioEditarOrga(Model model, Principal principal) {
        String emailLogueado = principal.getName();

        return organizacionRepository.findByEmailDireccion(emailLogueado)
            .map(orga -> {
                model.addAttribute("organizacion", orga);
                return "editar-mi-perfil-organizacion"; // Nombre del archivo HTML
            })
            .orElse("redirect:/mi-perfil-organizacion?error=no_encontrada");
    }

    @PostMapping("/organizacion/editar")
    public String procesarEditarOrga(@RequestParam String nombre, 
                                    @RequestParam String actividad, 
                                    Principal principal) {
        String emailLogueado = principal.getName();

        return organizacionRepository.findByEmailDireccion(emailLogueado)
            .map(orga -> {
                // Actualizamos solo los campos que permitimos en el formulario
                orga.setNombre(nombre);
                orga.setActividad(actividad);
                
                organizacionRepository.save(orga);
                
                System.out.println("Perfil actualizado con éxito: " + emailLogueado);
                return "redirect:/mi-perfil-organizacion?success=perfil_actualizado";
            })
            .orElse("redirect:/mi-perfil-organizacion?error=error_al_guardar");
    }

    // MÉTODO AUXILIAR
    
    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
            return null;
        }

        return usuarioRepository.findById(new Email(auth.getName())).orElse(null);
    }
}
