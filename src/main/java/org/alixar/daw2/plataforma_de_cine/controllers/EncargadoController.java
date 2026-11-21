
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

/**
 * Controller encargado de gestionar los encargados de las salas de cine.
 * Permite listar, crear, editar y eliminar encargados.
 */
@Controller
@RequestMapping("/encargados")
public class EncargadoController {

    /** Logger para mostrar informacion sobre las operaciones realizadas. */
    private static final Logger logger = LoggerFactory.getLogger(EncargadoController.class);

    /** Repository utilizado para acceder a los encargados de la base de datos. */
    private final EncargadoRepository encargadoRepository;

    /** Permite obtener los mensajes traducidos segun el idioma. */
    private final MessageSource messageSource;

    /**
     * Constructor del controller.
     *
     * @param encargadoRepository repository para gestionar los encargados
     * @param messageSource componente utilizado para obtener los mensajes
     */
    public EncargadoController(EncargadoRepository encargadoRepository, MessageSource messageSource) {
        this.encargadoRepository = encargadoRepository;
        this.messageSource = messageSource;
    }


    /**
     * Muestra el listado de todos los encargados.
     *
     * @param model modelo donde se guardan los datos que se envian a la vista
     * @param locale idioma utilizado para mostrar los mensajes
     * @return nombre de la vista encargados
     */
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


    /**
     * Muestra el formulario para crear un encargado nuevo.
     *
     * @param model modelo donde se guarda el encargado vacio
     * @return nombre de la vista del formulario
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para un nuevo encargado.");
        model.addAttribute("encargado", new Encargado());
        return "encargado-form";
    }


    /**
     * Busca un encargado por su id y muestra sus datos en el formulario de edicion.
     *
     * @param id identificador del encargado
     * @param model modelo donde se guardan los datos del encargado
     * @param redirectAttributes atributos que se envian al redireccionar
     * @param locale idioma utilizado para obtener los mensajes
     * @return formulario de edicion o redireccion al listado
     */
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


    /**
     * Guarda un encargado nuevo en la base de datos.
     *
     * @param encargado encargado que se va a guardar
     * @param result resultado de las validaciones del formulario
     * @param redirectAttributes atributos para enviar mensajes al redireccionar
     * @param locale idioma utilizado para obtener los mensajes
     * @return formulario si hay errores o redireccion al listado
     */
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


    /**
     * Actualiza los datos de un encargado existente.
     *
     * @param encargado encargado con los datos actualizados
     * @param result resultado de las validaciones del formulario
     * @param redirectAttributes atributos para enviar mensajes al redireccionar
     * @param locale idioma utilizado para obtener los mensajes
     * @return formulario si hay errores o redireccion al listado
     */
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


    /**
     * Elimina un encargado utilizando su id.
     *
     * @param id identificador del encargado que se va a eliminar
     * @param redirectAttributes atributos para enviar mensajes al redireccionar
     * @param locale idioma utilizado para obtener los mensajes
     * @return redireccion al listado de encargados
     */
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


    /**
     * Recupera la sala que tenia asignada el encargado.
     * Se utiliza cuando el formulario devuelve errores de validacion.
     *
     * @param encargado encargado cuya sala se quiere recuperar
     */
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
