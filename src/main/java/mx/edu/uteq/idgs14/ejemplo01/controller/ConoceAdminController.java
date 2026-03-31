package mx.edu.uteq.idgs14.ejemplo01.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mx.edu.uteq.idgs14.ejemplo01.model.ConoceUteq;
import mx.edu.uteq.idgs14.ejemplo01.repository.ConoceUteqRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/conoce")
public class ConoceAdminController {

    @Autowired
    private ConoceUteqRepository conoceUteqRepository;

    // ── Listado ───────────────────────────────────────────────────────────────

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("activeAdmin", "");
        model.addAttribute("activeAdminConoce", "active");
        model.addAttribute("breadcrumb", List.of("Conoce UTEQ"));
        model.addAttribute("secciones", conoceUteqRepository.findAll());
        return "consola/conoce-admin";
    }

    // ── Alta ──────────────────────────────────────────────────────────────────

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("titulo", "Agregar Sección");
        model.addAttribute("conoceUteq", new ConoceUteq());
        model.addAttribute("tiposSecciones", ConoceUteq.TipoSeccion.values());
        return "consola/conoceFrm-admin";
    }

    // ── Edición ───────────────────────────────────────────────────────────────

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        ConoceUteq seccion = conoceUteqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sección inválida: " + id));
        model.addAttribute("titulo", "Editar Sección");
        model.addAttribute("conoceUteq", seccion);
        model.addAttribute("tiposSecciones", ConoceUteq.TipoSeccion.values());
        return "consola/conoceFrm-admin";
    }

    // ── Guardar ───────────────────────────────────────────────────────────────

    @PostMapping("/save")
    public String save(@Valid ConoceUteq conoceUteq, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("titulo",
                conoceUteq.getId() == null ? "Agregar Sección" : "Editar Sección");
            model.addAttribute("tiposSecciones", ConoceUteq.TipoSeccion.values());
            return "consola/conoceFrm-admin";
        }
        conoceUteqRepository.save(conoceUteq);
        log.info("Sección guardada: {}", conoceUteq.getTipoSeccion());
        return "redirect:/admin/conoce";
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        conoceUteqRepository.deleteById(id);
        log.info("Sección eliminada id: {}", id);
        return "redirect:/admin/conoce";
    }
}