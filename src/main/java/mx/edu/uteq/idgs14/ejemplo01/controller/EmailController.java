package mx.edu.uteq.idgs14.ejemplo01.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import lombok.extern.slf4j.Slf4j;
import mx.edu.uteq.idgs14.ejemplo01.dto.EmailDTO;
import mx.edu.uteq.idgs14.ejemplo01.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class EmailController {

    @Autowired
    private EmailService emaoService;

    @GetMapping("/email/form")
    public String form(Model model) {
        // 1. Inicializamos el DTO vacío para que Thymeleaf no marque error
        model.addAttribute("emailDTO", new EmailDTO());
        
        // 2. IMPORTANTE: Si tu archivo se llama email-from.html,
        // el return debe ser exactamente "email-from" (sin la diagonal /)
        return "email-from"; 
    }

    // 3. QUITAMOS @Async de aquí. 
    // El @Async debe ir en el SERVICIO, no en el controlador.
    @PostMapping("/email/enviar")
    public String enviarCorreo(@ModelAttribute EmailDTO emailDTO) {
        try {
            log.info("Enviando correo a: {}", emailDTO.getDestinario());
            emaoService.enviarEmail(emailDTO);
        } catch (Exception e) {
            log.error("Error al enviar correo: {}", e.getMessage());
        }
        return "redirect:/?enviado=true";
    }
}