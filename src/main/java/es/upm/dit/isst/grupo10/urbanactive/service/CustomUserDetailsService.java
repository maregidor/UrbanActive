package es.upm.dit.isst.grupo10.urbanactive.service;


import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Organizacion;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.OrganizacionRepository;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;

import java.util.Optional;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final OrganizacionRepository organizacionRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository,
                                    OrganizacionRepository organizacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.organizacionRepository = organizacionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Email email = new Email(username);

        // 🔍 1. Buscar usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            System.out.println("LOGIN USUARIO: " + username);

            return User.withUsername(usuario.getEmail().getDireccion())
                    .password(usuario.getPassword())
                    .roles("USER")
                    .build();
        }

        // 🔍 2. Buscar organización
        Optional<Organizacion> orgOpt = organizacionRepository.findById(email);
        if (orgOpt.isPresent()) {
            Organizacion org = orgOpt.get();

            System.out.println("LOGIN ORGANIZACION: " + username);

            return User.withUsername(org.getEmail().getDireccion())
                    .password(org.getPassword())
                    .roles("ORG") // 👈 importante diferenciar
                    .build();
        }

        // ❌ No existe ninguno
        throw new UsernameNotFoundException("Usuario u organización no encontrado");
    }
}