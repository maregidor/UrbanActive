package es.upm.dit.isst.grupo10.urbanactive.service;


import es.upm.dit.isst.grupo10.urbanactive.model.Email;
import es.upm.dit.isst.grupo10.urbanactive.model.Usuario;
import es.upm.dit.isst.grupo10.urbanactive.repository.UsuarioRepository;


import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findById(new Email(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        System.out.println("LOGIN username =" + username);
        System.out.println("Usuario encontrado" + usuario.getNombre());
        System.out.println("Password BD" + usuario.getPassword());

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail().getDireccion())
                .password(usuario.getPassword())
                .roles("USER")
                .build();

        
    }
    
}