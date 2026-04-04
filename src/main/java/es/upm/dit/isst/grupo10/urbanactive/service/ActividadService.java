package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.repository.ActividadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final GeocodingService geocodingService;

    public ActividadService(ActividadRepository actividadRepository, GeocodingService geocodingService) {
        this.actividadRepository = actividadRepository;
        this.geocodingService = geocodingService;
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
        if (actividad.getLatitud() == null || actividad.getLongitud() == null) {
            try {
                String direccion = "";

                if (actividad.getEspacioPublico() != null) {
                    direccion = actividad.getEspacioPublico().getNombre() + ", " +
                            actividad.getEspacioPublico().getUbicacion() + ", Madrid, España";
                }

                if (!direccion.isEmpty()) {
                    GeoPoint punto = geocodingService.buscar(direccion);

                    if (punto != null) {
                        actividad.setLatitud(punto.lat());
                        actividad.setLongitud(punto.lon());
                    }
                }

            } catch (Exception e) {
            System.out.println("Error geocoding: " + e.getMessage());
            }
        }

        return actividadRepository.save(actividad);
    }
    //Método para ordenar la lista de actividades
public List<Actividad> ordenarActividades(List<Actividad> actividades, String criterio, Double userLat, Double userLon) {

    if (criterio == null) return actividades;

    switch (criterio) {

        case "fecha":
            return actividades.stream()
                    .sorted((a1, a2) -> a1.getFecha().compareTo(a2.getFecha()))
                    .toList();

        case "precio":
            return actividades.stream()
                    .sorted((a1, a2) -> Double.compare(
                            a1.getPrecio() != null ? a1.getPrecio() : Double.MAX_VALUE,
                            a2.getPrecio() != null ? a2.getPrecio() : Double.MAX_VALUE))
                    .toList();

        case "valoracion":
    return actividades; // temporal

        case "distancia":
            if (userLat == null || userLon == null) return actividades;

            return actividades.stream()
                    .sorted((a1, a2) -> Double.compare(
                            calcularDistancia(userLat, userLon, a1),
                            calcularDistancia(userLat, userLon, a2)))
                    .toList();

        default:
            return actividades;
    }
}

private double calcularDistancia(Double userLat, Double userLon, Actividad a) {
    if (a.getLatitud() == null || a.getLongitud() == null) return Double.MAX_VALUE;

    double dx = userLat - a.getLatitud();
    double dy = userLon - a.getLongitud();

    return Math.sqrt(dx * dx + dy * dy);
}
}

