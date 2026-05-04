package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;

@Entity
@Table(name = "organizaciones")
public class Organizacion {
    
    @Embedded
    private Identificacion identificacion;

    @Id
    private Email email;
    private String nombre;
    private String password;
    private String cif;
    private String actividad;
    private Valoracion valoracion;

@Column(unique = true, nullable = false)
private String slug;


    public Organizacion(Identificacion identificacion, String nombre, Valoracion valoracion, Email email, String password, String cif, String actividad) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.valoracion = valoracion;
        this.email = email;
        this.password = password;
        this.cif = cif;
        this.actividad = actividad;
    }

public Organizacion() {}

public Organizacion(Identificacion identificacion, String nombre, Valoracion valoracion) {
this.identificacion = identificacion;
this.nombre = nombre;
this.valoracion = valoracion;
}

@PrePersist
@PreUpdate
private void generarSlugSiHaceFalta() {
if (nombre != null && !nombre.isBlank()) {
this.slug = slugify(nombre);
}
}

private String slugify(String texto) {
return texto.toLowerCase()
.trim()
.replace("á", "a")
.replace("é", "e")
.replace("í", "i")
.replace("ó", "o")
.replace("ú", "u")
.replace("ñ", "n")
.replaceAll("[^a-z0-9\\s-]", "")
.replaceAll("\\s+", "-")
.replaceAll("-+", "-");
}

public Identificacion getIdentificacion() {
return identificacion;
}

public String getNombre() {
return nombre;
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



