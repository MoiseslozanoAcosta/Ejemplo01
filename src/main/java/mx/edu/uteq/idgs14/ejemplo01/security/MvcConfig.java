package mx.edu.uteq.idgs14.ejemplo01.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.uteq.idgs14.ejemplo01.converter.StringToDivisionConverter;
import mx.edu.uteq.idgs14.ejemplo01.converter.StringToOfertaEducativaConverter;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private StringToDivisionConverter stringToDivisionConverter;

    @Autowired
    private StringToOfertaEducativaConverter stringToOfertaEducativaConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToDivisionConverter);
        registry.addConverter(stringToOfertaEducativaConverter);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registro) {
        registro.addViewController("/login").setViewName("login");
        registro.addViewController("/acceso-denegado").setViewName("error/403");
    }

    /**
     * Necesario para que Spring Security detecte cuando una sesión HTTP expira
     * y pueda aplicar el control de sesiones concurrentes.
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /** Redirige al usuario a la sección correcta según su rol después del login. */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication auth) -> {
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                response.sendRedirect("/admin");
            } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_COORD"))) {
                response.sendRedirect("/admin/division");
            } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EDITOR"))) {
                response.sendRedirect("/admin/conoce");
            } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ASPIRANTE"))) {
                response.sendRedirect("/admin/perfil");
            } else {
                response.sendRedirect("/home");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── Autorización de rutas ─────────────────────────────────────────
            .authorizeHttpRequests(authz -> authz

                // Zona pública
                .requestMatchers(
                    "/", "/home", "/oferta", "/division", "/mapa",
                    "/admisiones", "/valores",
                    "/css/**", "/js/**", "/images/**",
                    "/login", "/forgot-password/**", "/reset-password/**"
                ).permitAll()

                // Zona privada por rol
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/admin/division/**").hasAnyRole("ADMIN", "COORD")
                .requestMatchers("/admin/oferta/**").hasAnyRole("ADMIN", "COORD")
                .requestMatchers("/admin/perfil/**").hasAnyRole("ADMIN", "ASPIRANTE")
                .requestMatchers("/admin/conoce/**").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers("/admin/directorio/**").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers("/email/**").hasAnyRole("ADMIN", "COORD")

                .anyRequest().authenticated()
            )

            // ── Login ─────────────────────────────────────────────────────────
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // ── Logout ────────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home?logout=true")
                .invalidateHttpSession(true)      // destruye la sesión del servidor
                .deleteCookies("JSESSIONID")      // elimina la cookie del cliente
                .permitAll()
            )

            // ── Gestión de sesiones ───────────────────────────────────────────
            .sessionManagement(session -> session
                // Un mismo usuario sólo puede tener 1 sesión activa a la vez.
                // Si abre otra, la sesión anterior queda inválida.
                .maximumSessions(1)
                .expiredUrl("/login?session=expired")   // redirige al expirar
            )

            // ── Errores de acceso ─────────────────────────────────────────────
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/acceso-denegado")
            );

        return http.build();
    }
}