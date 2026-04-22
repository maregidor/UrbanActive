package es.upm.dit.isst.grupo10.urbanactive.model;
import jakarta.persistence.*;

@Entity
public class EspacioPublico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipo;
    private Double latitud;
    private Double longitud;

    public EspacioPublico() {
    }

    public EspacioPublico(String nombre, String tipo, Double latitud, Double longitud) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}