package org.alixar.daw2.plataforma_de_cine.repositories;

import org.alixar.daw2.plataforma_de_cine.entities.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Long> {

    // Sesiones de una sala concreta
    List<Sesion> findBySalaId(Long idSala);

    // Sesiones de una película concreta
    List<Sesion> findByPeliculaId(Long idPelicula);

    // Sesiones de un día concreto
    List<Sesion> findByFechaSesion(LocalDate fechaSesion);
}
