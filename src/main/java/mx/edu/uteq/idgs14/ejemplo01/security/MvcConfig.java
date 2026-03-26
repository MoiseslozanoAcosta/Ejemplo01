package mx.edu.uteq.idgs14.ejemplo01.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // Agregada
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import mx.edu.uteq.idgs14.ejemplo01.converter.StringToDivisionConverter;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private StringToDivisionConverter stringToDivisionConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToDivisionConverter);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registro) {
        registro.addViewController("/login").setViewName("login"); // 👈 apunta a login.html
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/home", "/oferta", "/division", "/mapa",
                                 "/admisiones", "/valores", "/js/*", "/css/*",
                                 "/login").permitAll()
                .requestMatchers("/consola/divisiones/**").hasAnyRole("ADMIN", "COORD")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/email/**").hasAnyRole("ADMIN", "COORD")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin", true) // 👈 redirige al admin
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")   // 👈 logoutSuccessUrl, no logoutSuccesUrl
                .permitAll()
            );

        return http.build();
    }
}