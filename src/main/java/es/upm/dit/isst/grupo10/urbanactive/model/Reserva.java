package es.upm.dit.isst.grupo10.urbanactive.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "usuario_email_direccion", referencedColumnName = "direccion")
    })
    private Usuario usuario;
    
    @Column(name = "actividad_id")
    private Long actividadId;
    
    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoReserva estado;
    
    @Column(name = "precio_pagado")
    private double precioPagado;

    public enum EstadoReserva {
        PENDIENTE_CONFIRMACION,
        CONFIRMADA,
        CANCELADA,
        COMPLETADA
    }

    // Constructor vacío para JPA
    public Reserva() {}

    public Reserva(Long id, Usuario usuario, Long actividadId, double precioPagado) {
        this.id = id;
        this.usuario = usuario;
        this.actividadId = actividadId;
        this.fechaReserva = LocalDateTime.now();
        this.estado = EstadoReserva.PENDIENTE_CONFIRMACION;
        this.precioPagado = precioPagado;
    }

    // Getters y Setters para JPA
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Long getActividadId() { return actividadId; }
    public void setActividadId(Long actividadId) { this.actividadId = actividadId; }
    
    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
    
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    
    public double getPrecioPagado() { return precioPagado; }
    public void setPrecioPagado(double precioPagado) { this.precioPagado = precioPagado; }

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
                ", usuario=" + usuario.getNombre() +
                ", actividadId=" + actividadId +
                ", estado=" + estado +
                '}';
    }
}
