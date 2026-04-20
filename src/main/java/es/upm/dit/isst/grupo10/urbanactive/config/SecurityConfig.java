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
        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
        .requestMatchers("/reservas/**").authenticated()
        .requestMatchers("/h2-console/**").permitAll()
        .anyRequest().permitAll()
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
    System.out.println("Configuring security: permitAll for /login, /css/**, /js/**, /h2-console/**; authenticated for /reservas/**; permitAll for others");
    return http.build();
}

@Bean
public BCryptPasswordEncoder passwordEncoder() {
return new BCryptPasswordEncoder();
}
}

