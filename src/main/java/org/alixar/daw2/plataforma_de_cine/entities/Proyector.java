
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
 * Entidad que representa un proyector del cine.
 * Contiene sus datos y la sala a la que esta asignado.
 */
@Entity
@Table(name = "proyector")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyector {

    /** Identificador del proyector. Se genera automaticamente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyector")
    private Long id;


    /** Modelo del proyector. Es obligatorio y tiene un maximo de 30 caracteres. */
    @NotEmpty(message = "{msg.proyector.modelo.notEmpty}")
    @Size(max = 30, message = "{msg.proyector.modelo.size}")
    @Column(name = "modelo", nullable = false, length = 30)
    private String modelo;


    /** Marca del proyector. Es obligatoria y tiene un maximo de 30 caracteres. */
    @NotEmpty(message = "{msg.proyector.marca.notEmpty}")
    @Size(max = 30, message = "{msg.proyector.marca.size}")
    @Column(name = "marca", nullable = false, length = 30)
    private String marca;


    /** Sala relacionada con el proyector mediante una relacion uno a uno. */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "proyector", fetch = FetchType.LAZY)
    private Sala sala;
}
