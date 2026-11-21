package org.alixar.daw2.plataforma_de_cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * La clase Actor representa a un actor en la plataforma de cine.
 * Contiene información sobre el nombre, apellidos, fecha de nacimiento y nacionalidad del actor.
 * Además, mantiene una relación ManyToMany con la entidad Pelicula, representando las películas en las que ha participado.
 */
@Entity
@Table(name = "actor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actor")
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(max = 20, message = "El nombre no puede tener más de 20 caracteres")
    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    @NotEmpty(message = "El primer apellido no puede estar vacío")
    @Size(max = 50, message = "El primer apellido no puede tener más de 50 caracteres")
    @Column(name = "primerApellido", nullable = false, length = 50)
    private String primerApellido;

    @Size(max = 50, message = "El segundo apellido no puede tener más de 50 caracteres")
    @Column(name = "segundoApellido", length = 50)
    private String segundoApellido;

    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @NotEmpty(message = "La nacionalidad no puede estar vacía")
    @Size(max = 30, message = "La nacionalidad no puede tener más de 30 caracteres")
    @Column(name = "nacionalidad", nullable = false, length = 30)
    private String nacionalidad;

    /**
     * Relación ManyToMany con la entidad Pelicula.
     * Representa las películas en las que ha participado el actor.
     */
    @ManyToMany(mappedBy = "actores", fetch = FetchType.LAZY)
    private List<Pelicula> peliculas;
}
