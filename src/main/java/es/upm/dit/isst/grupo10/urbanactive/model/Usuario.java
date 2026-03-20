package es.upm.dit.isst.grupo10.urbanactive.model;

public class Usuario {

        private Email email;
        private String nombre;
        private Nivel nivelExperiencia;

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