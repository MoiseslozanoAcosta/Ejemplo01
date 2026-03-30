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
import lombok.ToString;

@Data
@Entity
public class PerfilIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    private String titulo;

    @NotEmpty
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_oferta", nullable = false)
    private OfertaEducativa ofertaEducativa;

    // PerfilIngreso 1:* HabilidadEspecifica
    @ToString.Exclude
    @OneToMany(mappedBy = "perfilIngreso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabilidadEspecifica> habilidadesEspecificas;

    // PerfilIngreso 1:* HabilidadTransversal
    @ToString.Exclude
    @OneToMany(mappedBy = "perfilIngreso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabilidadTransversal> habilidadesTransversales;
}