package org.alixar.daw2.plataforma_de_cine.controllers;

import jakarta.validation.Valid;
import org.alixar.daw2.plataforma_de_cine.entities.Pelicula;
import org.alixar.daw2.plataforma_de_cine.repositories.ActorRepository;
import org.alixar.daw2.plataforma_de_cine.repositories.PeliculaRepository;
import org.alixar.daw2.plataforma_de_cine.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controlador que maneja las operaciones CRUD para la entidad `Pelicula`.
 * Utiliza `PeliculaRepository` para las películas y `ActorRepository` para rellenar el desplegable de actores.
 * Con `FileStorageService` se maneja la subida y almacenamiento de imágenes asociadas a las películas.
 * Con `MessageSource` se gestionan los mensajes internacionales para mostrar al usuario.
 */
@Controller
@RequestMapping("/peliculas")
public class PeliculaController {
    private static final Logger logger = LoggerFactory.getLogger(PeliculaController.class);

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Lista todas las películas registradas y las pasa al modelo para la vista.
     *
     * @param model Objeto del modelo para pasar datos a la vista.
     * @param locale Objeto de localización para mensajes internacionales.
     * @return Nombre de la plantilla Thymeleaf del listado de películas.
     */
    @GetMapping
    public String listPeliculas(Model model, Locale locale) {
        logger.info("Solicitando la lista de todas las películas...");
        List<Pelicula> listPeliculas = null;
        try {
            listPeliculas = peliculaRepository.findAll();
            logger.info("Se han cargado {} películas.", listPeliculas.size());
        } catch (DataAccessException e) {
            logger.error("Error al listar las películas: {}", e.getMessage());
            model.addAttribute("errorMessage", messageSource.getMessage("msg.pelicula-controller.list.error", null, locale));
        }
        model.addAttribute("listPeliculas", listPeliculas);
        return "peliculas";
    }

    /**
     * Muestra el formulario para crear una nueva película.
     *
     * @param model              Modelo para pasar datos a la vista.
     * @param redirectAttributes Atributos para mensajes flash en caso de error.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Nombre de la plantilla Thymeleaf del formulario o redirección al listado.
     */
    @GetMapping("/new")
    public String showNewForm(Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario para nueva película.");
        model.addAttribute("pelicula", new Pelicula());
        return loadActoresAndShowForm(model, redirectAttributes, locale);
    }

    /**
     * Muestra el formulario para editar una película existente.
     *
     * @param id                 ID de la película a editar.
     * @param model              Modelo para pasar datos a la vista.
     * @param redirectAttributes Atributos para mensajes flash en caso de error.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Nombre de la plantilla Thymeleaf del formulario o redirección al listado.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Mostrando formulario de edición para la película con ID {}", id);
        try {
            Optional<Pelicula> peliculaOpt = peliculaRepository.findById(id);
            if (peliculaOpt.isEmpty()) {
                logger.warn("No se encontró la película con ID {}", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.pelicula-controller.edit.notFound", null, locale));
                return "redirect:/peliculas";
            }
            model.addAttribute("pelicula", peliculaOpt.get());
        } catch (DataAccessException e) {
            logger.error("Error al obtener la película con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.pelicula-controller.edit.error", null, locale));
            return "redirect:/peliculas";
        }
        return loadActoresAndShowForm(model, redirectAttributes, locale);
    }

    /**
     * Inserta una nueva película en la base de datos.
     *
     * @param pelicula           Objeto que contiene los datos del formulario.
     * @param result             Resultado de las validaciones del formulario.
     * @param model              Modelo para recargar los actores si hay errores de validación.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Redirección al listado de películas o retorno al formulario si hay errores.
     */
    @PostMapping("/insert")
    public String insertPelicula(@Valid @ModelAttribute("pelicula") Pelicula pelicula, BindingResult result,
                                 @RequestParam("imageFile") MultipartFile imageFile, Model model,
                                 RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nueva película con título {}", pelicula.getTitulo());
        try {
            if (result.hasErrors()) {
                return loadActoresAndShowForm(model, redirectAttributes, locale);
            }
            if (peliculaRepository.existsByTituloIgnoreCase(pelicula.getTitulo())) {
                logger.warn("El título de la película {} ya existe.", pelicula.getTitulo());
                result.rejectValue("titulo", "msg.pelicula-controller.insert.tituloExist");
                return loadActoresAndShowForm(model, redirectAttributes, locale);
            }
            // Guardar la imagen subida si el usuario seleccionó un archivo
            if (!imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile);
                if (fileName != null) {
                    pelicula.setImagen(fileName);
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            messageSource.getMessage("msg.pelicula-controller.image.error", null, locale));
                    return "redirect:/peliculas/new";
                }
            }
            peliculaRepository.save(pelicula);
            logger.info("Película {} insertada con éxito.", pelicula.getTitulo());
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.pelicula-controller.insert.success", null, locale));
        } catch (DataAccessException e) {
            logger.error("Error al insertar la película {}: {}", pelicula.getTitulo(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.pelicula-controller.insert.error", null, locale));
        }
        return "redirect:/peliculas";
    }

    /**
     * Actualiza una película existente en la base de datos.
     *
     * @param pelicula           Objeto que contiene los datos del formulario.
     * @param result             Resultado de las validaciones del formulario.
     * @param model              Modelo para recargar los actores si hay errores de validación.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Redirección al listado de películas o retorno al formulario si hay errores.
     */
    @PostMapping("/update")
    public String updatePelicula(@Valid @ModelAttribute("pelicula") Pelicula pelicula, BindingResult result,
                                 @RequestParam("imageFile") MultipartFile imageFile, Model model,
                                 RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Actualizando película con ID {}", pelicula.getId());
        try {
            if (result.hasErrors()) {
                return loadActoresAndShowForm(model, redirectAttributes, locale);
            }
            if (peliculaRepository.existsByTituloIgnoreCaseAndIdNot(pelicula.getTitulo(), pelicula.getId())) {
                logger.warn("El título de la película {} ya existe en otra película.", pelicula.getTitulo());
                result.rejectValue("titulo", "msg.pelicula-controller.update.tituloExist");
                return loadActoresAndShowForm(model, redirectAttributes, locale);
            }
            // Verificar si la película existe antes de actualizarla
            Optional<Pelicula> peliculaOpt = peliculaRepository.findById(pelicula.getId());
            if (peliculaOpt.isEmpty()) {
                logger.warn("No se encontró la película con ID {}", pelicula.getId());
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.pelicula-controller.update.notFound", null, locale));
                return "redirect:/peliculas";
            }
            // Actualizar los campos de la película existente con los nuevos valores
            Pelicula existingPelicula = peliculaOpt.get();
            existingPelicula.setTitulo(pelicula.getTitulo());
            existingPelicula.setSinopsis(pelicula.getSinopsis());
            existingPelicula.setFechaEstreno(pelicula.getFechaEstreno());
            existingPelicula.setMayorEdad(pelicula.isMayorEdad());
            existingPelicula.setActores(pelicula.getActores());
            // Solo si se sube una imagen nueva se sustituye la anterior; si no, se conserva
            String oldImage = existingPelicula.getImagen();
            boolean imageReplaced = false;
            if (!imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile);
                if (fileName != null) {
                    existingPelicula.setImagen(fileName);
                    imageReplaced = true;
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            messageSource.getMessage("msg.pelicula-controller.image.error", null, locale));
                    return "redirect:/peliculas/edit?id=" + pelicula.getId();
                }
            }
            peliculaRepository.save(existingPelicula);
            // Si se reemplazó la imagen, eliminar la anterior del sistema de archivos
            if (imageReplaced && oldImage != null && !oldImage.isEmpty()) {
                fileStorageService.deleteFile(oldImage);
            }
            logger.info("Película con ID {} actualizada con éxito.", pelicula.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.pelicula-controller.update.success", null, locale));
        } catch (DataAccessException e) {
            logger.error("Error al actualizar la película con ID {}: {}", pelicula.getId(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.pelicula-controller.update.error", null, locale));
        }
        return "redirect:/peliculas";
    }

    /**
     * Elimina una película de la base de datos.
     *
     * @param id                 ID de la película a eliminar.
     * @param redirectAttributes Atributos para mensajes flash de redirección.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Redirección al listado de películas.
     */
    @PostMapping("/delete")
    public String deletePelicula(@RequestParam("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Eliminando película con ID {}", id);
        try {
            Optional<Pelicula> peliculaOpt = peliculaRepository.findById(id);
            if (peliculaOpt.isPresent()) {
                String imagen = peliculaOpt.get().getImagen();
                peliculaRepository.deleteById(id);

                // Eliminar la imagen asociada a la película si existe
                if (imagen != null && !imagen.isEmpty()) {
                    fileStorageService.deleteFile(imagen);
                }
                logger.info("Película con ID {} eliminada con éxito.", id);
                redirectAttributes.addFlashAttribute("successMessage",
                        messageSource.getMessage("msg.pelicula-controller.delete.success", null, locale));
            } else {
                logger.warn("No se encontró la película con ID {}", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage("msg.pelicula-controller.delete.notFound", null, locale));
            }
        } catch (DataAccessException e) {
            logger.error("Error al eliminar la película con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.pelicula-controller.delete.error", null, locale));
        }
        return "redirect:/peliculas";
    }

    /**
     * Carga el listado de actores disponibles para seleccionar en el formulario y devuelve su vista.
     * Si ocurre un error al consultar los actores, redirige al listado con un mensaje de error.
     *
     * @param model              Modelo para pasar la lista de actores a la vista.
     * @param redirectAttributes Atributos para mensajes flash en caso de fallo.
     * @param locale             Objeto de localización para mensajes internacionales.
     * @return Nombre de la plantilla del formulario o redirección al listado.
     */
    private String loadActoresAndShowForm(Model model, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            model.addAttribute("listActores", actorRepository.findAll());
            return "pelicula-form";
        } catch (DataAccessException e) {
            logger.error("Error al cargar los actores para el formulario: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("msg.pelicula-controller.actores.error", null, locale));
            return "redirect:/peliculas";
        }
    }
}
