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
    @JoinColumn(name = "organizacion_email")
    private Organizacion organizacion;

    @ManyToOne
    @JoinColumn(name = "espacio_id")
    private EspacioPublico espacioPublico;

    @ManyToOne(cascade = CascadeType.ALL)
    private CondicionEntorno condicionesEntorno;

    @ManyToMany
    @JoinTable(
        name = "actividad_participantes",
        joinColumns = @JoinColumn(name = "actividad_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List <Usuario> participantes = new ArrayList<>();

    private Double latitud;
    private Double longitud;

    public Actividad() {}

    public String getNombreOrganizador() {
        if (this.organizacion != null) return organizacion.getNombre();
        if (this.usuarioOrganizador != null) return usuarioOrganizador.getNombre();
        return "Sin organizador";
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getDuracion() {
        return duracion;
    }

    public String getImagen() {
        return imagen;
    }

    public Double getPrecio() {
        return precio;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public EspacioPublico getEspacioPublico() {
        return espacioPublico;
    }

    public CondicionEntorno getCondicionesEntorno() {
        return condicionesEntorno;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public int getPlazasTotales() {
        return plazasTotales;
    }

    public Double getLatitud() { 
        return latitud; 
    }

    public Double getLongitud() {
         return longitud;
    }

    public void setPlazasTotales(int plazasTotales) {
        this.plazasTotales = plazasTotales;
    }

    public int getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public void setPlazasDisponibles(int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public void setUsuarioOrganizador(Usuario usuarioOrganizador) {
        this.usuarioOrganizador = usuarioOrganizador;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public void setEspacioPublico(EspacioPublico espacioPublico) {
        this.espacioPublico = espacioPublico;
    }

    public void setCondicionesEntorno(CondicionEntorno condicionesEntorno) {
        this.condicionesEntorno = condicionesEntorno;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    public void setLatitud(Double latitud) { 
        this.latitud = latitud; 
    }
    
    public void setLongitud(Double longitud) { 
        this.longitud = longitud;
    }
}