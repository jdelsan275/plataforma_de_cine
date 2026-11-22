package org.alixar.daw2.plataforma_de_cine.controllers;

import jakarta.validation.Valid;
import org.alixar.daw2.plataforma_de_cine.entities.Actor;
import org.alixar.daw2.plataforma_de_cine.entities.Pelicula;
import org.alixar.daw2.plataforma_de_cine.repositories.ActorRepository;
import org.alixar.daw2.plataforma_de_cine.repositories.PeliculaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Controlador que maneja las operaciones CRUD para la entidad `Actor`.
 * Utiliza `ActorRepository` para los actores y `PeliculaRepository` para rellenar el desplegable de películas.
 * Con `MessageSource` se gestionan los mensajes internacionales para mostrar al usuario.
 */
@Controller
@RequestMapping("/actores")
public class ActorController {
    private static final Logger logger = LoggerFactory.getLogger(ActorController.class);

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Lista todos los actores registrados y los pasa al modelo para la vista.
     *
     * @param model Objeto del modelo para pasar datos a la vista.
     * @param locale Objeto de localización para mensajes internacionales.
     * @return Nombre de la plantilla Thymeleaf del listado de actores.
     */
    @GetMapping
    public String listActores(Model model, Locale locale) {
        logger.info("Solicitando la lista de todos los actores...");
        List<Actor> listActores = null;
        try {
            listActores = actorRepository.findAll();
            logger.info("Se han cargado {} actores.", listActores.size());
        } catch (DataAccessException e) {
            logger.error("Error al listar los actores: {}", e.getMessage());
            model.addAttribute("errorMessage",
                    messageSource.getMessage("msg.actor-controller.list.error", null, locale));
        }
        model.addAttribute("listActores", listActores);
        return "actores";
    }

    /**
     * Muestra el formulario para crear un nuevo actor junto con el listado de películas disponibles.
     *
     * @param model              Modelo para pasar datos a la vista.
     * @param redirectAttributes Atributos para mensajes flash en caso de error.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Nombre de la plantilla Thymeleaf del formulario o redirección al listado.
     */
    @GetMapping("/new")
    public String showNewForm(Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario para nuevo actor.");
        model.addAttribute("actor", new Actor());
        return loadPeliculasAndShowForm(model, redirectAttributes, locale);
    }

    /**
     * Muestra el formulario para editar un actor existente junto con el listado de películas disponibles.
     *
     * @param id                 ID del actor a editar.
     * @param model              Modelo para pasar datos a la vista.
     * @param redirectAttributes Atributos para mensajes flash en caso de error.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Nombre de la plantilla Thymeleaf del formulario o redirección al listado.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario de edición para el actor con ID {}", id);
        try {
            Optional<Actor> actorOpt = actorRepository.findById(id);
            if (actorOpt.isEmpty()) {
                logger.warn("No se encontró el actor con ID {}", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.actor-controller.edit.notFound", null, locale));
                return "redirect:/actores";
            }
            model.addAttribute("actor", actorOpt.get());
        } catch (DataAccessException e) {
            logger.error("Error al obtener el actor con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.actor-controller.edit.error", null, locale));
            return "redirect:/actores";
        }
        return loadPeliculasAndShowForm(model, redirectAttributes, locale);
    }

    /**
     * Inserta un nuevo actor en la base de datos y asocia las películas seleccionadas en el formulario.
     *
     * @param actor              Objeto que contiene los datos del formulario.
     * @param result             Resultado de las validaciones del formulario.
     * @param model              Modelo para recargar las películas si hay errores de validación.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Redirección al listado de actores o retorno al formulario si hay errores.
     */
    @PostMapping("/insert")
    public String insertActor(@Valid @ModelAttribute("actor") Actor actor, BindingResult result,
                              Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nuevo actor {} {}", actor.getNombre(), actor.getPrimerApellido());
        try {
            if (result.hasErrors()) {
                return loadPeliculasAndShowForm(model, redirectAttributes, locale);
            }
            List<Long> peliculasSeleccionadasIds = obtenerIdsPeliculasSeleccionadas(actor);
            Actor actorGuardado = actorRepository.save(actor);
            sincronizarPeliculasDelActor(actorGuardado, peliculasSeleccionadasIds);

            logger.info("Actor {} {} insertado con éxito.", actor.getNombre(), actor.getPrimerApellido());
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.actor-controller.insert.success", null, locale));
        } catch (DataAccessException e) {
            logger.error("Error al insertar el actor: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.actor-controller.insert.error", null, locale));
        }
        return "redirect:/actores";
    }

    /**
     * Actualiza un actor existente en la base de datos y sincroniza sus películas asociadas.
     *
     * @param actor              Objeto que contiene los datos del formulario.
     * @param result             Resultado de las validaciones del formulario.
     * @param model              Modelo para recargar las películas si hay errores de validación.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Redirección al listado de actores o retorno al formulario si hay errores.
     */
    @PostMapping("/update")
    public String updateActor(@Valid @ModelAttribute("actor") Actor actor, BindingResult result,
                              Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Actualizando actor con ID {}", actor.getId());
        try {
            if (result.hasErrors()) {
                return loadPeliculasAndShowForm(model, redirectAttributes, locale);
            }
            List<Long> peliculasSeleccionadasIds = obtenerIdsPeliculasSeleccionadas(actor);
            Actor actorActualizado = actorRepository.save(actor);
            sincronizarPeliculasDelActor(actorActualizado, peliculasSeleccionadasIds);

            logger.info("Actor con ID {} actualizado con éxito.", actor.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.actor-controller.update.success", null, locale));
        } catch (DataAccessException e) {
            logger.error("Error al actualizar el actor con ID {}: {}", actor.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.actor-controller.update.error", null, locale));
        }
        return "redirect:/actores";
    }

    /**
     * Elimina un actor de la base de datos, desvinculándolo previamente de sus películas asociadas.
     *
     * @param id                 ID del actor a eliminar.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Redirección al listado de actores.
     */
    @PostMapping("/delete")
    public String deleteActor(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Eliminando actor con ID {}", id);
        try {
            Optional<Actor> actorOpt = actorRepository.findById(id);
            if (actorOpt.isPresent()) {
                Actor actor = actorOpt.get();
                for (Pelicula pelicula : actor.getPeliculas()) {
                    pelicula.getActores().remove(actor);
                    peliculaRepository.save(pelicula);
                }
                actorRepository.deleteById(id);
                logger.info("Actor con ID {} eliminado con éxito.", id);
                redirectAttributes.addFlashAttribute("successMessage",
                        messageSource.getMessage("msg.actor-controller.delete.success", null, locale));
            } else {
                logger.warn("No se encontró el actor con ID {} para eliminar.", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.actor-controller.delete.notFound", null, locale));
            }
        } catch (DataAccessException e) {
            logger.error("Error al eliminar el actor con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.actor-controller.delete.error", null, locale));
        }
        return "redirect:/actores";
    }

    /**
     * Carga el listado de películas disponibles para seleccionar en el formulario de actor y devuelve la vista.
     * Si no se pueden cargar las películas, redirige al listado con un mensaje de error.
     *
     * @param model              Modelo para pasar la lista de películas a la vista.
     * @param redirectAttributes Atributos para mensajes flash en caso de fallo.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Nombre de la plantilla del formulario o redirección al listado.
     */
    private String loadPeliculasAndShowForm(Model model, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            model.addAttribute("listPeliculas", peliculaRepository.findAll());
            return "actor-form";
        } catch (DataAccessException e) {
            logger.error("Error al cargar las películas para el formulario: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.actor-controller.peliculas.error", null, locale));
            return "redirect:/actores";
        }
    }

    /**
     * Extrae los identificadores de las películas marcadas en el formulario para el actor.
     *
     * @param actor Actor recibido desde el formulario.
     * @return Lista inmutable con los IDs de las películas seleccionadas.
     */
    private List<Long> obtenerIdsPeliculasSeleccionadas(Actor actor) {
        return actor.getPeliculas() != null
                ? actor.getPeliculas().stream().map(Pelicula::getId).toList()
                : List.of();
    }

    /**
     * Sincroniza la relación ManyToMany desde la entidad propietaria `Pelicula`
     * para reflejar en la tabla intermedia `pelicula_actor` las películas seleccionadas.
     *
     * @param actor                     Actor persistido en base de datos.
     * @param peliculasSeleccionadasIds IDs de las películas que deben quedar asociadas al actor.
     */
    private void sincronizarPeliculasDelActor(Actor actor, List<Long> peliculasSeleccionadasIds) {
        List<Pelicula> todasLasPeliculas = peliculaRepository.findAll();
        for (Pelicula pelicula : todasLasPeliculas) {
            boolean debeEstar = peliculasSeleccionadasIds.contains(pelicula.getId());
            boolean yaEstaba = pelicula.getActores().stream().anyMatch(a -> a.getId().equals(actor.getId()));

            if (debeEstar && !yaEstaba) {
                pelicula.getActores().add(actor);
                peliculaRepository.save(pelicula);
            } else if (!debeEstar && yaEstaba) {
                pelicula.getActores().removeIf(a -> a.getId().equals(actor.getId()));
                peliculaRepository.save(pelicula);
            }
        }
    }
}
