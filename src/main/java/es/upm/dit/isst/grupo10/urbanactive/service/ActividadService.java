package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.repository.ActividadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Comparator;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final GeocodingService geocodingService;

    public ActividadService(ActividadRepository actividadRepository, GeocodingService geocodingService) {
        this.actividadRepository = actividadRepository;
        this.geocodingService = geocodingService;
    }

    public List<Actividad> getActividades() {
        return actividadRepository.findAll();
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
    
    public List<Actividad> getActividadesOrdenadas(String criterio, Double userLat, Double userLon) {
        
        if ("precio".equalsIgnoreCase(criterio)) {
            return actividadRepository.findAllByOrderByPrecioAsc();
        } 
        
        if ("valoracion".equalsIgnoreCase(criterio)) {
            return actividadRepository.findAllByOrderByOrganizacion_Valoracion_PuntuacionDesc();
        }

        if ("distancia".equalsIgnoreCase(criterio) && userLat != null && userLon != null) {
            List<Actividad> actividades = actividadRepository.findAll();
            actividades.sort(Comparator.comparingDouble(a -> 
                calcularDistancia(userLat, userLon, a.getLatitud(), a.getLongitud())
            ));
            return actividades;
        }

        return actividadRepository.findAllByOrderByFechaAsc();
    }
    
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        if (lat1 == 0 || lon1 == 0 || lat2 == 0 || lon2 == 0) return Double.MAX_VALUE;

        final int R = 6371; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}

