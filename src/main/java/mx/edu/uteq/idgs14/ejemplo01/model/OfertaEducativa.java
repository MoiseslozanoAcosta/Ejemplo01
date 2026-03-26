package mx.edu.uteq.idgs14.ejemplo01.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
public class OfertaEducativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    private String nombreOferta;

    @NotEmpty
    private String modalidad;

    @NotEmpty
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "id_division", nullable = false)
    private Division division;

    // ✅ AGREGADO: una oferta puede tener varios perfiles
    @OneToMany(mappedBy = "ofertaEducativa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PerfilIngreso> perfilesIngreso;
}