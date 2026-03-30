package mx.edu.uteq.idgs14.ejemplo01.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expiracion;

    private boolean usado;

    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(expiracion);
    }
}