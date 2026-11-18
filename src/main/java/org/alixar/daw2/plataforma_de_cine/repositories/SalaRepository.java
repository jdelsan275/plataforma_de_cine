package org.alixar.daw2.plataforma_de_cine.repositories;

import org.alixar.daw2.plataforma_de_cine.entities.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaRepository extends JpaRepository<Sala, Long> {


    List<Sala> findAll();

    <S extends Sala> S save(S entity);
}
