package mx.edu.uteq.idgs14.ejemplo01.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapaController {

    @GetMapping("/mapa")
    public String mapa(Model model) {
        model.addAttribute("activeHome", "");
        model.addAttribute("activeOferta", "");
        model.addAttribute("activeAspirante", "");
        model.addAttribute("activemapa", "active");
        model.addAttribute("message", "Que pedo con tu mapa");
        model.addAttribute("breadcrumb", List.of("Mapa de Mexico"));
        return "mapa";
    }
    
}
