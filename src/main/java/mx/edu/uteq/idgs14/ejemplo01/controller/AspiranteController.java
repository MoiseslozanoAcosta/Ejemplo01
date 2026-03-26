package mx.edu.uteq.idgs14.ejemplo01.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AspiranteController {
    
    @GetMapping("/admisiones")
    public String admisiones(Model model) {
        model.addAttribute("activeHome", "");
        model.addAttribute("activeOferta", "");
        model.addAttribute("activeAspirante", "active");
        model.addAttribute("message", "Admisiones de la UTEQ");
        model.addAttribute("breadcrumb", List.of("Aspirantes", "Admisiones"));
        
        return "admisiones";
    }

    @GetMapping("/valores")
    public String valores(Model model) {
        model.addAttribute("activeHome", "");
        model.addAttribute("activeOferta", "");
        model.addAttribute("activeAspirante", "active");
        model.addAttribute("message", "Valores de la UTEQ");
        model.addAttribute("breadcrumb", List.of("Aspirantes", "Valores"));
        return "valores";
    }
}

