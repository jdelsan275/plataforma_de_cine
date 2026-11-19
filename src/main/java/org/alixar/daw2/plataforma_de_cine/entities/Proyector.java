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
 * Cada proyector pertenece a una única sala (relación uno a uno que gestiona la entidad {@code Sala},
 * que es la propietaria de la clave foránea {@code id_proyector}).
 */
@Entity
@Table(name = "proyector")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyector")
    private Long id;

    @NotEmpty(message = "El modelo no puede estar vacío")
    @Size(max = 30, message = "El modelo no puede tener más de 30 caracteres")
    @Column(name = "modelo", nullable = false, length = 30)
    private String modelo;

    @NotEmpty(message = "La marca no puede estar vacía")
    @Size(max = 30, message = "La marca no puede tener más de 30 caracteres")
    @Column(name = "marca", nullable = false, length = 30)
    private String marca;

//comentado hasta que se termine la entidad sala
    //@ToString.Exclude
    //@EqualsAndHashCode.Exclude
    //@OneToOne(mappedBy = "proyector", fetch = FetchType.LAZY)
    //private Sala sala;
}