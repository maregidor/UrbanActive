package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActividadService {

    private final List<Actividad> actividades = new ArrayList<>();
Long id, String tipo, String descripcion, Nivel nivel,
                     String organizador, String fecha, String hora,
                     String ubicacion, double precio, int plazas,
                     String duracion, String imagen
    public ActividadService() {
        actividades.add(new Actividad(1L, "Running", "Salida de running por El Retiro", "Intermedio", "Juan Perez", "26/04/2026", "14:00", "Parque el Retiro", "12€",  10, "30 minutos", "/img/running.jpg"));
        actividades.add(new Actividad(2L, "Yoga", "Sesión de yoga al aire libre", "Básico", "Lucía Lopez", "30/04/2026", "18:00", "Parque del Oeste", "9€",  5, "1 hora", "/img/yoga.jpg"));
        actividades.add(new Actividad(3L, "Ciclismo", "Ruta urbana en bicicleta", "Avanzado", "Juan Moreno", "6/05/2026", "19:00", "El Pardo", "gratis",  14, "1 hora y 30 minutos", "/img/ciclismo.jpg"))));
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public Actividad getActividadById(Long id) {
        return actividades.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean reservarActividad(Long id) {
        Actividad actividad = getActividadById(id);
        if (actividad != null && actividad.getPlazasDisponibles() > 0) {
            actividad.setPlazasDisponibles(actividad.getPlazasDisponibles() - 1);
            return true;
        }
        return false;
    }
}