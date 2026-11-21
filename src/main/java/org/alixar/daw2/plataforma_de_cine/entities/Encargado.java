
package org.alixar.daw2.plataforma_de_cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad que representa a un encargado del cine.
 * Contiene sus datos personales y la sala que tiene asignada.
 */
@Entity
@Table(name = "encargado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Encargado {

    /** Identificador del encargado. Se genera automaticamente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encargado")
    private Long id;


    /** Nombre del encargado. Es obligatorio y tiene un maximo de 30 caracteres. */
    @NotEmpty(message = "{msg.encargado.nombre.notEmpty}")
    @Size(max = 30, message = "{msg.encargado.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;


    /** Primer apellido del encargado. Es obligatorio y tiene un maximo de 30 caracteres. */
    @NotEmpty(message = "{msg.encargado.primerApellido.notEmpty}")
    @Size(max = 30, message = "{msg.encargado.primerApellido.size}")
    @Column(name = "primer_apellido", nullable = false, length = 30)
    private String primerApellido;


    /** Segundo apellido del encargado. Es opcional y tiene un maximo de 30 caracteres. */
    @Size(max = 30, message = "{msg.encargado.segundoApellido.size}")
    @Column(name = "segundo_apellido", length = 30)
    private String segundoApellido;


    /** Sala relacionada con el encargado mediante una relacion uno a uno. */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "encargado", fetch = FetchType.LAZY)
    private Sala sala;
}
