package mx.edu.uteq.idgs14.ejemplo01.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mx.edu.uteq.idgs14.ejemplo01.model.Directorio;
import mx.edu.uteq.idgs14.ejemplo01.repository.DirectorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/directorio")
public class DirectorioAdminController {

    @Autowired
    private DirectorioRepository directorioRepository;

    // ── Listado ───────────────────────────────────────────────────────────────

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("activeAdmin", "");
        model.addAttribute("activeAdminDirectorio", "active");
        model.addAttribute("breadcrumb", List.of("Directorio"));
        model.addAttribute("contactos", directorioRepository.findAll());
        return "consola/directorio-admin";
    }

    // ── Alta ──────────────────────────────────────────────────────────────────

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("titulo", "Agregar Contacto");
        model.addAttribute("directorio", new Directorio());
        return "consola/directorioFrm-admin";
    }

    // ── Edición ───────────────────────────────────────────────────────────────

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Directorio contacto = directorioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contacto inválido: " + id));
        model.addAttribute("titulo", "Editar Contacto");
        model.addAttribute("directorio", contacto);
        return "consola/directorioFrm-admin";
    }

    // ── Guardar ───────────────────────────────────────────────────────────────

    @PostMapping("/save")
    public String save(@Valid Directorio directorio, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("titulo",
                directorio.getId() == null ? "Agregar Contacto" : "Editar Contacto");
            return "consola/directorioFrm-admin";
        }
        directorioRepository.save(directorio);
        log.info("Contacto guardado: {}", directorio.getNombre());
        return "redirect:/admin/directorio";
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        directorioRepository.deleteById(id);
        log.info("Contacto eliminado id: {}", id);
        return "redirect:/admin/directorio";
    }
}