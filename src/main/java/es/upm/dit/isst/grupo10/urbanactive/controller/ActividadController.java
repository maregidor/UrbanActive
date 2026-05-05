package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.*;
import es.upm.dit.isst.grupo10.urbanactive.repository.*;
import es.upm.dit.isst.grupo10.urbanactive.service.*;
import es.upm.dit.isst.grupo10.urbanactive.dto.ActividadContexto;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Nuevo import

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

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

    @PostMapping("/actividades/guardar")
    public String guardarActividad(@ModelAttribute("actividad") Actividad actividad,
                                   @RequestParam("imagenArchivo") MultipartFile imagenArchivo) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String idPrincipal = auth.getName(); 

        // Lógica para procesar la subida del archivo local
        if (!imagenArchivo.isEmpty()) {
            try {
                // Definimos la carpeta de destino
                String carpetaUploads = "uploads";
                File directorio = new File(carpetaUploads);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }

                // Generamos un nombre único para evitar que se sobrescriban archivos
                String nombreArchivo = System.currentTimeMillis() + "_" + imagenArchivo.getOriginalFilename();
                Path rutaCompleta = Paths.get(carpetaUploads).resolve(nombreArchivo);

                // Copiamos el archivo al sistema de ficheros
                Files.copy(imagenArchivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

                // Guardamos la ruta relativa en el objeto Actividad
                // El navegador accederá vía /uploads/nombre_archivo.jpg
                actividad.setImagen("/uploads/" + nombreArchivo);

            } catch (IOException e) {
                System.err.println("Error al subir el archivo: " + e.getMessage());
                // Si falla la subida, se mantendrá la URL de la imagen si se proporcionó una
            }
        }
        // Si no hay archivo pero sí hay URL en 'actividad.imagen' (por el th:field), 
        // no hace falta hacer nada extra, ya viene en el objeto.

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