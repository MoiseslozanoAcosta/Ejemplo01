package mx.edu.uteq.idgs14.ejemplo01.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import mx.edu.uteq.idgs14.ejemplo01.model.Division;
import mx.edu.uteq.idgs14.ejemplo01.repository.DivisionRepository;

@Controller
@Slf4j
public class DivisionController {
   
    @Autowired
    private DivisionRepository divisionRepository;

    @GetMapping("/division")
    public String division(Model model) {
        model.addAttribute("activeHome", "");
        model.addAttribute("activeOferta", "");
        model.addAttribute("activeDivision", "active");
        model.addAttribute("activeAspirante", "");
        model.addAttribute("breadcrumb", List.of("Divisiones"));

         List<Division> lista = divisionRepository.findAll();
        model.addAttribute("nombreDivisiones", lista);
        return "division";
    }

} 