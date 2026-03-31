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
     * Envía un correo de notificación general usando la plantilla email-template.html.
     * Usa las variables: nombre (asunto), mensaje, codigo (número aleatorio).
     */
    public void enviarEmail(EmailDTO dto) throws MessagingException {
        Context context = new Context();
        context.setVariable("nombre", dto.getAsunto());
        context.setVariable("mensaje", dto.getMensaje());
        context.setVariable("codigo", (int) (Math.random() * 9999));
        context.setVariable("enlace", null); // sin enlace en correos normales

        enviar(dto.getDestinario(), "Notificación — UTEQ", context);
    }

    /**
     * Envía el correo de recuperación de contraseña.
     * Muestra el nombre de usuario, un mensaje explicativo y el botón con el enlace.
     *
     * @param destinatario  dirección de correo del destinatario
     * @param username      nombre de usuario para personalizar el saludo
     * @param enlace        URL del token de recuperación (válida 30 min)
     */
    public void enviarRecuperacion(String destinatario, String username, String enlace)
            throws MessagingException {

        Context context = new Context();
        context.setVariable("nombre", username);
        context.setVariable("mensaje",
            "Recibimos una solicitud para restablecer la contraseña de tu cuenta.");
        context.setVariable("enlace", enlace);
        context.setVariable("codigo", null); // sin código en correos de recuperación

        enviar(destinatario, "Recuperación de contraseña — UTEQ", context);
    }

    /** Método interno que construye y despacha el MimeMessage. */
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