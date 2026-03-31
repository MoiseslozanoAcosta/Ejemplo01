package mx.edu.uteq.idgs14.ejemplo01.controller;

import mx.edu.uteq.idgs14.ejemplo01.model.ConoceUteq;
import mx.edu.uteq.idgs14.ejemplo01.repository.ConoceUteqRepository;
import mx.edu.uteq.idgs14.ejemplo01.repository.DirectorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NosotrosController {

    @Autowired
    private ConoceUteqRepository conoceUteqRepository;

    @Autowired
    private DirectorioRepository directorioRepository;

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        model.addAttribute("activeHome", "");
        model.addAttribute("activeOferta", "");
        model.addAttribute("activeAspirante", "");
        model.addAttribute("activeNosotros", "active");
        model.addAttribute("breadcrumb", List.of("Nosotros"));

        // Cargar cada sección por tipo
        model.addAttribute("mision",
            conoceUteqRepository.findByTipoSeccion(ConoceUteq.TipoSeccion.MISION).orElse(null));
        model.addAttribute("vision",
            conoceUteqRepository.findByTipoSeccion(ConoceUteq.TipoSeccion.VISION).orElse(null));
        model.addAttribute("politica",
            conoceUteqRepository.findByTipoSeccion(ConoceUteq.TipoSeccion.POLITICA).orElse(null));
        model.addAttribute("objetivos",
            conoceUteqRepository.findByTipoSeccion(ConoceUteq.TipoSeccion.OBJETIVOS_SGE).orElse(null));
        model.addAttribute("valores",
            conoceUteqRepository.findByTipoSeccion(ConoceUteq.TipoSeccion.VALORES).orElse(null));

        // Directorio público (solo activos)
        model.addAttribute("directorio",
            directorioRepository.findByActivoTrueOrderByOrdenAsc());

        return "nosotros";
    }
}