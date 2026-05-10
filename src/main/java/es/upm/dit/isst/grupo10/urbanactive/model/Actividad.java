package es.upm.dit.isst.grupo10.urbanactive.model;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 40)
    private String tipo;

    @Column(length = 40)
    private String titulo;

    @Column(length = 150)
    private String descripcion;

    @Embedded
    private Nivel nivel;

    private LocalDate fecha;
    private LocalTime hora;
    private int plazasTotales;
    private int plazasDisponibles;
    private String duracion;
    private String imagen;
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "usuario_email")
    private Usuario usuarioOrganizador;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "organizacion_tipo", referencedColumnName = "tipo"),
        @JoinColumn(name = "organizacion_numero", referencedColumnName = "numero")
    })
    private Organizacion organizacion;

    @ManyToOne
    @JoinColumn(name = "espacio_id")
    private EspacioPublico espacioPublico;

    @ManyToOne(cascade = CascadeType.ALL)
    private CondicionEntorno condicionesEntorno;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name="actividad_reservas",
        joinColumns = @JoinColumn(name="actividad_id"),
        inverseJoinColumns = @JoinColumn(name="reserva_id")
    )
    private List<Reserva> reservas = new ArrayList<>();

    private Double latitud;
    private Double longitud;

    public Actividad() {}

    // --- MÉTODOS DE LÓGICA ---
    public String getNombreOrganizador() {
        if (this.organizacion != null) return organizacion.getNombre();
        if (this.usuarioOrganizador != null) return usuarioOrganizador.getNombre();
        return "Sin organizador";
    }


    public List<Usuario> getParticipantes() {
        List<Usuario> participantes = new ArrayList<>();
        for (Reserva reserva : reservas) {
            participantes.add(reserva.getUsuario());
        }
        return participantes;
    }

    public void añadirReserva(Reserva reserva) {
        this.reservas.add(reserva);
        reserva.setActividadId(this.id);
    }

    // --- GETTERS (El de usuarioOrganizador es la clave del error) ---
    public Usuario getUsuarioOrganizador() { return usuarioOrganizador; }
    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public Nivel getNivel() { return nivel; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public String getDuracion() { return duracion; }
    public String getImagen() { return imagen; }
    public Double getPrecio() { return precio; }
    public Organizacion getOrganizacion() { return organizacion; }
    public EspacioPublico getEspacioPublico() { return espacioPublico; }
    public CondicionEntorno getCondicionesEntorno() { return condicionesEntorno; }
    public List<Reserva> getReservas() { return reservas; }
    public int getPlazasTotales() { return plazasTotales; }
    public int getPlazasDisponibles() { return plazasDisponibles; }
    public Double getLatitud() { return latitud; }
    public Double getLongitud() { return longitud; }

    // --- SETTERS ---
    public void setPlazasTotales(int plazasTotales) { this.plazasTotales = plazasTotales; }
    public void setPlazasDisponibles(int plazasDisponibles) { this.plazasDisponibles = plazasDisponibles; }
    public void setId(Long id) { this.id = id; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setNivel(Nivel nivel) { this.nivel = nivel; }
    public void setUsuarioOrganizador(Usuario usuarioOrganizador) { this.usuarioOrganizador = usuarioOrganizador; }
    public void setOrganizacion(Organizacion organizacion) { this.organizacion = organizacion; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public void setEspacioPublico(EspacioPublico espacioPublico) { this.espacioPublico = espacioPublico; }
    public void setCondicionesEntorno(CondicionEntorno condicionesEntorno) { this.condicionesEntorno = condicionesEntorno; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

}