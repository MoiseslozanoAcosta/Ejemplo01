package mx.edu.uteq.idgs14.ejemplo01.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sender;

    /**
     * Envía el correo de recuperación con botón azul y código gris.
     */
    public void enviarRecuperacion(String destinatario, String username, String enlace, String codigo)
            throws MessagingException {

        Context context = new Context();
        context.setVariable("nombre", username);
        context.setVariable("mensaje", "Recibimos una solicitud para restablecer la contraseña de tu cuenta.");
        context.setVariable("enlace", enlace);
        context.setVariable("codigo", codigo); // ✅ Ahora el código viaja a la plantilla

        enviar(destinatario, "Recuperación de contraseña — UTEQ", context);
    }

    private void enviar(String destinatario, String asunto, Context context)
            throws MessagingException {

        // Asegúrate que tu HTML se llame "email-template.html"
        String htmlContent = templateEngine.process("email-template", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(sender);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("Correo enviado a {}", destinatario);
    }
}