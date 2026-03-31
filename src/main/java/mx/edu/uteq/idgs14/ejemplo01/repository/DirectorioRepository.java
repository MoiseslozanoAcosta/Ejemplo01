package mx.edu.uteq.idgs14.ejemplo01.repository;

import mx.edu.uteq.idgs14.ejemplo01.model.Directorio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DirectorioRepository extends JpaRepository<Directorio, Integer> {

    List<Directorio> findByActivoTrueOrderByOrdenAsc();
}