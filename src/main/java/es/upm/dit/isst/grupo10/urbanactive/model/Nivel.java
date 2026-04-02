package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Nivel implements Serializable {

    private double valor; 

    public Nivel() {
        this.valor = 0.0;
    }

    public Nivel(double valor) {
        if (valor < 0.0 || valor > 10.0) {
            throw new IllegalArgumentException("El valor del nivel debe estar entre 0.0 y 10.0");
        }
        this.valor = valor;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nivel that = (Nivel) o;
        return Double.compare(that.valor, valor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        String category;
        int val = (int) Math.round(valor);
        if (val <= 3) {
            category = "Principiante";
        } else if (val <= 6) {
            category = "Intermedio";
        } else if (val <= 8) {
            category = "Avanzado";
        } else {
            category = "Experto";
        }
        return category + " (" + val + "/10)";
    }

}