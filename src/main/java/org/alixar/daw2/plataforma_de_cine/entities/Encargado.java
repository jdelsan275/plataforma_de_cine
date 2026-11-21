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


    @NotEmpty(message = "{msg.encargado.nombre.notEmpty}")
    @Size(max = 30, message = "{msg.encargado.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;


    @NotEmpty(message = "{msg.encargado.primerApellido.notEmpty}")
    @Size(max = 30, message = "{msg.encargado.primerApellido.size}")
    @Column(name = "primer_apellido", nullable = false, length = 30)
    private String primerApellido;


    @Size(max = 30, message = "{msg.encargado.segundoApellido.size}")
    @Column(name = "segundo_apellido", length = 30)
    private String segundoApellido;


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "encargado", fetch = FetchType.LAZY)
    private Sala sala;
}