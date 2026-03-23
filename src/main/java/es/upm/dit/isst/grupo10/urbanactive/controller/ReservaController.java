package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.model.Reserva;
import es.upm.dit.isst.grupo10.urbanactive.model.Sesion;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadService;
import es.upm.dit.isst.grupo10.urbanactive.service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final ActividadService actividadService;

    public ReservaController(ReservaService reservaService, ActividadService actividadService) {
        this.reservaService = reservaService;
        this.actividadService = actividadService;
    }

    @GetMapping("/")
    public String reservasHome(Model model) {
        return "redirect:/actividades";
    }

    @GetMapping("/actividad/{actividadId}")
    public String mostrarSesionesActividad(@PathVariable Long actividadId, Model model) {
        Actividad actividad = actividadService.getActividadById(actividadId);
        if (actividad == null) {
            return "redirect:/actividades";
        }

        List<Sesion> sesiones = reservaService.getSesionesPorActividad(actividadId);
        model.addAttribute("actividad", actividad);
        model.addAttribute("sesiones", sesiones);
        return "reserva-sesiones";
    }

    @GetMapping("/formulario/{actividadId}/{sesionId}")
    public String mostrarFormularioReserva(@PathVariable Long actividadId, 
                                         @PathVariable Long sesionId, 
                                         Model model) {
        Actividad actividad = actividadService.getActividadById(actividadId);
        Sesion sesion = reservaService.getSesionById(sesionId).orElse(null);

        if (actividad == null || sesion == null) {
            return "redirect:/actividades";
        }

        model.addAttribute("actividad", actividad);
        model.addAttribute("sesion", sesion);
        return "reserva-formulario";
    }

    @PostMapping("/procesar")
    public String procesarReserva(@RequestParam String emailUsuario,
                                @RequestParam Long actividadId,
                                @RequestParam Long sesionId,
                                RedirectAttributes redirectAttributes) {
        
        ReservaService.ReservaResultado resultado = reservaService.reservarPlaza(emailUsuario, actividadId, sesionId);

        if (resultado.isExito()) {
            redirectAttributes.addFlashAttribute("mensajeExito", resultado.getMensaje());
            redirectAttributes.addFlashAttribute("reserva", resultado.getReserva());
            return "redirect:/reservas/confirmacion/" + resultado.getReserva().getId();
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", resultado.getMensaje());
            redirectAttributes.addFlashAttribute("emailUsuario", emailUsuario);
            return "redirect:/reservas/formulario/" + actividadId + "/" + sesionId;
        }
    }

    @GetMapping("/confirmacion/{reservaId}")
    public String mostrarConfirmacion(@PathVariable Long reservaId, Model model) {
        Reserva reserva = reservaService.getReservaById(reservaId);
        if (reserva == null) {
            return "redirect:/actividades";
        }

        Actividad actividad = actividadService.getActividadById(reserva.getActividadId());
        Sesion sesion = reservaService.getSesionById(reserva.getSesionId()).orElse(null);

        model.addAttribute("reserva", reserva);
        model.addAttribute("actividad", actividad);
        model.addAttribute("sesion", sesion);
        return "reserva-confirmacion";
    }

    @GetMapping("/mis-reservas")
    public String mostrarMisReservas(@RequestParam(required = false) String email, Model model) {
        if (email != null && !email.trim().isEmpty()) {
            List<Reserva> reservas = reservaService.getReservasPorUsuario(email);
            model.addAttribute("reservas", reservas);
            model.addAttribute("emailUsuario", email);
        }
        return "mis-reservas";
    }

    @PostMapping("/cancelar/{reservaId}")
    public String cancelarReserva(@PathVariable Long reservaId,
                                 @RequestParam String emailUsuario,
                                 RedirectAttributes redirectAttributes) {
        
        boolean cancelada = reservaService.cancelarReserva(reservaId, emailUsuario);
        
        if (cancelada) {
            redirectAttributes.addFlashAttribute("mensajeExito", "Reserva cancelada correctamente");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo cancelar la reserva. Verifica que la reserva exista y te pertenezca.");
        }
        
        return "redirect:/reservas/mis-reservas?email=" + emailUsuario;
    }

    @GetMapping("/detalle/{reservaId}")
    public String verDetalleReserva(@PathVariable Long reservaId, Model model) {
        Reserva reserva = reservaService.getReservaById(reservaId);
        if (reserva == null) {
            return "redirect:/actividades";
        }

        Actividad actividad = actividadService.getActividadById(reserva.getActividadId());
        Sesion sesion = reservaService.getSesionById(reserva.getSesionId()).orElse(null);

        model.addAttribute("reserva", reserva);
        model.addAttribute("actividad", actividad);
        model.addAttribute("sesion", sesion);
        return "reserva-detalle";
    }
}
