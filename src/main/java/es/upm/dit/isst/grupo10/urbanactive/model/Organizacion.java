package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "organizaciones")
public class Organizacion {
    
    @Embedded
    private Identificacion identificacion;

    @Id
    private Email email;
    private String nombre;
    private String password;
    private String cif;
    private String actividad;

    @Embedded
    private Valoracion valoracion;

    public Organizacion() {}

    public Organizacion(Identificacion identificacion, String nombre, Valoracion valoracion, Email email, String password, String cif, String actividad) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.valoracion = valoracion;
        this.email = email;
        this.password = password;
        this.cif = cif;
        this.actividad = actividad;
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

    public Valoracion getValoracion() {
        return valoracion;
    }

    public void setValoracion(Valoracion valoracion) {
        this.valoracion = valoracion;
    }
    public Email getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public void setEmail(Email email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getCif() {
        return cif;
    }
    public void setCif(String cif) {
        this.cif = cif;
    }
    public String getActividad() {
        return actividad;
    }
    public void setActividad(String actividad) {
        this.actividad = actividad;
    }
}



