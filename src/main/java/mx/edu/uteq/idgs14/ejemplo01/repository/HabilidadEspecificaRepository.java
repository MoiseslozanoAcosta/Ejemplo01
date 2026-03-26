package mx.edu.uteq.idgs14.ejemplo01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mx.edu.uteq.idgs14.ejemplo01.model.HabilidadEspecifica;

public interface HabilidadEspecificaRepository extends JpaRepository<HabilidadEspecifica, Integer> {
}