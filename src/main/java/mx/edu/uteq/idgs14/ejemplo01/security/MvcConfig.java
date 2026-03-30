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

    // Redirige según el rol después del login
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
                // USER y cualquier otro → zona pública
                response.sendRedirect("/home");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz

                // ── Zona pública ──────────────────────────────────────────
                .requestMatchers(
                    "/", "/home", "/oferta", "/division", "/mapa",
                    "/admisiones", "/valores",
                    "/css/**", "/js/**", "/images/**",
                    "/login", "/forgot-password", "/reset-password"
                ).permitAll()

                // ── Zona privada: solo ADMIN accede a TODO /admin ─────────
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/admin/division/**").hasAnyRole("ADMIN", "COORD")
                .requestMatchers("/admin/oferta/**").hasAnyRole("ADMIN", "COORD")
                .requestMatchers("/admin/perfil/**").hasAnyRole("ADMIN", "ASPIRANTE")
                .requestMatchers("/admin/conoce/**").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers("/admin/directorio/**").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers("/email/**").hasAnyRole("ADMIN", "COORD")

                // Cualquier otra ruta autenticada
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler()) // redirige por rol
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/home")   // al cerrar sesión → zona pública
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/acceso-denegado")
            );

        return http.build();
    }
}