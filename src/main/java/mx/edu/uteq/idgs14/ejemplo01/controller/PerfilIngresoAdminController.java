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
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import mx.edu.uteq.idgs14.ejemplo01.model.PerfilIngreso;
import mx.edu.uteq.idgs14.ejemplo01.repository.OfertaEducativaRepository;
import mx.edu.uteq.idgs14.ejemplo01.repository.PerfilIngresoRepository;

@Controller
@RequestMapping("/admin/perfil")
public class PerfilIngresoAdminController {

    private static final Logger log = LoggerFactory.getLogger(PerfilIngresoAdminController.class);

    @Autowired
    private PerfilIngresoRepository perfilIngresoRepository;

    @Autowired
    private OfertaEducativaRepository ofertaEducativaRepository;

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("activeAdmin", "");
        model.addAttribute("activeAdminDivision", "");
        model.addAttribute("activeAdminOferta", "");
        model.addAttribute("activeAdminPerfil", "active");
        model.addAttribute("breadcrumb", List.of("Perfiles de Ingreso"));
        model.addAttribute("perfiles", perfilIngresoRepository.findAll());
        model.addAttribute("perfilIngreso", new PerfilIngreso());
        model.addAttribute("ofertas", ofertaEducativaRepository.findAll());
        return "consola/perfil-admin";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("titulo", "Agregar Perfil de Ingreso");
        model.addAttribute("perfilIngreso", new PerfilIngreso());
        model.addAttribute("ofertas", ofertaEducativaRepository.findAll());
        return "consola/perfilFrm-admin";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        PerfilIngreso perfil = perfilIngresoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Perfil inválido: " + id));
        model.addAttribute("titulo", "Editar Perfil de Ingreso");
        model.addAttribute("perfilIngreso", perfil);
        model.addAttribute("ofertas", ofertaEducativaRepository.findAll());
        return "consola/perfilFrm-admin";
    }

    @PostMapping("/save")
    public String save(@Valid PerfilIngreso perfilIngreso, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("titulo", perfilIngreso.getId() == null ? "Agregar Perfil" : "Editar Perfil");
            model.addAttribute("ofertas", ofertaEducativaRepository.findAll());
            return "consola/perfilFrm-admin";
        }
        perfilIngresoRepository.save(perfilIngreso);
        return "redirect:/admin/perfil";
    }

    @GetMapping(value = "/api/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getPerfilById(@PathVariable Integer id) {
        try {
            PerfilIngreso perfil = perfilIngresoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado: " + id));
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Perfil no encontrado"));
        }
    }

    @PostMapping(value = "/api/save", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> savePerfilAsync(@Valid @RequestBody PerfilIngreso perfil, Errors errores) {
        log.info("Guardando perfil: {}", perfil);
        if (errores.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Errores de validación"));
        }
        try {
            PerfilIngreso saved = perfilIngresoRepository.save(perfil);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Perfil guardado correctamente",
                    "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al guardar"));
        }
    }
}