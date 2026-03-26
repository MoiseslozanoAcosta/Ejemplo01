package mx.edu.uteq.idgs14.ejemplo01.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import mx.edu.uteq.idgs14.ejemplo01.model.Division;
import mx.edu.uteq.idgs14.ejemplo01.repository.DivisionRepository;

@Controller
@RequestMapping("/admin/division")
public class DivisionAdminController {

    private static final Logger log = LoggerFactory.getLogger(DivisionAdminController.class);

    @Autowired
    private DivisionRepository divisionRepository;

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("activeAdmin", "");
        model.addAttribute("activeAdminDivision", "active");
        model.addAttribute("activeAdminOferta", "");
        model.addAttribute("breadcrumb", List.of("Divisiones"));
        model.addAttribute("division", new Division());
        model.addAttribute("divisiones", divisionRepository.findAll());
        return "consola/division-admin";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("titulo", "Agregar División");
        model.addAttribute("division", new Division());
        return "consola/divisionFrm-admin";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("División inválida: " + id));
        model.addAttribute("titulo", "Editar División");
        model.addAttribute("division", division);
        return "consola/divisionFrm-admin";
    }

    @GetMapping(value = "/api/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getDivisionById(@PathVariable Integer id) {
        try {
            Division division = divisionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("División no encontrada: " + id));
            return ResponseEntity.ok(division);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "División no encontrada"));
        }
    }

    @PostMapping("/save")
    public String save(@Valid Division division, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("titulo", division.getId() == null ? "Agregar División" : "Editar División");
            return "consola/divisionFrm-admin";
        }
        divisionRepository.save(division);
        return "redirect:/admin/division";
    }

    @PostMapping(value = "/api/save", consumes = "application/json", produces = "application/json")
    @ResponseBody // 👈 necesitas este import:
                  // org.springframework.web.bind.annotation.ResponseBody
    public ResponseEntity<?> saveDivisionAsync(@Valid @RequestBody Division division, Errors errores) {
        log.info("Guardando división: {}", division);
        if (errores.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Errores de validación"));
        }
        try {
            Division saved = divisionRepository.save(division);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "División guardada correctamente",
                    "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al guardar"));
        }
    }
}
