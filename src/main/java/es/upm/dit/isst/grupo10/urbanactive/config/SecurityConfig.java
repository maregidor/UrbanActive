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
            .requestMatchers("/login", "/css/**", "/js/**", "/h2-console/**").permitAll()
            .requestMatchers("/", "/actividades").permitAll() // La lista de actividades es pública
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

