package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;

@Entity
public class CondicionEntorno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String descripcionClima;
    private String nivelTrafico;

    public CondicionEntorno() {
    }

    public CondicionEntorno(String tipo, String descripcionClima, String nivelTrafico) {
        this.tipo = tipo;
        this.descripcionClima = descripcionClima;
        this.nivelTrafico = nivelTrafico;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcionClima() {
        return descripcionClima;
    }

    public void setDescripcionClima(String descripcionClima) {
        this.descripcionClima = descripcionClima;
    }

    public String getNivelTrafico() {
        return nivelTrafico;
    }

    public void setNivelTrafico(String nivelTrafico) {
        this.nivelTrafico = nivelTrafico;
    }
}
