package org.alixar.daw2.plataforma_de_cine.controllers;

import jakarta.validation.Valid;
import org.alixar.daw2.plataforma_de_cine.entities.Pelicula;
import org.alixar.daw2.plataforma_de_cine.entities.Sala;
import org.alixar.daw2.plataforma_de_cine.entities.Sesion;
import org.alixar.daw2.plataforma_de_cine.repositories.PeliculaRepository;
import org.alixar.daw2.plataforma_de_cine.repositories.SalaRepository;
import org.alixar.daw2.plataforma_de_cine.repositories.SesionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/sesiones")
public class SesionController {

    private static final Logger logger = LoggerFactory.getLogger(SesionController.class);

    private final SesionRepository sesionRepository;
    private final SalaRepository salaRepository;
    private final PeliculaRepository peliculaRepository;
    private final MessageSource messageSource;

    public SesionController(SesionRepository sesionRepository, SalaRepository salaRepository,
                            PeliculaRepository peliculaRepository, MessageSource messageSource) {
        this.sesionRepository = sesionRepository;
        this.salaRepository = salaRepository;
        this.peliculaRepository = peliculaRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String listSesiones(Model model, Locale locale) {
        logger.info("Solicitando la lista de todas las sesiones...");
        List<Sesion> listSesiones = null;
        try {
            listSesiones = sesionRepository.findAll();
            logger.info("Se han cargado {} sesiones.", listSesiones.size());
        } catch (DataAccessException e) {
            logger.error("Error al listar las sesiones: {}", e.getMessage());
            model.addAttribute("errorMessage",
                    messageSource.getMessage("msg.sesion-controller.list.error", null, locale));
        }
        model.addAttribute("listSesiones", listSesiones);
        return "sesiones";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para una nueva sesión.");
        Sesion sesion = new Sesion();
        sesion.setSala(new Sala());
        sesion.setPelicula(new Pelicula());
        model.addAttribute("sesion", sesion);
        addOptions(model);
        return "sesion-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario de edición para la sesión con ID {}", id);
        try {
            Optional<Sesion> sesion = sesionRepository.findById(id);
            if (sesion.isEmpty()) {
                logger.warn("No se encontró la sesión con ID {}", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.sesion-controller.edit.notFound", null, locale));
                return "redirect:/sesiones";
            }
            model.addAttribute("sesion", sesion.get());
            addOptions(model);
        } catch (DataAccessException e) {
            logger.error("Error al obtener la sesión con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sesion-controller.edit.error", null, locale));
            return "redirect:/sesiones";
        }
        return "sesion-form";
    }

    @PostMapping("/insert")
    public String insertSesion(@Valid @ModelAttribute("sesion") Sesion sesion, BindingResult result,
                               Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nueva sesión del {}", sesion.getFechaSesion());
        resolveRelations(sesion, result);
        if (result.hasErrors()) {
            addOptions(model);
            return "sesion-form";
        }
        try {
            sesionRepository.save(sesion);
            logger.info("Sesión insertada con éxito.");
        } catch (DataAccessException e) {
            logger.error("Error al insertar la sesión: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sesion-controller.insert.error", null, locale));
        }
        return "redirect:/sesiones";
    }

    @PostMapping("/update")
    public String updateSesion(@Valid @ModelAttribute("sesion") Sesion sesion, BindingResult result,
                               Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Actualizando sesión con ID {}", sesion.getId());
        resolveRelations(sesion, result);
        if (result.hasErrors()) {
            addOptions(model);
            return "sesion-form";
        }
        try {
            Optional<Sesion> actual = sesionRepository.findById(sesion.getId());
            if (actual.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.sesion-controller.edit.notFound", null, locale));
                return "redirect:/sesiones";
            }
            Sesion existing = actual.get();
            existing.setFechaSesion(sesion.getFechaSesion());
            existing.setHora(sesion.getHora());
            existing.setPrecio(sesion.getPrecio());
            existing.setSesionAM(sesion.isSesionAM());
            existing.setSala(sesion.getSala());
            existing.setPelicula(sesion.getPelicula());
            sesionRepository.save(existing);
            logger.info("Sesión con ID {} actualizada con éxito.", sesion.getId());
        } catch (DataAccessException e) {
            logger.error("Error al actualizar la sesión con ID {}: {}", sesion.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sesion-controller.update.error", null, locale));
        }
        return "redirect:/sesiones";
    }

    @PostMapping("/delete")
    public String deleteSesion(@RequestParam("id") Long id, RedirectAttributes redirectAttributes,
                               Locale locale) {
        logger.info("Eliminando sesión con ID {}", id);
        try {
            sesionRepository.deleteById(id);
            logger.info("Sesión con ID {} eliminada con éxito.", id);
        } catch (DataAccessException e) {
            logger.error("Error al eliminar la sesión con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sesion-controller.delete.error", null, locale));
        }
        return "redirect:/sesiones";
    }

    /**
     * Sustituye la sala y la película que llegan del formulario (solo traen el id)
     * por las entidades reales de la BD. Si falta alguna, añade el error al BindingResult.
     */
    private void resolveRelations(Sesion sesion, BindingResult result) {
        Long idSala = sesion.getSala() != null ? sesion.getSala().getId() : null;
        Optional<Sala> sala = idSala != null ? salaRepository.findById(idSala) : Optional.empty();
        if (sala.isPresent()) {
            sesion.setSala(sala.get());
        } else {
            result.rejectValue("sala", "msg.sesion-controller.sala.required");
        }

        Long idPelicula = sesion.getPelicula() != null ? sesion.getPelicula().getId() : null;
        Optional<Pelicula> pelicula = idPelicula != null
                ? peliculaRepository.findById(idPelicula) : Optional.empty();
        if (pelicula.isPresent()) {
            sesion.setPelicula(pelicula.get());
        } else {
            result.rejectValue("pelicula", "msg.sesion-controller.pelicula.required");
        }
    }

    private void addOptions(Model model) {
        model.addAttribute("salas", salaRepository.findAll());
        model.addAttribute("peliculas", peliculaRepository.findAll());
    }
}