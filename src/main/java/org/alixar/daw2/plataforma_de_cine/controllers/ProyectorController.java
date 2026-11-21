
package org.alixar.daw2.plataforma_de_cine.controllers;

import jakarta.validation.Valid;
import org.alixar.daw2.plataforma_de_cine.entities.Proyector;
import org.alixar.daw2.plataforma_de_cine.repositories.ProyectorRepository;
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

/**
 * Controller encargado de gestionar los proyectores del cine.
 * Permite listar, crear, editar y eliminar proyectores.
 */
@Controller
@RequestMapping("/proyectores")
public class ProyectorController {

    /** Logger para mostrar informacion sobre las operaciones realizadas. */
    private static final Logger logger = LoggerFactory.getLogger(ProyectorController.class);

    /** Repository utilizado para acceder a los proyectores de la base de datos. */
    private final ProyectorRepository proyectorRepository;

    /** Permite obtener los mensajes traducidos segun el idioma. */
    private final MessageSource messageSource;


    /**
     * Constructor del controller.
     *
     * @param proyectorRepository repository para gestionar los proyectores
     * @param messageSource componente utilizado para obtener los mensajes
     */
    public ProyectorController(ProyectorRepository proyectorRepository, MessageSource messageSource) {
        this.proyectorRepository = proyectorRepository;
        this.messageSource = messageSource;
    }


    /**
     * Muestra el listado de todos los proyectores.
     *
     * @param model modelo donde se guardan los datos que se envian a la vista
     * @param locale idioma utilizado para mostrar los mensajes
     * @return nombre de la vista proyectores
     */
    @GetMapping
    public String listProyectores(Model model, Locale locale) {
        logger.info("Solicitando la lista de todos los proyectores...");
        List<Proyector> listProyectores = null;
        try {
            listProyectores = proyectorRepository.findAll();
            logger.info("Se han cargado {} proyectores.", listProyectores.size());
        } catch (DataAccessException e) {
            logger.error("Error al listar los proyectores: {}", e.getMessage());
            model.addAttribute("errorMessage",
                    messageSource.getMessage("msg.proyector-controller.list.error", null, locale));
        }
        model.addAttribute("listProyectores", listProyectores);
        return "proyectores";
    }


    /**
     * Muestra el formulario para crear un proyector nuevo.
     *
     * @param model modelo donde se guarda el proyector vacio
     * @return nombre de la vista del formulario
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para un nuevo proyector.");
        model.addAttribute("proyector", new Proyector());
        return "proyector-form";
    }


    /**
     * Busca un proyector por su id y muestra sus datos en el formulario de edicion.
     *
     * @param id identificador del proyector
     * @param model modelo donde se guardan los datos del proyector
     * @param redirectAttributes atributos que se envian al redireccionar
     * @param locale idioma utilizado para obtener los mensajes
     * @return formulario de edicion o redireccion al listado
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario de edición para el proyector con ID {}", id);
        try {
            Optional<Proyector> proyector = proyectorRepository.findById(id);
            if (proyector.isEmpty()) {
                logger.warn("No se encontró el proyector con ID {}", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.proyector-controller.edit.notFound", null, locale));
                return "redirect:/proyectores";
            }
            model.addAttribute("proyector", proyector.get());
        } catch (DataAccessException e) {
            logger.error("Error al obtener el proyector con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.proyector-controller.edit.error", null, locale));
            return "redirect:/proyectores";
        }
        return "proyector-form";
    }


    /**
     * Guarda un proyector nuevo en la base de datos.
     *
     * @param proyector proyector que se va a guardar
     * @param result resultado de las validaciones del formulario
     * @param redirectAttributes atributos para enviar mensajes al redireccionar
     * @param locale idioma utilizado para obtener los mensajes
     * @return formulario si hay errores o redireccion al listado
     */
    @PostMapping("/insert")
    public String insertProyector(@Valid @ModelAttribute("proyector") Proyector proyector, BindingResult result,
                                  RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nuevo proyector {}", proyector.getModelo());
        if (result.hasErrors()) {
            return "proyector-form";
        }
        try {
            proyectorRepository.save(proyector);
            logger.info("Proyector {} insertado con éxito.", proyector.getModelo());
        } catch (DataAccessException e) {
            logger.error("Error al insertar el proyector {}: {}", proyector.getModelo(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.proyector-controller.insert.error", null, locale));
        }
        return "redirect:/proyectores";
    }


    /**
     * Actualiza los datos de un proyector existente.
     *
     * @param proyector proyector con los datos actualizados
     * @param result resultado de las validaciones del formulario
     * @param redirectAttributes atributos para enviar mensajes al redireccionar
     * @param locale idioma utilizado para obtener los mensajes
     * @return formulario si hay errores o redireccion al listado
     */
    @PostMapping("/update")
    public String updateProyector(@Valid @ModelAttribute("proyector") Proyector proyector, BindingResult result,
                                  RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Actualizando proyector con ID {}", proyector.getId());
        if (result.hasErrors()) {
            restoreSala(proyector);
            return "proyector-form";
        }
        try {
            proyectorRepository.save(proyector);
            logger.info("Proyector con ID {} actualizado con éxito.", proyector.getId());
        } catch (DataAccessException e) {
            logger.error("Error al actualizar el proyector con ID {}: {}", proyector.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.proyector-controller.update.error", null, locale));
        }
        return "redirect:/proyectores";
    }


    /**
     * Elimina un proyector utilizando su id.
     *
     * @param id identificador del proyector que se va a eliminar
     * @param redirectAttributes atributos para enviar mensajes al redireccionar
     * @param locale idioma utilizado para obtener los mensajes
     * @return redireccion al listado de proyectores
     */
    @PostMapping("/delete")
    public String deleteProyector(@RequestParam("id") Long id, RedirectAttributes redirectAttributes,
                                  Locale locale) {
        logger.info("Eliminando proyector con ID {}", id);
        try {
            proyectorRepository.deleteById(id);
            logger.info("Proyector con ID {} eliminado con éxito.", id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("No se puede eliminar el proyector con ID {}: está asignado a una sala.", id);
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.proyector-controller.delete.hasSala", null, locale));
        } catch (DataAccessException e) {
            logger.error("Error al eliminar el proyector con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.proyector-controller.delete.error", null, locale));
        }
        return "redirect:/proyectores";
    }


    /**
     * Recupera la sala que tenia asignada el proyector.
     * Se utiliza cuando el formulario devuelve errores de validacion.
     *
     * @param proyector proyector cuya sala se quiere recuperar
     */
    private void restoreSala(Proyector proyector) {
        if (proyector.getId() == null) {
            return;
        }
        try {
            proyectorRepository.findById(proyector.getId())
                    .ifPresent(actual -> proyector.setSala(actual.getSala()));
        } catch (DataAccessException e) {
            logger.warn("No se pudo recuperar la sala del proyector con ID {}: {}",
                    proyector.getId(), e.getMessage());
        }
    }
}
