package es.upm.dit.isst.grupo10.urbanactive.model;

public class Actividad {

    private Long id;
    private String tipo;
    private String descripcion;
    private Nivel nivel;

    public Actividad(Long id, String tipo, String descripcion, Nivel nivel) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.nivel = nivel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

}