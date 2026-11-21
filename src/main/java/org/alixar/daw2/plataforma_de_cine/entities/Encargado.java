package org.alixar.daw2.plataforma_de_cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "encargado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Encargado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encargado")
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(max = 30, message = "El nombre no puede tener más de 30 caracteres")
    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @NotEmpty(message = "El primer apellido no puede estar vacío")
    @Size(max = 30, message = "El primer apellido no puede tener más de 30 caracteres")
    @Column(name = "primer_apellido", nullable = false, length = 30)
    private String primerApellido;

    @Size(max = 30, message = "El segundo apellido no puede tener más de 30 caracteres")
    @Column(name = "segundo_apellido", length = 30)
    private String segundoApellido;


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "encargado", fetch = FetchType.LAZY)
    private Sala sala;
}