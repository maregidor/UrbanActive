package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.repository.ActividadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;

    public ActividadService(ActividadRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public List<Actividad> getActividades() {
        return StreamSupport.stream(actividadRepository.findAll().spliterator(), false)
                            .collect(Collectors.toList());
    }

    public Actividad getActividadById(Long id) {
        return actividadRepository.findById(id).orElse(null);
    }

    public boolean reservarActividad(Long id) {
        Actividad actividad = getActividadById(id);
        
        if (actividad != null && actividad.getPlazasDisponibles() > 0) {
            actividad.setPlazasDisponibles(actividad.getPlazasDisponibles() - 1);
            
            actividadRepository.save(actividad); 
            return true;
        }
        return false;
    }

    // 5. Método extra para que tú puedas guardar nuevas actividades desde tu US
    public Actividad guardarActividad(Actividad actividad) {
        return actividadRepository.save(actividad);
    }
}