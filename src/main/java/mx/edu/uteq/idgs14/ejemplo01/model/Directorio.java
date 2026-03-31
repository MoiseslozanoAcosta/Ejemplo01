package mx.edu.uteq.idgs14.ejemplo01.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Directorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Size(min = 2, max = 100)
    private String nombre;

    @NotEmpty
    @Size(min = 2, max = 100)
    private String puesto;

    @Size(max = 100)
    private String departamento;

    @Size(max = 20)
    private String telefono;

    @Size(max = 100)
    private String email;

    private boolean activo;

    private Integer orden;
}