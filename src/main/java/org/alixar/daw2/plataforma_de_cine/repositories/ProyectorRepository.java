package org.alixar.daw2.plataforma_de_cine.repositories;

import org.alixar.daw2.plataforma_de_cine.entities.Proyector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface ProyectorRepository extends JpaRepository<Proyector, Long> {

 //comentado hasta que se termine la entidad sala
  //  List<Proyector> findBySalaIsNull();
}