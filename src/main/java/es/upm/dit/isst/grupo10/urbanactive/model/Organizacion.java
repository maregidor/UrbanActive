package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;

@Entity
@Table(name = "organizaciones")
public class Organizacion {

@EmbeddedId
private Identificacion identificacion;

@Column(unique = true, nullable = false)
private String slug;

private String nombre;

@Embedded
private Valoracion valoracion;

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

public void setIdentificacion(Identificacion identificacion) {
this.identificacion = identificacion;
}

public String getSlug() {
return slug;
}

public void setSlug(String slug) {
this.slug = slug;
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
}



