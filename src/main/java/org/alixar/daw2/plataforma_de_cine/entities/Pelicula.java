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
 * La clase Pelicula representa una película en la plataforma de cine.
 * Contiene información sobre el título, sinopsis, fecha de estreno y si es para mayores de edad.
 * Además, mantiene una relación ManyToMany con la entidad Actor, representando los actores que participan en la película.
 */
@Entity
@Table(name = "pelicula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Long id;

    @NotEmpty(message = "{msg.pelicula.titulo.notEmpty}")
    @Size(max = 30, message = "{msg.pelicula.titulo.size}")
    @Column(name = "titulo", nullable = false, length = 30)
    private String titulo;

    @NotEmpty(message = "{msg.pelicula.sinopsis.notEmpty}")
    @Size(max = 300, message = "{msg.pelicula.sinopsis.size}")
    @Column(name = "sinopsis", nullable = false, length = 300)
    private String sinopsis;

    @NotNull(message = "{msg.pelicula.fechaEstreno.notNull}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_estreno", nullable = false)
    private LocalDate fechaEstreno;

    @Column(name = "mayor_edad", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean mayorEdad;

    @Column(name = "imagen")
    private String imagen;

    /**
     * Relación ManyToMany con la entidad Actor.
     * Representa los actores que participan en la película.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pelicula_actor",
            joinColumns = @JoinColumn(name = "id_pelicula"),
            inverseJoinColumns = @JoinColumn(name = "id_actor")
    )
    private List<Actor> actores;
}
