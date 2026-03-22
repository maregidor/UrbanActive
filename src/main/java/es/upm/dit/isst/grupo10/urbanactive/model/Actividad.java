package es.upm.dit.isst.grupo10.urbanactive.model;

public class Actividad {

    private Long id;
    private String tipo;
    private String descripcion;
    private Nivel nivel;

    // Campos para US2
    private String organizador;
    private String fecha;
    private String hora;
    private String ubicacion;
    private double precio;
    private int plazas;
    private String duracion;
    private String imagen;

    public Actividad(Long id, String tipo, String descripcion, Nivel nivel,
                     String organizador, String fecha, String hora,
                     String ubicacion, double precio, int plazas,
                     String duracion, String imagen) {

        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.nivel = nivel;
        this.organizador = organizador;
        this.fecha = fecha;
        this.hora = hora;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.plazas = plazas;
        this.duracion = duracion;
        this.imagen = imagen;
    }

    // getters y setters (muy importante)

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Nivel getNivel() { return nivel; }

    public String getOrganizador() { return organizador; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getUbicacion() { return ubicacion; }
    public double getPrecio() { return precio; }
    public int getPlazas() { return plazas; }
    public String getDuracion() { return duracion; }
    public String getImagen() { return imagen; }
}