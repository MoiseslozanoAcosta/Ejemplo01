package mx.edu.uteq.idgs14.ejemplo01.controller;

import lombok.extern.slf4j.Slf4j;
import mx.edu.uteq.idgs14.ejemplo01.model.PasswordResetToken;
import mx.edu.uteq.idgs14.ejemplo01.repository.PasswordResetTokenRepository;
import mx.edu.uteq.idgs14.ejemplo01.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private InMemoryUserDetailsManager userManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Como usamos InMemoryUserDetailsManager, no hay emails reales por usuario.
     * El enlace se envía al correo del administrador configurado en application.properties.
     * En producción (con base de datos) se reemplaza emailAdmin por el email real del usuario.
     */
    @Value("${spring.mail.username}")
    private String emailAdmin;

    // ── GET /forgot-password ──────────────────────────────────────────────────

    @GetMapping("/forgot-password")
    public String forgotForm() {
        return "forgot-password";
    }

    // ── POST /forgot-password ─────────────────────────────────────────────────

    @PostMapping("/forgot-password")
    public String forgotProcess(@RequestParam String username,
                                RedirectAttributes attr) {

        // Respuesta siempre genérica para no revelar si el usuario existe
        final String mensajeGenerico =
            "Si el usuario existe, recibirás las instrucciones en el correo del administrador.";

        try {
            userManager.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.warn("Solicitud de recuperación para usuario inexistente: {}", username);
            attr.addFlashAttribute("mensaje", mensajeGenerico);
            return "redirect:/forgot-password";
        }

        // Generar token único y guardarlo en BD
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(username);           // guarda el username, no el email
        resetToken.setExpiracion(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsado(false);
        tokenRepository.save(resetToken);

        String enlace = "http://localhost:8080/reset-password?token=" + token;

        // Enviar correo usando el método dedicado de recuperación
        try {
            emailService.enviarRecuperacion(emailAdmin, username, enlace);
            log.info("Correo de recuperación enviado para usuario: {}", username);
        } catch (Exception e) {
            log.error("Error enviando correo de recuperación para {}: {}", username, e.getMessage());
            // No interrumpimos el flujo para no revelar información
        }

        attr.addFlashAttribute("mensaje", mensajeGenerico);
        return "redirect:/forgot-password";
    }

    // ── GET /reset-password ───────────────────────────────────────────────────

    @GetMapping("/reset-password")
    public String resetForm(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty() || opt.get().isExpirado() || opt.get().isUsado()) {
            model.addAttribute("error",
                "El enlace no es válido, ya fue utilizado o ha expirado. Solicita uno nuevo.");
            model.addAttribute("token", null);   // oculta el formulario de nueva contraseña
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // ── POST /reset-password ──────────────────────────────────────────────────

    @PostMapping("/reset-password")
    public String resetProcess(@RequestParam String token,
                               @RequestParam String password,
                               RedirectAttributes attr) {

        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty() || opt.get().isExpirado() || opt.get().isUsado()) {
            attr.addFlashAttribute("error",
                "El enlace no es válido o ha expirado. Solicita uno nuevo.");
            return "redirect:/forgot-password";
        }

        if (password == null || password.trim().length() < 6) {
            attr.addFlashAttribute("error",
                "La contraseña debe tener al menos 6 caracteres.");
            return "redirect:/reset-password?token=" + token;
        }

        PasswordResetToken resetToken = opt.get();
        String username = resetToken.getEmail();  // aquí "email" almacena el username

        try {
            UserDetails userActual = userManager.loadUserByUsername(username);

            UserDetails userActualizado = User
                    .withUsername(username)
                    .password(passwordEncoder.encode(password))
                    .authorities(userActual.getAuthorities())
                    .build();

            userManager.updateUser(userActualizado);
            log.info("Contraseña actualizada para: {}", username);

            // Marcar token como usado — no puede reutilizarse
            resetToken.setUsado(true);
            tokenRepository.save(resetToken);

            attr.addFlashAttribute("exito",
                "Contraseña actualizada correctamente. Ya puedes iniciar sesión.");

        } catch (Exception e) {
            log.error("Error al actualizar contraseña de {}: {}", username, e.getMessage());
            attr.addFlashAttribute("error",
                "Ocurrió un error al actualizar la contraseña. Inténtalo de nuevo.");
        }

        return "redirect:/login";
    }
}