package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ActividadController {

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping("/actividades")
    public String listarActividades(Model model) {
        model.addAttribute("actividades", actividadService.getActividades());
        return "actividades";
    }

    @GetMapping("/actividades/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Actividad actividad = actividadService.getActividadById(id);
        model.addAttribute("actividad", actividad);
        return "actividad-detalle";
    }

    @PostMapping("/actividades/{id}/reservar")
    public String reservarActividad(@PathVariable Long id, Model model) {
        boolean reservada = actividadService.reservarActividad(id);
        model.addAttribute("actividad", actividadService.getActividadById(id));
        model.addAttribute("mensaje", reservada ? "Reserva realizada correctamente" : "No quedan plazas disponibles");
        return "actividad-detalle";
    }
}