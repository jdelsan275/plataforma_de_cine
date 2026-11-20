package org.alixar.daw2.plataforma_de_cine.repositories;

import org.alixar.daw2.plataforma_de_cine.entities.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    // Comprueba si ya existe una sala con ese número
    boolean existsByNumeroSala(int numeroSala);

    // Comprueba si otra sala distinta ya usa ese número
    boolean existsByNumeroSalaAndIdNot(int numeroSala, Long id);
}
