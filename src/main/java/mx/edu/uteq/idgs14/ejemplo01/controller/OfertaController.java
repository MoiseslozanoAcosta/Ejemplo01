package mx.edu.uteq.idgs14.ejemplo01.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import lombok.extern.slf4j.Slf4j;
import mx.edu.uteq.idgs14.ejemplo01.model.OfertaEducativa;
import mx.edu.uteq.idgs14.ejemplo01.repository.OfertaEducativaRepository;


@Controller
@Slf4j
public class OfertaController {
    @Autowired
    private OfertaEducativaRepository ofertaEducativaRepository;

    @GetMapping("/oferta")
    public String oferta(Model model) {
        model.addAttribute("activeHome", "");
        model.addAttribute("activeOferta", "active");
        model.addAttribute("activeAspirante", "");
        model.addAttribute("breadcrumb", List.of("Oferta Educativa"));
        List<OfertaEducativa> ofertas = ofertaEducativaRepository.findAll();
        model.addAttribute("ofertas", ofertas);
        return "oferta";
    }
}
