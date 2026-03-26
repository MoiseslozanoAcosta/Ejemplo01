package mx.edu.uteq.idgs14.ejemplo01.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        // ADMIN: Tiene acceso a TODO
        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build());

        // COORD: Solo accede a Divisiones
        manager.createUser(User.withUsername("coord")
                .password(passwordEncoder().encode("coord123"))
                .roles("COORD")
                .build());

        // USER: Solo ve la parte pública
        manager.createUser(User.withUsername("user")
                .password(passwordEncoder().encode("123456"))
                .roles("USER")
                .build());

        return manager;
    }
}