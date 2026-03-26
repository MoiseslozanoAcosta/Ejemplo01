package mx.edu.uteq.idgs14.ejemplo01.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // EJEMPLO: DTAI
    @NotEmpty
    @Size(min = 2, max = 10)
    @Column(unique = true)
    private String clave;

    @NotEmpty
    private String nombreDivision;

    private boolean activo;

    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "divicion")
    //private List<OfertaEducativa> pOfertaEducativos;
}
