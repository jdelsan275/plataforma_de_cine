package org.alixar.daw2.plataforma_de_cine.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.Date;
@Entity
@Table(name = "sala")
@AllArgsConstructor
@NoArgsConstructor
@Data


public class Sala {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero_sala;

    private int capacidad;

    private boolean tiene_3D;

    private Long id_encargado;

    private Long id_proyector;


}
