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
import mx.edu.uteq.idgs14.ejemplo01.dto.EmailDTO;

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
     * Formulario manual del panel admin — envía notificación general.
     */
    public void enviarEmail(EmailDTO dto) throws MessagingException {
        Context context = new Context();
        context.setVariable("nombre", dto.getAsunto());
        context.setVariable("mensaje", dto.getMensaje());
        context.setVariable("enlace", null);
        context.setVariable("codigo", null);
        enviar(dto.getDestinario(), "Notificación — UTEQ", context);
    }

    /**
     * Flujo recuperación de contraseña — 4 argumentos:
     * @param destinatario  correo destino
     * @param username      nombre del usuario (para el saludo)
     * @param enlace        URL con el token (para el botón del correo)
     * @param codigo        código de 4 dígitos que el usuario debe ingresar
     */
    public void enviarRecuperacion(String destinatario, String username,
                                   String enlace, String codigo)
            throws MessagingException {

        Context context = new Context();
        context.setVariable("nombre", username);
        context.setVariable("mensaje",
            "Recibimos una solicitud para restablecer la contraseña de tu cuenta.");
        context.setVariable("enlace", enlace);
        context.setVariable("codigo", codigo);
        enviar(destinatario, "Recuperación de contraseña — UTEQ", context);
    }

    private void enviar(String destinatario, String asunto, Context context)
            throws MessagingException {
        String htmlContent = templateEngine.process("email-template", context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(sender);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(htmlContent, true);
        mailSender.send(message);
        log.info("Correo enviado a {} — asunto: {}", destinatario, asunto);
    }
}