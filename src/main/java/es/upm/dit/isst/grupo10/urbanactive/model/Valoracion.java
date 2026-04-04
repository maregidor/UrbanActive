package es.upm.dit.isst.grupo10.urbanactive.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.*;

@Embeddable
public class Valoracion implements Serializable{

    private double puntuacion;
    private int numeroVotos;

    public Valoracion() {
        this.puntuacion = 0.0;
        this.numeroVotos = 0;
    }

    public Valoracion(double puntuacion, int numeroVotos) {
        if (puntuacion < 0.0 || puntuacion > 5.0) {
            throw new IllegalArgumentException("La puntuación debe estar entre 0.0 y 5.0");
        }
        if (numeroVotos < 0) {
            throw new IllegalArgumentException("El número de votos no puede ser negativo");
        }
        this.puntuacion = puntuacion;
        this.numeroVotos = numeroVotos;
    }

    public double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(double puntuacion) {
        this.puntuacion = puntuacion;
    }

    public int getNumeroVotos() {
        return numeroVotos;
    }

    public void setNumeroVotos(int numeroVotos) {
        this.numeroVotos = numeroVotos;
    }

    public String getEstrellas() {
        int enteras = (int) Math.floor(puntuacion);
        return "⭐".repeat(enteras) + (puntuacion % 1 >= 0.5 ? "½" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Valoracion that = (Valoracion) o;
        return Double.compare(that.puntuacion, puntuacion) == 0 && numeroVotos == that.numeroVotos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(puntuacion, numeroVotos);
    }

    @Override
    public String toString() {
        return String.format("%.1f/5 (%d votos)", puntuacion, numeroVotos);
    }
    
}
