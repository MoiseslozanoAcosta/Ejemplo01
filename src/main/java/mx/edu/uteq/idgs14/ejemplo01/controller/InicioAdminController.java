package mx.edu.uteq.idgs14.ejemplo01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioAdminController {
    @GetMapping("/admin")
    public String inicio(Model model) {

        model.addAttribute("activeAdmin", "active");
        model.addAttribute("activeAdminDivision", "");
        model.addAttribute("activeAdminOferta", "");

        return "consola/indexadmin";
    }
}
