package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActividadService {

    private final List<Actividad> actividades = new ArrayList<>();

    public ActividadService() {
        actividades.add(new Actividad(1L, "Running", "Salida de running por El Retiro", "Intermedio", 10));
        actividades.add(new Actividad(2L, "Yoga", "Sesión de yoga al aire libre", "Básico", 8));
        actividades.add(new Actividad(3L, "Ciclismo", "Ruta urbana en bicicleta", "Avanzado", 5));
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