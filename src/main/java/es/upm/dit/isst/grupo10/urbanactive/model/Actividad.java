package es.upm.dit.isst.grupo10.urbanactive.model;

public class Actividad {

    private Long id;
    private String tipo;
    private String descripcion;
    private String nivel;
    private int plazasDisponibles;

    public Actividad(Long id, String tipo, String descripcion, String nivel, int plazasDisponibles) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.nivel = nivel;
        this.plazasDisponibles = plazasDisponibles;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNivel() {
        return nivel;
    }

    public int getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public void setPlazasDisponibles(int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }
}