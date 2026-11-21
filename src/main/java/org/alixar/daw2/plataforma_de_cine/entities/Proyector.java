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
@Table(name = "proyector")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyector")
    private Long id;

    @NotEmpty(message = "{msg.proyector.modelo.notEmpty}")
    @Size(max = 30, message = "{msg.proyector.modelo.size}")
    @Column(name = "modelo", nullable = false, length = 30)
    private String modelo;

    @NotEmpty(message = "{msg.proyector.marca.notEmpty}")
    @Size(max = 30, message = "{msg.proyector.marca.size}")
    @Column(name = "marca", nullable = false, length = 30)
    private String marca;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "proyector", fetch = FetchType.LAZY)
    private Sala sala;
}