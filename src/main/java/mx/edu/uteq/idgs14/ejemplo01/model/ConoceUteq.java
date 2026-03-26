package mx.edu.uteq.idgs14.ejemplo01.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class ConoceUteq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoSeccion tipoSeccion;

    @NotEmpty
    @Size(min = 10, max = 500)
    @Column(nullable = false, length = 500)
    private String contenido;

    @Size(max = 20)
    private String textoDocumento;

    @Size(max = 10)
    private String revision;

    @Size(max = 20)
    private String fechaDocumento;

    private boolean activo;

    private Integer orden;

    public enum TipoSeccion {
        MISION,
        VISION,
        POLITICA,
        OBJETIVOS_SGE,
        VALORES
    }
}