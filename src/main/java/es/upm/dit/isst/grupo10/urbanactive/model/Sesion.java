package es.upm.dit.isst.grupo10.urbanactive.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Sesion {
    private Long id;
    private Long actividadId;
    private LocalDateTime fechaHora;
    private int plazasTotales;
    private int plazasDisponibles;
    private String ubicacion;

    public Sesion(Long id, Long actividadId, LocalDateTime fechaHora, int plazasTotales, String ubicacion) {
        this.id = id;
        this.actividadId = actividadId;
        this.fechaHora = fechaHora;
        this.plazasTotales = plazasTotales;
        this.plazasDisponibles = plazasTotales;
        this.ubicacion = ubicacion;
    }

    public Long getId() { return id; }
    public Long getActividadId() { return actividadId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getPlazasTotales() { return plazasTotales; }
    public int getPlazasDisponibles() { return plazasDisponibles; }
    public String getUbicacion() { return ubicacion; }

    public void setPlazasDisponibles(int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }

    public boolean hayPlazasDisponibles() {
        return plazasDisponibles > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sesion sesion = (Sesion) o;
        return Objects.equals(id, sesion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sesion{" +
                "id=" + id +
                ", fechaHora=" + fechaHora +
                ", plazasDisponibles=" + plazasDisponibles +
                '}';
    }
}
