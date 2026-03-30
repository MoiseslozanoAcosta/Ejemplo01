package mx.edu.uteq.idgs14.ejemplo01.controller;

import lombok.extern.slf4j.Slf4j;
import mx.edu.uteq.idgs14.ejemplo01.dto.EmailDTO;
import mx.edu.uteq.idgs14.ejemplo01.model.PasswordResetToken;
import mx.edu.uteq.idgs14.ejemplo01.repository.PasswordResetTokenRepository;
import mx.edu.uteq.idgs14.ejemplo01.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Value("${spring.mail.username}")
    private String emailAdmin;

    @GetMapping("/forgot-password")
    public String forgotForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotProcess(@RequestParam String username,
                                RedirectAttributes attr) {
        try {
            userManager.loadUserByUsername(username);
        } catch (Exception e) {
            attr.addFlashAttribute("mensaje",
                "Si el usuario existe, recibirás las instrucciones en el correo del administrador.");
            return "redirect:/forgot-password";
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(username);
        resetToken.setExpiracion(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsado(false);
        tokenRepository.save(resetToken);

        String enlace = "http://localhost:8080/reset-password?token=" + token;

        EmailDTO dto = new EmailDTO();
        dto.setDestinario(emailAdmin);
        dto.setAsunto("Recuperación de contraseña — usuario: " + username);
        dto.setMensaje("Se solicitó recuperar la contraseña del usuario: "
                + username + "\n\nEnlace válido 30 min:\n" + enlace);

        try {
            emailService.enviarEmail(dto);
            log.info("Email de recuperación enviado para: {}", username);
        } catch (Exception e) {
            log.error("Error enviando email: {}", e.getMessage());
        }

        attr.addFlashAttribute("mensaje",
            "Si el usuario existe, recibirás las instrucciones en el correo del administrador.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetForm(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty() || opt.get().isExpirado() || opt.get().isUsado()) {
            model.addAttribute("error", "El enlace no es válido o ya expiró.");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetProcess(@RequestParam String token,
                               @RequestParam String password,
                               RedirectAttributes attr) {

        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty() || opt.get().isExpirado() || opt.get().isUsado()) {
            attr.addFlashAttribute("error", "El enlace no es válido o ya expiró.");
            return "redirect:/login";
        }

        PasswordResetToken resetToken = opt.get();
        String username = resetToken.getEmail();

        try {
            UserDetails userActual = userManager.loadUserByUsername(username);

            // Ahora User está importado correctamente
            UserDetails userActualizado = User
                    .withUsername(username)
                    .password(passwordEncoder.encode(password))
                    .authorities(userActual.getAuthorities())
                    .build();

            userManager.updateUser(userActualizado);
            log.info("Contraseña actualizada para: {}", username);

            resetToken.setUsado(true);
            tokenRepository.save(resetToken);

            attr.addFlashAttribute("exito",
                "Contraseña actualizada. Inicia sesión con tu nueva contraseña.");

        } catch (Exception e) {
            log.error("Error actualizando contraseña: {}", e.getMessage());
            attr.addFlashAttribute("error", "Error al actualizar la contraseña.");
        }

        return "redirect:/login";
    }
}