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

    public void enviarEmail(EmailDTO dto) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();
        context.setVariable("nombre", dto.getAsunto());
        context.setVariable("mensaje", dto.getMensaje());
        context.setVariable("codigo", (int) (Math.random() * 9999));

        String htmlContent = templateEngine.process("email-template", context);

        helper.setTo(dto.getDestinario());
        helper.setSubject("Notificacion Especial");
        helper.setText(htmlContent, true);

        mailSender.send(message);



    
    }
}