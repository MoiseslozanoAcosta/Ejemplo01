package mx.edu.uteq.idgs14.ejemplo01.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Configuración de usuarios en memoria con sus roles.
 *
 * Roles disponibles y sus permisos:
 * ─────────────────────────────────────────────────────────
 *  ADMIN     → acceso total: /admin/**, /admin/division/**,
 *              /admin/oferta/**, /admin/perfil/**,
 *              /admin/conoce/**, /admin/directorio/**, /email/**
 *
 *  COORD     → divisiones, ofertas y email:
 *              /admin/division/**, /admin/oferta/**, /email/**
 *
 *  EDITOR    → contenido institucional:
 *              /admin/conoce/**, /admin/directorio/**
 *
 *  ASPIRANTE → perfiles de ingreso:
 *              /admin/perfil/**
 *
 *  USER      → zona pública únicamente:
 *              /, /home, /oferta, /division, /mapa,
 *              /admisiones, /valores
 * ─────────────────────────────────────────────────────────
 *
 * NOTA: En producción reemplazar InMemoryUserDetailsManager
 *       por un UserDetailsService con base de datos.
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        // ADMIN — acceso completo al panel
        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build());

        // COORD — administra divisiones, ofertas y puede enviar emails
        manager.createUser(User.withUsername("coord")
                .password(passwordEncoder().encode("coord123"))
                .roles("COORD")
                .build());

        // EDITOR — gestiona el contenido institucional (Conoce UTEQ, Directorio)
        manager.createUser(User.withUsername("editor")
                .password(passwordEncoder().encode("editor123"))
                .roles("EDITOR")
                .build());

        // ASPIRANTE — sólo puede consultar y gestionar perfiles de ingreso
        manager.createUser(User.withUsername("aspirante")
                .password(passwordEncoder().encode("aspirante123"))
                .roles("ASPIRANTE")
                .build());

        // USER — acceso únicamente a la zona pública
        manager.createUser(User.withUsername("user")
                .password(passwordEncoder().encode("user123"))
                .roles("USER")
                .build());

        return manager;
    }
}