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
        
        @Column(nullable = false)
        private String password;

        public Usuario() {
        }

        public Usuario(Email email, String nombre, Nivel nivelExperiencia, String password) {
                this.email = email;
                this.nombre = nombre;
                this.nivelExperiencia = nivelExperiencia;
                this.password = password;
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

        public void setNivelExperiencia(Nivel nivelExperiencia) {
                this.nivelExperiencia = nivelExperiencia;
        }

        public void setPassword(String password){
                this.password = password;
        }

}