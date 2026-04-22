package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.dto.ActividadContexto;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.dto.TrafficInfo;
import es.upm.dit.isst.grupo10.urbanactive.dto.WeatherInfo;
import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import org.springframework.stereotype.Service;

@Service
public class ActividadContextService {

    private final GeocodingService geocodingService;
    private final AemetService aemetService;
    private final TrafficService trafficService;

    public ActividadContextService(GeocodingService geocodingService,
                                   AemetService aemetService,
                                   TrafficService trafficService) {
        this.geocodingService = geocodingService;
        this.aemetService = aemetService;
        this.trafficService = trafficService;
    }

    public ActividadContexto getContexto(Actividad actividad, GeoPoint userPoint) {
        GeoPoint puntoActividad = null;

        if (actividad.getLatitud() != null && actividad.getLongitud() != null) {
            puntoActividad = new GeoPoint(
                    actividad.getLatitud(),
                    actividad.getLongitud(),
                    actividad.getTitulo()
            );
        } else if (actividad.getEspacioPublico() != null) {
            puntoActividad = new GeoPoint(
                actividad.getEspacioPublico().getLatitud(),
                actividad.getEspacioPublico().getLongitud(),
                actividad.getEspacioPublico().getNombre()
            );
        }

        WeatherInfo weather = aemetService.getWeatherMadrid(actividad.getFecha());

        TrafficInfo traffic;
        if (userPoint != null && puntoActividad != null) {
            traffic = trafficService.calcularTraficoTrayecto(userPoint, puntoActividad);
        } else {
            traffic = new TrafficInfo(
                    "No disponible",
                    0,
                    "Permite la ubicación para ver el trayecto",
                    0.0,
                    "-",
                    "-"
            );
        }

        return new ActividadContexto(puntoActividad, weather, traffic);
    }
}