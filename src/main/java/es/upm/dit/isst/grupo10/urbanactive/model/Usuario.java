package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

        @EmbeddedId
        private Email email;

        private String nombre;

        @Embedded
        private Nivel nivelExperiencia;

        public Usuario() {
        }

        public Usuario(Email email, String nombre, Nivel nivelExperiencia) {
                this.email = email;
                this.nombre = nombre;
                this.nivelExperiencia = nivelExperiencia;
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

        public Nivel getNivelExperiencia() {
                return nivelExperiencia;
        }

        public void setNivelExperiencia(Nivel nivelExperiencia) {
                this.nivelExperiencia = nivelExperiencia;
        }

}