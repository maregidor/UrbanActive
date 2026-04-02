package es.upm.dit.isst.grupo10.urbanactive.config;

import es.upm.dit.isst.grupo10.urbanactive.model.*;
import es.upm.dit.isst.grupo10.urbanactive.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ActividadRepository actividadRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrganizacionRepository organizacionRepository;
    private final EspacioPublicoRepository espacioRepository;

    // Inyectamos todos los repositorios por constructor
    public DataInitializer(ActividadRepository actividadRepository, 
                           UsuarioRepository usuarioRepository, 
                           OrganizacionRepository organizacionRepository,
                           EspacioPublicoRepository espacioRepository) {
        this.actividadRepository = actividadRepository;
        this.usuarioRepository = usuarioRepository;
        this.organizacionRepository = organizacionRepository;
        this.espacioRepository = espacioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        
        System.out.println("Cargando datos de prueba en la BBDD...");

        // 1. Crear un Espacio Público
        EspacioPublico retiro = new EspacioPublico("Parque del Retiro", "Parque", "Madrid Centro");
        espacioRepository.save(retiro);

        // 2. Crear una Organización (con su Value Object Identificacion)
        Identificacion cifMadrid = new Identificacion("CIF", "B12345678");
        Organizacion madridActivo = new Organizacion(cifMadrid, "Madrid Activo SL");
        organizacionRepository.save(madridActivo);

        // 3. Crear un Usuario (con su Email como ID y Nivel)
        Email emailUser = new Email("usuario1@gmail.com");
        Nivel nivelIniciado = new Nivel(5.0); // Suponiendo que Nivel guarda un double
        Usuario juan = new Usuario(emailUser, "Juan Pérez", nivelIniciado);
        usuarioRepository.save(juan);

        // 4. Crear la Actividad principal
        Actividad yoga = new Actividad();
        yoga.setTitulo("Yoga al amanecer");
        yoga.setTipo("Bienestar");
        yoga.setDescripcion("Sesión de yoga para todos los niveles frente al estanque.");
        yoga.setNivel(new Nivel(3.0));
        yoga.setFecha(LocalDate.now().plusDays(2));
        yoga.setHora(LocalTime.of(8, 30));
        yoga.setDuracion("60 min");
        yoga.setImagen("https://images.unsplash.com/photo-1544367567-0f2fcb009e0b");
        
        // Asignamos el organizador (usando la opción de columnas separadas)
        yoga.setOrganizacion(madridActivo);
        yoga.setPrecio(10.0); // Como es organización, ponemos precio
        
        // Asignamos el lugar
        yoga.setEspacioPublico(retiro);
        
        // Añadimos a Juan como participante
        yoga.getParticipantes().add(juan);

        // Guardamos la actividad (esto creará también la relación en la tabla de unión)
        actividadRepository.save(yoga);

        System.out.println("¡Datos cargados con éxito! Actividad ID: " + yoga.getId());
    }
}