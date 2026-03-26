package mx.edu.uteq.idgs14.ejemplo01.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
public class HabilidadTransversal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    private String habilidadTransversal;

    // HabilidadTransversal *:1 PerfilIngreso
    @ManyToOne
    @JoinColumn(name = "id_perfil", nullable = false)
    private PerfilIngreso perfilIngreso;
}