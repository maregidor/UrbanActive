package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.model.Reserva;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.service.ReservaService;
import es.upm.dit.isst.grupo10.urbanactive.repository.ActividadRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaController(ReservaService reservaService, ActividadRepository actividadRepository, UsuarioRepository usuarioRepository) {
        this.reservaService = reservaService;
        this.actividadRepository = actividadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioAutenticado(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null || auth.getName().equals("anoymousUser")){
            return null;
        }

        return usuarioRepository.findById(new Email(auth.getName())).orElse(null);
    }

    @GetMapping("/")
    public String reservasHome(Model model) {
        return "redirect:/actividades";
    }

    @GetMapping("/actividad/{actividadId}")
    public String mostrarSesionesActividad(@PathVariable Long actividadId, Model model) {
        Optional<Actividad> actividadOpt = actividadRepository.findById(actividadId);
        if (actividadOpt.isEmpty()) {
            return "redirect:/actividades";
        }

        model.addAttribute("actividad", actividadOpt.get());
        return "reserva-sesiones";
    }

    @GetMapping("/formulario/{actividadId}")
    public String mostrarFormularioReserva(@PathVariable Long actividadId, 
                                         Model model,
                                         HttpSession session) {
        Optional<Actividad> actividadOpt = actividadRepository.findById(actividadId);
        if (actividadOpt.isEmpty()) {
            return "redirect:/actividades";
        }

        // Obtener usuario logueado de la sesión
        Usuario usuario = (Usuario) getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("actividad", actividadOpt.get());
        model.addAttribute("usuario", usuario);
        return "reserva-formulario-simple";
    }

    @PostMapping("/procesar")
    public String procesarReserva(@RequestParam Long actividadId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        
        // Obtener usuario logueado de la sesión
        Usuario usuario = (Usuario) getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        if (reservaService.yaTieneReservaActiva(usuario, actividadId)) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ya tienes esta actividad reservada.");
            return "redirect:/actividades/" + actividadId;
        }

        boolean exito = reservaService.reservarPlaza(usuario, actividadId);

        if (exito) {
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Reserva realizada correctamente! Tu plaza ha sido confirmada.");
            return "redirect:/reservas/exito";
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo realizar la reserva. Verifica que haya plazas disponibles.");
            return "redirect:/reservas/formulario/" + actividadId;
        }

    }

    @GetMapping("/exito")
    public String mostrarReservaExito(Model model) {
        return "reserva-exito";
    }

    @GetMapping("/confirmacion/{reservaId}")
    public String mostrarConfirmacion(@PathVariable Long reservaId, Model model) {
        Reserva reserva = reservaService.getReservaById(reservaId);
        if (reserva == null) {
            return "redirect:/actividades";
        }

        Optional<Actividad> actividadOpt = actividadRepository.findById(reserva.getActividadId());
        if (actividadOpt.isEmpty()) {
            return "redirect:/actividades";
        }

        model.addAttribute("reserva", reserva);
        model.addAttribute("actividad", actividadOpt.get());
        return "reserva-confirmacion";
    }

    @GetMapping("/mis-reservas")
    public String mostrarMisReservas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }

        List<Reserva> reservas = reservaService.getReservasPorUsuario(usuario);
        model.addAttribute("reservas", reservas);
        model.addAttribute("usuario", usuario);
        return "mis-reservas";
    }

    @PostMapping("/cancelar/{reservaId}")
    public String cancelarReserva(@PathVariable Long reservaId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        
        Usuario usuario = (Usuario) getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }
        
        boolean cancelada = reservaService.cancelarReserva(reservaId, usuario);
        
        if (cancelada) {
            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva cancelada correctamente");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo cancelar la reserva. Verifica que la reserva exista y te pertenezca.");
        }
        
        return "redirect:/reservas/mis-reservas";
    }

    @GetMapping("/detalle/{reservaId}")
    public String verDetalleReserva(@PathVariable Long reservaId, 
                                   HttpSession session,
                                   Model model) {
        Usuario usuario = (Usuario) getUsuarioAutenticado();
        if (usuario == null) {
            return "redirect:/login";
        }

        Reserva reserva = reservaService.getReservaById(reservaId);
        if (reserva == null || !reserva.getUsuario().equals(usuario)) {
            return "redirect:/reservas/mis-reservas";
        }

        Optional<Actividad> actividadOpt = actividadRepository.findById(reserva.getActividadId());
        if (actividadOpt.isEmpty()) {
            return "redirect:/reservas/mis-reservas";
        }

        model.addAttribute("reserva", reserva);
        model.addAttribute("actividad", actividadOpt.get());
        return "reserva-detalle";
    }
}
