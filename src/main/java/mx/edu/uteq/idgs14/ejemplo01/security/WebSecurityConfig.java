package mx.edu.uteq.idgs14.ejemplo01.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Tipo de retorno cambiado a InMemoryUserDetailsManager
    // así Spring puede inyectarlo como UserDetailsService Y como InMemoryUserDetailsManager
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build());

        manager.createUser(User.withUsername("coord")
                .password(passwordEncoder().encode("coord123"))
                .roles("COORD")
                .build());

        manager.createUser(User.withUsername("editor")
                .password(passwordEncoder().encode("editor123"))
                .roles("EDITOR")
                .build());

        manager.createUser(User.withUsername("aspirante")
                .password(passwordEncoder().encode("aspirante123"))
                .roles("ASPIRANTE")
                .build());

        manager.createUser(User.withUsername("user")
                .password(passwordEncoder().encode("user123"))
                .roles("USER")
                .build());

        return manager;
    }
}