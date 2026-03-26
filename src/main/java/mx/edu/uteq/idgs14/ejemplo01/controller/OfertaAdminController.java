package mx.edu.uteq.idgs14.ejemplo01.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mx.edu.uteq.idgs14.ejemplo01.model.OfertaEducativa;
import mx.edu.uteq.idgs14.ejemplo01.repository.DivisionRepository;
import mx.edu.uteq.idgs14.ejemplo01.repository.OfertaEducativaRepository;

@Controller
@Slf4j
@RequestMapping("/admin/oferta")
public class OfertaAdminController {

    @Autowired
    private OfertaEducativaRepository ofertaEducativaRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("activeAdmin", "");
        model.addAttribute("activeAdminDivision", "");
        model.addAttribute("activeAdminOferta", "active");
        model.addAttribute("breadcrumb", List.of("Ofertas"));
        model.addAttribute("ofertas", ofertaEducativaRepository.findAll());
        model.addAttribute("divisiones", divisionRepository.findAll());
        model.addAttribute("ofertaEducativa", new OfertaEducativa());
        return "consola/oferta-admin";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("titulo", "Agregar Nueva Oferta Educativa");
        model.addAttribute("ofertaEducativa", new OfertaEducativa());
        model.addAttribute("divisiones", divisionRepository.findAll());
        return "consola/ofertafrm-addmin";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        OfertaEducativa oferta = ofertaEducativaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Oferta inválida: " + id));
        model.addAttribute("ofertaEducativa", oferta);
        model.addAttribute("titulo", "Editar Oferta Educativa");
        model.addAttribute("divisiones", divisionRepository.findAll());
        return "consola/ofertafrm-addmin";
    }

    @PostMapping("/save")
    public String save(@Valid OfertaEducativa ofertaEducativa, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("divisiones", divisionRepository.findAll());
            return "consola/ofertafrm-addmin";
        }
        ofertaEducativaRepository.save(ofertaEducativa);
        return "redirect:/admin/oferta";
    }

    @GetMapping(value = "/api/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getOfertaById(@PathVariable Integer id) {
        try {
            OfertaEducativa oferta = ofertaEducativaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada: " + id));
            return ResponseEntity.ok(oferta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Oferta no encontrada wey"));
        }
    }

    @PostMapping(value = "/api/save", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> saveOfertaAsync(@Valid @RequestBody OfertaEducativa oferta, Errors errores) {
        log.info("Guardando oferta: {}", oferta);
        if (errores.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Errores de validación wey"));
        }
        try {
            OfertaEducativa saved = ofertaEducativaRepository.save(oferta);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "La Oferta se guardo correctamente wey",
                    "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al guardar wey"));
        }
    }
}