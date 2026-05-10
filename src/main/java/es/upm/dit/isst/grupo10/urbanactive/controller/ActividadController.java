package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.*;
import es.upm.dit.isst.grupo10.urbanactive.repository.*;
import es.upm.dit.isst.grupo10.urbanactive.service.*;
import jakarta.servlet.http.HttpServletRequest;
import es.upm.dit.isst.grupo10.urbanactive.dto.ActividadContexto;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile; 
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
public class ActividadController {

    private final ActividadRepository actividadRepository;
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
                               OrganizacionRepository organizacionRepository,
                               ActividadRepository actividadRepository) {
        this.actividadService = actividadService;
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
        this.actividadContextService = actividadContextService;
        this.espacioPublicoRepository = espacioPublicoRepository;
        this.organizacionRepository = organizacionRepository;
        this.actividadRepository = actividadRepository;
    }

    @GetMapping("/actividades")
    public String listarActividades(
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) Double userLat,
            @RequestParam(required = false) Double userLon,
            Model model) {

        List<Actividad> actividades = actividadService.getActividadesOrdenadas(orden, userLat, userLon);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean autenticado = auth != null
                && auth.isAuthenticated()
                && auth.getName() != null
                && !auth.getName().equals("anonymousUser");

        boolean esOrganizador = autenticado
                && organizacionRepository.findByEmailDireccion(auth.getName()).isPresent();

        model.addAttribute("actividades", actividades);
        model.addAttribute("ordenActual", orden);
        model.addAttribute("autenticado", autenticado);
        model.addAttribute("esOrganizador", esOrganizador);

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
        boolean yaReservada = (usuario != null) && reservaService.yaTieneReservaActiva(usuario, id);

        GeoPoint userPoint = (userLat != null && userLon != null) ? 
                             new GeoPoint(userLat, userLon, "Mi ubicación") : null;

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

        List<EspacioPublico> listaEspacios = espacioPublicoRepository.findAll();
        boolean esOrganizador = organizacionRepository.findByEmailDireccion(username).isPresent();
       
        model.addAttribute("espacios", listaEspacios); 
        model.addAttribute("actividad", new Actividad());
        model.addAttribute("esOrganizador", esOrganizador);
        
        return "crear-actividad";
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
            redirectAttributes.addFlashAttribute("mensajeError", "¡El archivo es demasiado grande! Intenta con uno de menos de 10MB.");
            
            return "redirect:/actividades/nueva";
        }
    }

    @PostMapping("/actividades/guardar")
    public String guardarActividad(@ModelAttribute("actividad") Actividad actividad,
                                @RequestParam("imagenArchivo") MultipartFile imagenArchivo,
                                @RequestParam Long espacioPublicoId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String idPrincipal = auth.getName();

        if (!imagenArchivo.isEmpty()) {
            try {
                String carpetaUploads = "uploads";
                File directorio = new File(carpetaUploads);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }

                String nombreArchivo = System.currentTimeMillis() + "_" + imagenArchivo.getOriginalFilename();
                Path rutaCompleta = Paths.get(carpetaUploads).resolve(nombreArchivo);

                Files.copy(imagenArchivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

                actividad.setImagen("/uploads/" + nombreArchivo);

            } catch (IOException e) {
                System.err.println("Error al subir el archivo: " + e.getMessage());
            }
        }

        EspacioPublico espacio = espacioPublicoRepository.findById(espacioPublicoId)
                .orElseThrow(() -> new IllegalArgumentException("El espacio público seleccionado no existe"));

        actividad.setEspacioPublico(espacio);
        actividad.setLatitud(espacio.getLatitud());
        actividad.setLongitud(espacio.getLongitud());

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

    @GetMapping("/mi-perfil-organizacion")
    public String miPerfilOrganizacion(Model model, Principal principal) {
        // Principal es una forma más directa de obtener el username (email)
        String emailLogueado = principal.getName(); 

        // OJO: Verifica en OrganizacionRepository que este método existe
        Optional<Organizacion> orgaOpt = organizacionRepository.findByEmailDireccion(emailLogueado);

        if (orgaOpt.isPresent()) {
            model.addAttribute("organizacion", orgaOpt.get());
            return "mi-perfil-organizacion";
        }
        
        // Si llegas aquí, es que Spring Security te reconoce, pero el Repo no te encuentra
        System.out.println("ERROR: Organización no encontrada en DB para el email: " + emailLogueado);
        return "redirect:/actividades?error=not_found";
    }

    @GetMapping("/mis-actividades-organizacion")
    public String misActividadesOrga(Model model, Principal principal) {
        String email = principal.getName();
        System.out.println("--- INICIO GESTIÓN ACTIVIDADES ORGA ---");
        System.out.println("Email logueado: " + email);

        Optional<Organizacion> orgaOpt = organizacionRepository.findByEmailDireccion(email);

        if (orgaOpt.isPresent()) {
            Organizacion orga = orgaOpt.get();
            String tipo = orga.getIdentificacion().getTipo();
            String numero = orga.getIdentificacion().getNumero();
            
            System.out.println("Organización encontrada: " + orga.getNombre());
            System.out.println("Buscando actividades para ID: " + tipo + " " + numero);

            try {
                List<Actividad> lista = actividadService.getActividadesPorOrganizacion(tipo, numero);
                System.out.println("Actividades encontradas: " + lista.size());
                
                model.addAttribute("actividades", lista);
                return "mis-actividades-organizacion";
            } catch (Exception e) {
                System.out.println("ERROR al recuperar actividades: " + e.getMessage());
                e.printStackTrace();
                return "redirect:/actividades?error=error_en_consulta";
            }
        } else {
            System.out.println("ERROR: No se encontró la orga con email " + email + " en este método.");
            return "redirect:/actividades?error=orga_no_encontrada";
        }
    }

    @GetMapping("/mis-actividades-usuario")
    public String misActividadesUsuario(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); 

        List<Actividad> misActividades = actividadService.getActividadesPorCreador(email);
        
        model.addAttribute("actividades", misActividades);
        return "mis-actividades-usuario";
    }

    @GetMapping("/actividades/{id}/asistentes")
    public String verAsistentes(@PathVariable Long id, Model model, Principal principal) {
        String emailLogueado = principal.getName();
        
        Optional<Actividad> actividadOpt = actividadRepository.findById(id);
        
        if (actividadOpt.isPresent()) {
            Actividad actividad = actividadOpt.get();
            
            boolean esOrgaDueña = actividad.getOrganizacion() != null && 
                                actividad.getOrganizacion().getEmail().getDireccion().equals(emailLogueado);
            
            boolean esUsuarioDueño = actividad.getUsuarioOrganizador() != null && 
                                    actividad.getUsuarioOrganizador().getEmail().getDireccion().equals(emailLogueado);

            if (esOrgaDueña || esUsuarioDueño) {
                model.addAttribute("actividad", actividad);
                model.addAttribute("asistentes", reservaService.getAsistentesActivos(id));
                return "asistentes-actividad";
            }
        }
        
        return "redirect:/actividades?error=acceso_denegado";
    }

    @GetMapping("/actividades/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, Principal principal) {
        Optional<Actividad> actividadOpt = actividadRepository.findById(id);

        if (actividadOpt.isPresent()) {
            Actividad actividad = actividadOpt.get();
            String emailLogueado = principal.getName();

            boolean esDueño = (actividad.getUsuarioOrganizador() != null && actividad.getUsuarioOrganizador().getEmail().getDireccion().equals(emailLogueado)) ||
                            (actividad.getOrganizacion() != null && actividad.getOrganizacion().getEmail().getDireccion().equals(emailLogueado));

            if (esDueño) {
                model.addAttribute("actividad", actividad);
                model.addAttribute("modo", "editar"); 
                return "crear-actividad"; 
            }
        }
        return "redirect:/actividades?error=no_autorizado";
    }

    @PostMapping("/actividades/editar/{id}")
    public String procesarEdicion(@PathVariable Long id, @ModelAttribute Actividad actividadEditada, Principal principal) {
        Optional<Actividad> actividadOpt = actividadRepository.findById(id);

        if (actividadOpt.isPresent()) {
            Actividad actividadExistente = actividadOpt.get();
            String emailLogueado = principal.getName();

            boolean esDueño = (actividadExistente.getUsuarioOrganizador() != null && actividadExistente.getUsuarioOrganizador().getEmail().getDireccion().equals(emailLogueado)) ||
                            (actividadExistente.getOrganizacion() != null && actividadExistente.getOrganizacion().getEmail().getDireccion().equals(emailLogueado));

            if (esDueño) {
                actividadExistente.setTitulo(actividadEditada.getTitulo());
                actividadExistente.setDescripcion(actividadEditada.getDescripcion());
                actividadExistente.setFecha(actividadEditada.getFecha());
                actividadExistente.setHora(actividadEditada.getHora());
                actividadExistente.setPlazasTotales(actividadEditada.getPlazasTotales());
                actividadExistente.setPrecio(actividadEditada.getPrecio());
                actividadExistente.setTipo(actividadEditada.getTipo());
                
                if (actividadEditada.getImagen() != null && !actividadEditada.getImagen().isEmpty()) {
                    actividadExistente.setImagen(actividadEditada.getImagen());
                }

                actividadRepository.save(actividadExistente);
                
                if (actividadExistente.getOrganizacion() != null) return "redirect:/mis-actividades-organizacion";
                return "redirect:/mis-actividades-usuario";
            }
        }
        return "redirect:/actividades";
    }

    @PostMapping("/actividades/eliminar/{id}")
    public String eliminarActividad(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        String emailLogueado = principal.getName();
        Optional<Actividad> actividadOpt = actividadRepository.findById(id);

        if (actividadOpt.isPresent()) {
            Actividad actividad = actividadOpt.get();

            // Seguridad: Verificar que el logueado es el dueño (Usuario u Orga)
            boolean esDueño = (actividad.getUsuarioOrganizador() != null && actividad.getUsuarioOrganizador().getEmail().getDireccion().equals(emailLogueado)) ||
                            (actividad.getOrganizacion() != null && actividad.getOrganizacion().getEmail().getDireccion().equals(emailLogueado));

            if (esDueño) {
                actividadRepository.delete(actividad);
                System.out.println("Actividad " + id + " eliminada por " + emailLogueado);
                
                // Redirección inteligente: volvemos a la página desde la que venía el usuario
                String referer = request.getHeader("Referer");
                return "redirect:" + (referer != null ? referer : "/actividades");
            }
        }

        return "redirect:/actividades?error=no_autorizado";
    }
}