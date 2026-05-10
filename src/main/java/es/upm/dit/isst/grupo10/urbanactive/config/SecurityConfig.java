package es.upm.dit.isst.grupo10.urbanactive.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
            .requestMatchers("/actividades/nueva", "/actividades/guardar").hasAnyRole("USER", "ORGA")
            .requestMatchers("/", "/actividades", "/actividades/{id}", "/register/**").permitAll()

            .requestMatchers("/mis-actividades/**", "/mi-perfil-organizacion").hasRole("ORGA")

            .requestMatchers("/reservas/**", "/mis-seguidos", "/mi-perfil").hasRole("USER")

            .anyRequest().authenticated() 
        )
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/actividades", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/actividades")
        );

    return http.build();
}

@Bean
public BCryptPasswordEncoder passwordEncoder() {
return new BCryptPasswordEncoder();
}
}

