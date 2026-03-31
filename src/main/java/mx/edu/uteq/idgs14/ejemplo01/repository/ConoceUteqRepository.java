package mx.edu.uteq.idgs14.ejemplo01.repository.repository;

import mx.edu.uteq.idgs14.ejemplo01.model.ConoceUteq;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
 
public interface ConoceUteqRepository extends JpaRepository<ConoceUteq, Integer> {
 
    // Buscar por tipo de sección (MISION, VISION, etc.)
    Optional<ConoceUteq> findByTipoSeccion(ConoceUteq.TipoSeccion tipoSeccion);
 
    // Solo los activos ordenados
    List<ConoceUteq> findByActivoTrueOrderByOrdenAsc();
}
 
