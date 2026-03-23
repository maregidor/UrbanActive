package es.upm.dit.isst.grupo10.urbanactive.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reserva {
    private Long id;
    private Email emailUsuario;
    private Long actividadId;
    private Long sesionId;
    private LocalDateTime fechaReserva;
    private EstadoReserva estado;
    private double precioPagado;

    public enum EstadoReserva {
        PENDIENTE_CONFIRMACION,
        CONFIRMADA,
        CANCELADA,
        COMPLETADA
    }

    public Reserva(Long id, Email emailUsuario, Long actividadId, Long sesionId, double precioPagado) {
        this.id = id;
        this.emailUsuario = emailUsuario;
        this.actividadId = actividadId;
        this.sesionId = sesionId;
        this.fechaReserva = LocalDateTime.now();
        this.estado = EstadoReserva.PENDIENTE_CONFIRMACION;
        this.precioPagado = precioPagado;
    }

    public Long getId() { return id; }
    public Email getEmailUsuario() { return emailUsuario; }
    public Long getActividadId() { return actividadId; }
    public Long getSesionId() { return sesionId; }
    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public EstadoReserva getEstado() { return estado; }
    public double getPrecioPagado() { return precioPagado; }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public void confirmarReserva() {
        this.estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelarReserva() {
        this.estado = EstadoReserva.CANCELADA;
    }

    public void completarReserva() {
        this.estado = EstadoReserva.COMPLETADA;
    }

    public boolean estaActiva() {
        return estado == EstadoReserva.PENDIENTE_CONFIRMACION || estado == EstadoReserva.CONFIRMADA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(id, reserva.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", emailUsuario=" + emailUsuario.getDireccion() +
                ", actividadId=" + actividadId +
                ", sesionId=" + sesionId +
                ", estado=" + estado +
                '}';
    }
}
