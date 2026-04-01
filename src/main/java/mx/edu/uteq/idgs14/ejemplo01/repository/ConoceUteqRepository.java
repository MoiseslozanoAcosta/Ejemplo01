package mx.edu.uteq.idgs14.ejemplo01.repository;

import mx.edu.uteq.idgs14.ejemplo01.model.ConoceUteq;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConoceUteqRepository extends JpaRepository<ConoceUteq, Integer> {

    Optional<ConoceUteq> findByTipoSeccion(ConoceUteq.TipoSeccion tipoSeccion);

    List<ConoceUteq> findByActivoTrueOrderByOrdenAsc();
}