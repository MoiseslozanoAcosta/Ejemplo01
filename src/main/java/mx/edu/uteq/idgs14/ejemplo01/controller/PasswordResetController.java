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

    @Value("${spring.mail.username}")
    private String emailAdmin;

    // ── PASO 1: Formulario — pedir username ───────────────────────────────────

    @GetMapping("/forgot-password")
    public String forgotForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotProcess(@RequestParam String username,
                                RedirectAttributes attr) {

        final String mensajeGenerico =
            "Si el usuario existe, recibirás un código de 4 dígitos en el correo del administrador.";

        try {
            userManager.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.warn("Solicitud para usuario inexistente: {}", username);
            attr.addFlashAttribute("mensaje", mensajeGenerico);
            return "redirect:/forgot-password";
        }

        // Generar código de 4 dígitos y token UUID
        String codigo = String.format("%04d", (int) (Math.random() * 10000));
        String token  = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(username);
        resetToken.setCodigo(codigo);
        resetToken.setExpiracion(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsado(false);
        tokenRepository.save(resetToken);

        String enlace = "http://localhost:8080/verify-code?token=" + token;

        try {
            // 4 argumentos: destinatario, username, enlace, codigo
            emailService.enviarRecuperacion(emailAdmin, username, enlace, codigo);
            log.info("Código de recuperación enviado para: {}", username);
        } catch (Exception e) {
            log.error("Error enviando correo de recuperación: {}", e.getMessage());
        }

        attr.addFlashAttribute("mensaje", mensajeGenerico);
        return "redirect:/forgot-password";
    }

    // ── PASO 2: Formulario — ingresar código de 4 dígitos ────────────────────

    @GetMapping("/verify-code")
    public String verifyCodeForm(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty() || opt.get().isExpirado() || opt.get().isUsado()) {
            model.addAttribute("error",
                "El enlace no es válido o ha expirado. Solicita uno nuevo.");
            return "forgot-password";
        }

        model.addAttribute("token", token);
        return "verify-code";
    }

    @PostMapping("/verify-code")
    public String verifyCodeProcess(@RequestParam String token,
                                    @RequestParam String codigo,
                                    RedirectAttributes attr) {

        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty() || opt.get().isExpirado() || opt.get().isUsado()) {
            attr.addFlashAttribute("error",
                "El enlace no es válido o ha expirado. Solicita uno nuevo.");
            return "redirect:/forgot-password";
        }

        PasswordResetToken resetToken = opt.get();

        // Verificar que el código ingresado coincida
        if (!resetToken.getCodigo().equals(codigo.trim())) {
            attr.addFlashAttribute("error", "Código incorrecto. Inténtalo de nuevo.");
            attr.addFlashAttribute("token", token);
            return "redirect:/verify-code?token=" + token;
        }

        // Código correcto → redirigir al formulario de nueva contraseña
        return "redirect:/reset-password?token=" + token;
    }

    // ── PASO 3: Formulario — nueva contraseña ─────────────────────────────────

    @GetMapping("/reset-password")
    public String resetForm(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);

        if (opt.isEmpty() || opt.get().isExpirado() || opt.get().isUsado()) {
            model.addAttribute("error",
                "El enlace no es válido o ha expirado. Solicita uno nuevo.");
            model.addAttribute("token", null);
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
        String username = resetToken.getEmail();

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
                "Ocurrió un error. Inténtalo de nuevo.");
        }

        return "redirect:/login";
    }
}