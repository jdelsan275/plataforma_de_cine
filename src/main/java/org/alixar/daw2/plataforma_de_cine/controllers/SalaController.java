package org.alixar.daw2.plataforma_de_cine.controllers;

import jakarta.validation.Valid;
import org.alixar.daw2.plataforma_de_cine.entities.Encargado;
import org.alixar.daw2.plataforma_de_cine.entities.Proyector;
import org.alixar.daw2.plataforma_de_cine.entities.Sala;
import org.alixar.daw2.plataforma_de_cine.repositories.EncargadoRepository;
import org.alixar.daw2.plataforma_de_cine.repositories.ProyectorRepository;
import org.alixar.daw2.plataforma_de_cine.repositories.SalaRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/salas")
public class SalaController {

    private static final Logger logger = LoggerFactory.getLogger(SalaController.class);

    private final SalaRepository salaRepository;
    private final EncargadoRepository encargadoRepository;
    private final ProyectorRepository proyectorRepository;
    private final MessageSource messageSource;

    public SalaController(SalaRepository salaRepository, EncargadoRepository encargadoRepository,
                          ProyectorRepository proyectorRepository, MessageSource messageSource) {
        this.salaRepository = salaRepository;
        this.encargadoRepository = encargadoRepository;
        this.proyectorRepository = proyectorRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String listSalas(Model model, Locale locale) {
        logger.info("Solicitando la lista de todas las salas...");
        List<Sala> listSalas = null;
        try {
            listSalas = salaRepository.findAll();
            logger.info("Se han cargado {} salas.", listSalas.size());
        } catch (DataAccessException e) {
            logger.error("Error al listar las salas: {}", e.getMessage());
            model.addAttribute("errorMessage",
                    messageSource.getMessage("msg.sala-controller.list.error", null, locale));
        }
        model.addAttribute("listSalas", listSalas);
        return "salas";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para una nueva sala.");
        Sala sala = new Sala();
        sala.setEncargado(new Encargado());
        sala.setProyector(new Proyector());
        model.addAttribute("sala", sala);
        addOptions(model, sala);
        return "sala-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario de edición para la sala con ID {}", id);
        try {
            Optional<Sala> sala = salaRepository.findById(id);
            if (sala.isEmpty()) {
                logger.warn("No se encontró la sala con ID {}", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.sala-controller.edit.notFound", null, locale));
                return "redirect:/salas";
            }
            model.addAttribute("sala", sala.get());
            addOptions(model, sala.get());
        } catch (DataAccessException e) {
            logger.error("Error al obtener la sala con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sala-controller.edit.error", null, locale));
            return "redirect:/salas";
        }
        return "sala-form";
    }

    @PostMapping("/insert")
    public String insertSala(@Valid @ModelAttribute("sala") Sala sala, BindingResult result,
                             Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nueva sala número {}", sala.getNumeroSala());
        resolveAssignments(sala, result);
        if (!result.hasFieldErrors("numeroSala") && salaRepository.existsByNumeroSala(sala.getNumeroSala())) {
            result.rejectValue("numeroSala", "msg.sala-controller.numero.exists");
        }
        if (result.hasErrors()) {
            addOptions(model, sala);
            return "sala-form";
        }
        try {
            salaRepository.save(sala);
            logger.info("Sala {} insertada con éxito.", sala.getNumeroSala());
        } catch (DataIntegrityViolationException e) {
            logger.warn("Encargado o proyector ya asignado a otra sala: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sala-controller.save.conflict", null, locale));
        } catch (DataAccessException e) {
            logger.error("Error al insertar la sala {}: {}", sala.getNumeroSala(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sala-controller.insert.error", null, locale));
        }
        return "redirect:/salas";
    }

    @PostMapping("/update")
    public String updateSala(@Valid @ModelAttribute("sala") Sala sala, BindingResult result,
                             Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Actualizando sala con ID {}", sala.getId());
        resolveAssignments(sala, result);
        if (!result.hasFieldErrors("numeroSala")
                && salaRepository.existsByNumeroSalaAndIdNot(sala.getNumeroSala(), sala.getId())) {
            result.rejectValue("numeroSala", "msg.sala-controller.numero.exists");
        }
        if (result.hasErrors()) {
            addOptions(model, sala);
            return "sala-form";
        }
        try {
            Optional<Sala> actual = salaRepository.findById(sala.getId());
            if (actual.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.sala-controller.edit.notFound", null, locale));
                return "redirect:/salas";
            }
            // Se actualiza la sala cargada de la BD para no perder sus sesiones
            Sala existing = actual.get();
            existing.setNumeroSala(sala.getNumeroSala());
            existing.setCapacidad(sala.getCapacidad());
            existing.setTiene3D(sala.isTiene3D());
            existing.setEncargado(sala.getEncargado());
            existing.setProyector(sala.getProyector());
            salaRepository.save(existing);
            logger.info("Sala con ID {} actualizada con éxito.", sala.getId());
        } catch (DataIntegrityViolationException e) {
            logger.warn("Encargado o proyector ya asignado a otra sala: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sala-controller.save.conflict", null, locale));
        } catch (DataAccessException e) {
            logger.error("Error al actualizar la sala con ID {}: {}", sala.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sala-controller.update.error", null, locale));
        }
        return "redirect:/salas";
    }

    @PostMapping("/delete")
    public String deleteSala(@RequestParam("id") Long id, RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Eliminando sala con ID {}", id);
        try {
            salaRepository.deleteById(id);
            logger.info("Sala con ID {} eliminada con éxito.", id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("No se puede eliminar la sala con ID {}: tiene sesiones asociadas.", id);
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sala-controller.delete.hasSesiones", null, locale));
        } catch (DataAccessException e) {
            logger.error("Error al eliminar la sala con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.sala-controller.delete.error", null, locale));
        }
        return "redirect:/salas";
    }

    /**
     * Sustituye el encargado y el proyector que llegan del formulario (solo traen el id)
     * por las entidades reales de la BD. Si falta alguno, añade el error al BindingResult.
     */
    private void resolveAssignments(Sala sala, BindingResult result) {
        Long idEncargado = sala.getEncargado() != null ? sala.getEncargado().getId() : null;
        Optional<Encargado> encargado = idEncargado != null
                ? encargadoRepository.findById(idEncargado) : Optional.empty();
        if (encargado.isPresent()) {
            sala.setEncargado(encargado.get());
        } else {
            result.rejectValue("encargado", "msg.sala-controller.encargado.required");
        }

        Long idProyector = sala.getProyector() != null ? sala.getProyector().getId() : null;
        Optional<Proyector> proyector = idProyector != null
                ? proyectorRepository.findById(idProyector) : Optional.empty();
        if (proyector.isPresent()) {
            sala.setProyector(proyector.get());
        } else {
            result.rejectValue("proyector", "msg.sala-controller.proyector.required");
        }
    }

    /**
     * Carga en el modelo los encargados y proyectores libres. Al editar, añade también
     * los que ya tiene la sala, porque esos ya no figuran como libres.
     */
    private void addOptions(Model model, Sala sala) {
        List<Encargado> encargados = new ArrayList<>(encargadoRepository.findBySalaIsNull());
        List<Proyector> proyectores = new ArrayList<>(proyectorRepository.findBySalaIsNull());

        if (sala.getId() != null) {
            salaRepository.findById(sala.getId()).ifPresent(actual -> {
                Encargado e = actual.getEncargado();
                if (e != null && encargados.stream().noneMatch(x -> x.getId().equals(e.getId()))) {
                    encargados.add(e);
                }
                Proyector p = actual.getProyector();
                if (p != null && proyectores.stream().noneMatch(x -> x.getId().equals(p.getId()))) {
                    proyectores.add(p);
                }
            });
        }
        model.addAttribute("encargados", encargados);
        model.addAttribute("proyectores", proyectores);
    }
}
