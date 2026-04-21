package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Identificacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.EspacioPublicoRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.OrganizacionRepository;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadService;
import es.upm.dit.isst.grupo10.urbanactive.service.ReservaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.upm.dit.isst.grupo10.urbanactive.dto.ActividadContexto;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadContextService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ActividadController {

private final ActividadService actividadService;
private final ReservaService reservaService;
private final UsuarioRepository usuarioRepository;
private final ActividadContextService actividadContextService;
private final EspacioPublicoRepository espacioPublicoRepository;
private final OrganizacionRepository organizacionRepository;

public ActividadController(ActividadService actividadService,
                           ReservaService reservaService,
                           UsuarioRepository usuarioRepository,
                           ActividadContextService actividadContextService,
                           EspacioPublicoRepository espacioPublicoRepository,
                           OrganizacionRepository organizacionRepository) {
    this.actividadService = actividadService;
    this.reservaService = reservaService;
    this.usuarioRepository = usuarioRepository;
    this.actividadContextService = actividadContextService;
    this.espacioPublicoRepository = espacioPublicoRepository;
    this.organizacionRepository = organizacionRepository;
}

@GetMapping("/actividades")
public String listarActividades(
        @RequestParam(required = false) String orden,
        @RequestParam(required = false) Double userLat,
        @RequestParam(required = false) Double userLon,
        Model model) {

    System.out.println("ORDEN RECIBIDO EN CONTROLLER: [" + orden + "]");
    
    List<Actividad> actividades = actividadService.getActividadesOrdenadas(orden, userLat, userLon);
        
    model.addAttribute("actividades", actividades);
    model.addAttribute("ordenActual", orden); // Para saber qué botón marcar como activo en el HTML
    model.addAttribute("esOrganizador", true);
        
    return "actividades";
}

@GetMapping("/actividades/crear")
public String mostrarFormularioCrearActividad(Model model) {
    model.addAttribute("actividad", new Actividad());
    return "crear-actividad";
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

@GetMapping("/actividades/nueva")
public String mostrarFormularioCrear(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    Optional<Organizacion> org = organizacionRepository.findByIdentificacionNumero(username);
    boolean esOrganizador = org.isPresent();

    model.addAttribute("actividad", new Actividad());
    model.addAttribute("espacios", espacioPublicoRepository.findAll());
    model.addAttribute("esOrganizador", esOrganizador);
    
    return "crear-actividad";
}

@PostMapping("/actividades/guardar")
    public String guardarActividad(@ModelAttribute("actividad") Actividad actividad) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String idPrincipal = auth.getName(); 
    
        actividadService.crearNuevaActividad(actividad, idPrincipal);
    
        return "redirect:/actividades";
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
            return null;
        }

        return usuarioRepository.findById(new Email(auth.getName())).orElse(null);
    }
}