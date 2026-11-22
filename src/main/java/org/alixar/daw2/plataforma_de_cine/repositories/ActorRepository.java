package org.alixar.daw2.plataforma_de_cine.repositories;

import org.alixar.daw2.plataforma_de_cine.entities.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Actor.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre la tabla de actores.
 */
@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
}
