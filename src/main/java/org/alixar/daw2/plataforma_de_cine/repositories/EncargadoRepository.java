package org.alixar.daw2.plataforma_de_cine.repositories;

import org.alixar.daw2.plataforma_de_cine.entities.Encargado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncargadoRepository extends JpaRepository<Encargado, Long> {

   List<Encargado> findBySalaIsNull();
}