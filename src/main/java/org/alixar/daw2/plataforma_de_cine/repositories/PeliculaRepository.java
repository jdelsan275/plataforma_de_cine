package org.alixar.daw2.plataforma_de_cine.repositories;

import org.alixar.daw2.plataforma_de_cine.entities.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * findByTituloContainingIgnoreCase: Busca películas cuyo título contenga una cadena específica, ignorando mayúsculas y minúsculas.
 * findByMayorEdad: Busca películas según si son para mayores de edad o no.
 * findPeliculasPorActor: Busca películas asociadas a un actor específico por su ID.
 */
@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
    List<Pelicula> findByMayorEdad(boolean mayorEdad);
    @Query("SELECT p FROM Pelicula p JOIN p.actores a WHERE a.id = :idActor ORDER BY p.titulo ASC")
    List<Pelicula> findPeliculasPorActor(@Param("idActor") Long idActor);
}
