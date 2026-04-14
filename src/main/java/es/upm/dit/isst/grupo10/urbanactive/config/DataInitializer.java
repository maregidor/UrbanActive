package es.upm.dit.isst.grupo10.urbanactive.config;

import es.upm.dit.isst.grupo10.urbanactive.model.*;
import es.upm.dit.isst.grupo10.urbanactive.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrganizacionRepository organizacionRepository;
    private final EspacioPublicoRepository espacioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(ActividadRepository actividadRepository, 
                           UsuarioRepository usuarioRepository, 
                           OrganizacionRepository organizacionRepository,
                           EspacioPublicoRepository espacioRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.actividadRepository = actividadRepository;
        this.usuarioRepository = usuarioRepository;
        this.organizacionRepository = organizacionRepository;
        this.espacioRepository = espacioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        
        if (actividadRepository.count() > 0) {
            System.out.println("Base de datos ya poblada. Saltando inicialización.");
            return;
        }

        System.out.println("Cargando datos de prueba en la BBDD...");

        // 1. ESPACIOS PÚBLICOS
        EspacioPublico retiro = espacioRepository.save(new EspacioPublico("Parque del Retiro", "Parque", "Madrid Centro"));
        EspacioPublico canal = espacioRepository.save(new EspacioPublico("Instalaciones Canal", "Polideportivo", "Chamberí"));
        EspacioPublico madridRio = espacioRepository.save(new EspacioPublico("Madrid Río", "Parque", "Arganzuela"));

        // 2. TRES ORGANIZADORES (Organizaciones con sus valoraciones)
        Organizacion madridActivo = organizacionRepository.save(new Organizacion(
            new Identificacion("CIF", "B12345678"), "Madrid Activo SL", new Valoracion(4.5, 100)));
        
        Organizacion crossfitMad = organizacionRepository.save(new Organizacion(
            new Identificacion("CIF", "B87654321"), "Crossfit Madrid", new Valoracion(4.9, 250)));
            
        Organizacion urbanYoga = organizacionRepository.save(new Organizacion(
            new Identificacion("CIF", "B11223344"), "Urban Yoga Studio", new Valoracion(2.2, 50)));

        // 3. USUARIO (Participante)
        Usuario juan = usuarioRepository.save(new Usuario(new Email("usuario1@gmail.com"), "Juan Pérez", new Nivel(5.0), passwordEncoder.encode("1234")));

        // 4. SEIS ACTIVIDADES DISTINTAS

        // Actividad 1: Yoga al amanecer (Barata, Alta valoración, Retiro)
        actividadRepository.save(crearActividad("Yoga al amanecer", "Bienestar", 8.50, 2.0, 
            LocalDate.now().plusDays(2), LocalTime.of(8, 30), 
            urbanYoga, retiro, 40.4153, -3.6839, 20, 
            "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b"));

        // Actividad 2: HIIT Extremo (Cara, Muy valorada, Canal, Hoy)
        actividadRepository.save(crearActividad("HIIT Extremo", "Fuerza", 15.0, 8.5, 
            LocalDate.now(), LocalTime.of(19, 0), 
            crossfitMad, canal, 40.4385, -3.7071, 10, 
            "https://images.unsplash.com/photo-1534438327276-14e5300c3a48"));

        // Actividad 3: Running 5K (Gratis, Valoración media, Madrid Río)
        actividadRepository.save(crearActividad("Running 5K técnica", "Cardio", 0.0, 4.0, 
            LocalDate.now().plusDays(3), LocalTime.of(10, 0), 
            madridActivo, madridRio, 40.4033, -3.7162, 50, 
            "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8"));

        // Actividad 4: Pilates Park (Precio medio, Retiro)
        actividadRepository.save(crearActividad("Pilates Park", "Bienestar", 12.0, 3.5, 
            LocalDate.now().plusDays(5), LocalTime.of(11, 0), 
            urbanYoga, retiro, 40.4160, -3.6840, 15, 
            "https://images.unsplash.com/photo-1518611012118-696072aa579a"));

        // Actividad 5: Entrenamiento Funcional (Madrid Activo, Canal)
        actividadRepository.save(crearActividad("Funcional Urban", "Fuerza", 5.0, 6.0, 
            LocalDate.now().plusDays(1), LocalTime.of(18, 30), 
            madridActivo, canal, 40.4390, -3.7080, 12, 
            "https://images.unsplash.com/photo-1517836357463-d25dfeac3438"));

        // Actividad 6: Zumba al aire libre (Gratis, Madrid Río)
        actividadRepository.save(crearActividad("Zumba Urban", "Ocio", 0.0, 3.0, 
            LocalDate.now().plusDays(4), LocalTime.of(12, 0), 
            madridActivo, madridRio, 40.4045, -3.7170, 40, 
            "https://images.unsplash.com/photo-1524594152303-9fd13543fe6e"));

        System.out.println("¡Datos cargados con éxito! Se han creado 6 actividades y 3 organizaciones.");
    }

    // Método auxiliar para limpiar el código de creación de actividades
    private Actividad crearActividad(String titulo, String tipo, Double precio, Double nivelVal, 
                                     LocalDate fecha, LocalTime hora, Organizacion org, 
                                     EspacioPublico esp, Double lat, Double lon, int plazas, String img) {
        Actividad a = new Actividad();
        a.setTitulo(titulo);
        a.setTipo(tipo);
        a.setPrecio(precio);
        a.setNivel(new Nivel(nivelVal));
        a.setFecha(fecha);
        a.setHora(hora);
        a.setOrganizacion(org);
        a.setEspacioPublico(esp);
        a.setLatitud(lat);
        a.setLongitud(lon);
        a.setPlazasTotales(plazas);
        a.setPlazasDisponibles(plazas);
        a.setImagen(img);
        a.setDuracion("60 min");
        a.setDescripcion("Actividad de " + tipo + " organizada por " + org.getNombre());
        return a;
    }
}