package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "organizaciones")
public class Organizacion {
    
    @EmbeddedId
    private Identificacion identificacion;

    private String nombre;

    @Embedded
    private Valoracion valoracion;

    @ManyToMany
    @JoinTable(
        name = "seguidores_organizacion",
        joinColumns = @JoinColumn(name = "organizacion_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_email")
    )
    private List<Usuario> seguidores = new ArrayList<>();

    public Organizacion() {}

    public Organizacion(Identificacion identificacion, String nombre, Valoracion valoracion) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.valoracion = valoracion;
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

    public List<Usuario> getSeguidores() {
        return seguidores;
    }

    public void setValoracion(Valoracion valoracion) {
        this.valoracion = valoracion;
    }

    public void eliminarSeguidor(Usuario seguidor) {
                if (seguidores.contains(seguidor)) {
                        seguidores.remove(seguidor);
                }
        }
}



