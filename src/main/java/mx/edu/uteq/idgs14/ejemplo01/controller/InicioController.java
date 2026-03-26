package mx.edu.uteq.idgs14.ejemplo01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/home")
    public String inicio(Model model) {
        model.addAttribute("activeHome", "active");
        model.addAttribute("activeOferta", "");
        model.addAttribute("activeAspirante", "");

        return "index";
    }
    
}
