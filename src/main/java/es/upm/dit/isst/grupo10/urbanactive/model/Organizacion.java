package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;

@Entity
@Table (name = "organizaciones")
public class Organizacion {
    
    @EmbeddedId
    private Identificacion identificacion;

    private String nombre;

    public Organizacion() {}

    public Organizacion(Identificacion identificacion, String nombre) {
        this.identificacion = identificacion;
        this.nombre = nombre;
    }

    public Identificacion getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(Identificacion identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
