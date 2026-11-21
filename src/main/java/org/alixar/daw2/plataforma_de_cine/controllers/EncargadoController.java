
package org.alixar.daw2.plataforma_de_cine.controllers;

import jakarta.validation.Valid;
import org.alixar.daw2.plataforma_de_cine.entities.Encargado;
import org.alixar.daw2.plataforma_de_cine.repositories.EncargadoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/encargados")
public class EncargadoController {

    private final EncargadoRepository encargadoRepository;

    public EncargadoController(EncargadoRepository encargadoRepository) {
        this.encargadoRepository = encargadoRepository;
    }

    // Mostrar todos los encargados
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("encargados", encargadoRepository.findAll());
        return "encargado";
    }

    // Mostrar el formulario para añadir un encargado
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("encargado", new Encargado());
        model.addAttribute("tituloFormulario", "encargado.form.nuevo");
        return "encargado-form";
    }

    // Mostrar el formulario para editar un encargado
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model,
                         RedirectAttributes redirectAttributes) {

        Encargado encargado = encargadoRepository.findById(id).orElse(null);

        if (encargado == null) {
            redirectAttributes.addFlashAttribute("mensaje", "encargado.error.noEncontrado");
            return "redirect:/encargados";
        }

        model.addAttribute("encargado", encargado);
        model.addAttribute("tituloFormulario", "encargado.form.editar");
        return "encargado-form";
    }

    // Guardar un encargado nuevo o los cambios de uno existente
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("encargado") Encargado encargado,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("tituloFormulario",
                    encargado.getId() == null
                            ? "encargado.form.nuevo"
                            : "encargado.form.editar");

            return "encargado-form";
        }

        encargadoRepository.save(encargado);

        redirectAttributes.addFlashAttribute("mensaje",
                encargado.getId() == null
                        ? "encargado.mensaje.creado"
                        : "encargado.mensaje.actualizado");

        return "redirect:/encargados";
    }

    // Eliminar un encargado
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {

        if (!encargadoRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("mensaje", "encargado.error.noEncontrado");
            return "redirect:/encargados";
        }

        try {
            encargadoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "encargado.mensaje.eliminado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "encargado.error.noSePuedeEliminar");
        }

        return "redirect:/encargados";
    }
}
