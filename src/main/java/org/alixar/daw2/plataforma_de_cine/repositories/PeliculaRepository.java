package org.alixar.daw2.plataforma_de_cine.repositories;

import org.alixar.daw2.plataforma_de_cine.entities.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Pelicula.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre la tabla de películas.
 * existeByTituloIgnoreCase: Verifica si existe una película con un título específico, ignorando mayúsculas y minúsculas.
 * existeByTituloIgnoreCaseAndIdNot: Verifica si existe una película con un título específico, ignorando mayúsculas y minúsculas, excluyendo una película con un ID específico
 */
@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    boolean existsByTituloIgnoreCase(String titulo);
    boolean existsByTituloIgnoreCaseAndIdNot(String titulo, Long id);
}
