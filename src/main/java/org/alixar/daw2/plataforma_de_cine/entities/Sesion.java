package org.alixar.daw2.plataforma_de_cine.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "sesion")
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date fecha_sesion;

    private Time hora;

    private int precio;

    private boolean sesion_AM;

    private Long id_pelicula;

    private Long id_sala;
}
