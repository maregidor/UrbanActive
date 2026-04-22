package es.upm.dit.isst.grupo10.urbanactive.config;

import es.upm.dit.isst.grupo10.urbanactive.model.*;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder; // Importante
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Inyectamos el encoder

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            
            Email email = new Email("usuario1@gmail.com");
            Nivel nivelInicial = new Nivel(); 
            
            // Encriptamos la contraseña "1234" antes de guardarla
            String passwordEncriptada = passwordEncoder.encode("1234");

            Usuario usuarioPrueba = new Usuario(
                email,
                "Juan Pérez",
                nivelInicial,
                passwordEncriptada, // Guardamos el hash, no el texto plano
                new ArrayList<>(),
                new ArrayList<>()
            );

            usuarioRepository.save(usuarioPrueba);

            System.out.println("✅ Usuario de prueba creado con contraseña encriptada (BCrypt).");
        }
    }
}