package org.alixar.daw2.plataforma_de_cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sala")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Long id;

    @Min(value = 1, message = "{msg.sala.numero.min}")
    @Max(value = 99, message = "{msg.sala.numero.max}")
    @Column(name = "numero", nullable = false)
    private int numeroSala;

    @Min(value = 20, message = "{msg.sala.capacidad.min}")
    @Max(value = 100, message = "{msg.sala.capacidad.max}")
    @Column(name = "capacidad", nullable = false)
    private int capacidad;

    @Column(name = "tiene_3d", nullable = false)
    private boolean tiene3D;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_encargado", nullable = false, unique = true)
    private Encargado encargado;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_proyector", nullable = false, unique = true)
    private Proyector proyector;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "sala", fetch = FetchType.LAZY)
    private List<Sesion> sesiones = new ArrayList<>();
}
