package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

        @EmbeddedId
        private Email email;

        private String nombre;

        @Embedded
        private Nivel nivelExperiencia;

        @ManyToMany
        @JoinTable(
                name = "seguidores",
                joinColumns = @JoinColumn(name = "usuario_email"),
                inverseJoinColumns = @JoinColumn(name = "seguidor_email")
        )
        private List<Usuario> seguidores = new ArrayList<>();
        
        @ManyToMany
        @JoinTable(
                name = "siguiendo",
                joinColumns = @JoinColumn(name = "usuario_email"),
                inverseJoinColumns = @JoinColumn(name = "siguiendo_email")
        )
        private List<Usuario> siguiendo = new ArrayList<>();

        @Column(nullable = false)
        private String password;

        @OneToMany
        @JoinTable(
                name = "usuario_reservas",
                joinColumns = @JoinColumn(name = "usuario_email"),
                inverseJoinColumns = @JoinColumn(name = "reserva_id")
        )
        private List<Reserva> reservas = new ArrayList<>();

        @Column(unique = true, nullable = false)
        private String slug;





        public Usuario() {
        }

        public Usuario(Email email, String nombre, Nivel nivelExperiencia, String password, List<Usuario> seguidores, List<Usuario> siguiendo) {
                this.email = email;
                this.nombre = nombre;
                this.nivelExperiencia = nivelExperiencia;
                this.password = password;
                this.seguidores = seguidores;
                this.siguiendo = siguiendo;
        }

        public Email getEmail() {
                return email;
        }

        public void setNombre(String nombre) {
                this.nombre = nombre;
        }

        public String getNombre() {
                return nombre;
        }

        public String getPassword(){
                return password;
        }

        public Nivel getNivelExperiencia() {
                return nivelExperiencia;
        }

        public List<Usuario> getSeguidores() {
                return seguidores;
        }

        public List<Usuario> getSiguiendo() {
                return siguiendo;
        }

        public void setNivelExperiencia(Nivel nivelExperiencia) {
                this.nivelExperiencia = nivelExperiencia;
        }

        public void setPassword(String password){
                this.password = password;
        }

        public void eliminarSeguidor(Usuario seguidor) {
                if (seguidores.contains(seguidor)) {
                        seguidores.remove(seguidor);
                        seguidor.getSiguiendo().remove(this);
                }
        }

        public void seguir(Usuario usuario) {
                if (!siguiendo.contains(usuario)) {
                        siguiendo.add(usuario);
                        usuario.getSeguidores().add(this);
                }
        }

        public void dejarDeSeguir(Usuario usuario) {
                if (siguiendo.contains(usuario)) {
                        siguiendo.remove(usuario);
                        usuario.getSeguidores().remove(this);
                }
        }

        @PrePersist
        @PreUpdate
        private void generarSlugSiHaceFalta() {
        if ((slug == null || slug.isBlank()) && nombre != null && !nombre.isBlank()) {
                String base = slugify(nombre);
                String random = java.util.UUID.randomUUID().toString().substring(0, 6);
                this.slug = base + "-" + random;
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

        public String getSlug() {
        return slug;
        }

        public void setSlug(String slug) {
        this.slug = slug;
        }

}