package org.alixar.daw2.plataforma_de_cine.repositories;

import org.alixar.daw2.plataforma_de_cine.entities.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * findByNacionalidadIgnoreCase: Busca actores por nacionalidad, ignorando mayúsculas y minúsculas.
 * findByNombreContainingIgnoreCaseOrPrimerApellidoContainingIgnoreCase: Busca actores cuyo nombre
 * o primer apellido contenga una cadena específica, ignorando mayúsculas y minúsculas.
 */
@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
    List<Actor> findByNacionalidadIgnoreCase(String nacionalidad);
    List<Actor> findByNombreContainingIgnoreCaseOrPrimerApellidoContainingIgnoreCase(String nombre, String primerApellido);
}
