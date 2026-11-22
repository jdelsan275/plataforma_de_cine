package org.alixar.daw2.plataforma_de_cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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

    @NotEmpty(message = "{msg.actor.nombre.notEmpty}")
    @Size(max = 20, message = "{msg.actor.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    @NotEmpty(message = "{msg.actor.primerApellido.notEmpty}")
    @Size(max = 50, message = "{msg.actor.primerApellido.size}")
    @Column(name = "primer_apellido", nullable = false, length = 50)
    private String primerApellido;

    @Size(max = 50, message = "{msg.actor.segundoApellido.size}")
    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @NotNull(message = "{msg.actor.fechaNacimiento.notNull}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @NotEmpty(message = "{msg.actor.nacionalidad.notEmpty}")
    @Size(max = 30, message = "{msg.actor.nacionalidad.size}")
    @Column(name = "nacionalidad", nullable = false, length = 30)
    private String nacionalidad;

    /**
     * Relación ManyToMany con la entidad Pelicula.
     * Representa las películas en las que ha participado el actor.
     */
    @ManyToMany(mappedBy = "actores", fetch = FetchType.LAZY)
    private List<Pelicula> peliculas;
}
