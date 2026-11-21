package org.alixar.daw2.plataforma_de_cine.controllers;

import jakarta.validation.Valid;
import org.alixar.daw2.plataforma_de_cine.entities.Encargado;
import org.alixar.daw2.plataforma_de_cine.repositories.EncargadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Controller
@RequestMapping("/encargados")
public class EncargadoController {

    private static final Logger logger = LoggerFactory.getLogger(EncargadoController.class);

    private final EncargadoRepository encargadoRepository;
    private final MessageSource messageSource;

    public EncargadoController(EncargadoRepository encargadoRepository, MessageSource messageSource) {
        this.encargadoRepository = encargadoRepository;
        this.messageSource = messageSource;
    }


    @GetMapping
    public String listEncargados(Model model, Locale locale) {
        logger.info("Solicitando la lista de todos los encargados...");
        List<Encargado> listEncargados = null;
        try {
            listEncargados = encargadoRepository.findAll();
            logger.info("Se han cargado {} encargados.", listEncargados.size());
        } catch (DataAccessException e) {
            logger.error("Error al listar los encargados: {}", e.getMessage());
            model.addAttribute("errorMessage",
                    messageSource.getMessage("msg.encargado-controller.list.error", null, locale));
        }
        model.addAttribute("listEncargados", listEncargados);
        return "encargados";
    }


    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para un nuevo encargado.");
        model.addAttribute("encargado", new Encargado());
        return "encargado-form";
    }


    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario de edición para el encargado con ID {}", id);
        try {
            Optional<Encargado> encargado = encargadoRepository.findById(id);
            if (encargado.isEmpty()) {
                logger.warn("No se encontró el encargado con ID {}", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.encargado-controller.edit.notFound", null, locale));
                return "redirect:/encargados";
            }
            model.addAttribute("encargado", encargado.get());
        } catch (DataAccessException e) {
            logger.error("Error al obtener el encargado con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.encargado-controller.edit.error", null, locale));
            return "redirect:/encargados";
        }
        return "encargado-form";
    }


    @PostMapping("/insert")
    public String insertEncargado(@Valid @ModelAttribute("encargado") Encargado encargado, BindingResult result,
                                  RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nuevo encargado {}", encargado.getNombre());
        if (result.hasErrors()) {
            return "encargado-form";
        }
        try {
            encargadoRepository.save(encargado);
            logger.info("Encargado {} insertado con éxito.", encargado.getNombre());
        } catch (DataAccessException e) {
            logger.error("Error al insertar el encargado {}: {}", encargado.getNombre(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.encargado-controller.insert.error", null, locale));
        }
        return "redirect:/encargados";
    }


    @PostMapping("/update")
    public String updateEncargado(@Valid @ModelAttribute("encargado") Encargado encargado, BindingResult result,
                                  RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Actualizando encargado con ID {}", encargado.getId());
        if (result.hasErrors()) {
            restoreSala(encargado);
            return "encargado-form";
        }
        try {
            encargadoRepository.save(encargado);
            logger.info("Encargado con ID {} actualizado con éxito.", encargado.getId());
        } catch (DataAccessException e) {
            logger.error("Error al actualizar el encargado con ID {}: {}", encargado.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.encargado-controller.update.error", null, locale));
        }
        return "redirect:/encargados";
    }

    @PostMapping("/delete")
    public String deleteEncargado(@RequestParam("id") Long id, RedirectAttributes redirectAttributes,
                                  Locale locale) {
        logger.info("Eliminando encargado con ID {}", id);
        try {
            encargadoRepository.deleteById(id);
            logger.info("Encargado con ID {} eliminado con éxito.", id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("No se puede eliminar el encargado con ID {}: está asignado a una sala.", id);
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.encargado-controller.delete.hasSala", null, locale));
        } catch (DataAccessException e) {
            logger.error("Error al eliminar el encargado con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.encargado-controller.delete.error", null, locale));
        }
        return "redirect:/encargados";
    }


    private void restoreSala(Encargado encargado) {
        if (encargado.getId() == null) {
            return;
        }
        try {
            encargadoRepository.findById(encargado.getId())
                    .ifPresent(actual -> encargado.setSala(actual.getSala()));
        } catch (DataAccessException e) {
            logger.warn("No se pudo recuperar la sala del encargado con ID {}: {}",
                    encargado.getId(), e.getMessage());
        }
    }
}