package mx.edu.uteq.idgs14.ejemplo01.controller;

import lombok.extern.slf4j.Slf4j;
import mx.edu.uteq.idgs14.ejemplo01.dto.EmailDTO;
import mx.edu.uteq.idgs14.ejemplo01.model.PasswordResetToken;
import mx.edu.uteq.idgs14.ejemplo01.repository.PasswordResetTokenRepository;
import mx.edu.uteq.idgs14.ejemplo01.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Gestión del flujo "Olvidé mi contraseña":
 *
 *  1. Usuario solicita recuperación → se genera un token UUID de 30 min.
 *  2. Se envía un correo AL ADMINISTRADOR con el enlace (porque InMemory
 *     no almacena emails reales de usuario; en producción con BD se
 *     reemplaza emailAdmin por el email del usuario).
 *  3. El admin (o el propio usuario) accede al enlace y establece nueva contraseña.
 *  4. El token se marca como usado y no puede reutilizarse.
 *
 *  SEGURIDAD:
 *  - El mensaje de respuesta es genérico (no revela si el usuario existe).
 *  - Los tokens expirados o ya usados son rechazados.
 *  - La nueva contraseña se hashea con BCrypt antes de guardarse.
 */
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

    /** Email del administrador: se usa porque InMemory no guarda emails reales. */
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

        // Verificar si el usuario existe (respuesta genérica para evitar enumeración)
        try {
            userManager.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            // No revelamos si el usuario existe o no
            attr.addFlashAttribute("mensaje",
                "Si el usuario existe, recibirás las instrucciones en el correo registrado.");
            return "redirect:/forgot-password";
        }

        // Generar token único
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(username);
        resetToken.setExpiracion(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsado(false);
        tokenRepository.save(resetToken);

        String enlace = "http://localhost:8080/reset-password?token=" + token;

        // Construir el email
        EmailDTO dto = new EmailDTO();
        dto.setDestinario(emailAdmin); // En producción: email real del usuario en BD
        dto.setAsunto("Recuperación de contraseña — usuario: " + username);
        dto.setMensaje(
            "Se solicitó restablecer la contraseña del usuario: " + username +
            "\n\nHaz clic en el siguiente enlace (válido 30 minutos):\n" + enlace
        );

        try {
            emailService.enviarEmail(dto);
            log.info("Correo de recuperación enviado para usuario: {}", username);
        } catch (Exception e) {
            log.error("Error al enviar correo de recuperación: {}", e.getMessage());
            // No interrumpimos el flujo para no revelar si el email existe
        }

        attr.addFlashAttribute("mensaje",
            "Si el usuario existe, recibirás las instrucciones en el correo registrado.");
        return "redirect:/forgot-password";
    }

    // ── GET /reset-password ───────────────────────────────────────────────────

    @GetMapping("/reset-password")
    public String resetForm(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty() || opt.get().isExpirado() || opt.get().isUsado()) {
            model.addAttribute("error",
                "El enlace no es válido, ya fue utilizado o expiró. Solicita uno nuevo.");
            model.addAttribute("token", null); // oculta el formulario
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
                "El enlace no es válido o ya expiró. Solicita uno nuevo.");
            return "redirect:/forgot-password";
        }

        // Validación básica de longitud
        if (password == null || password.trim().length() < 6) {
            attr.addFlashAttribute("error",
                "La contraseña debe tener al menos 6 caracteres.");
            return "redirect:/reset-password?token=" + token;
        }

        PasswordResetToken resetToken = opt.get();
        String username = resetToken.getEmail();

        try {
            UserDetails userActual = userManager.loadUserByUsername(username);

            // Actualizar la contraseña con BCrypt
            UserDetails userActualizado = User
                    .withUsername(username)
                    .password(passwordEncoder.encode(password))
                    .authorities(userActual.getAuthorities())
                    .build();

            userManager.updateUser(userActualizado);
            log.info("Contraseña actualizada para el usuario: {}", username);

            // Marcar el token como usado para que no pueda reutilizarse
            resetToken.setUsado(true);
            tokenRepository.save(resetToken);

            attr.addFlashAttribute("exito",
                "Contraseña actualizada correctamente. Ya puedes iniciar sesión.");

        } catch (Exception e) {
            log.error("Error al actualizar contraseña para {}: {}", username, e.getMessage());
            attr.addFlashAttribute("error",
                "Ocurrió un error al actualizar la contraseña. Inténtalo de nuevo.");
        }

        return "redirect:/login";
    }
}