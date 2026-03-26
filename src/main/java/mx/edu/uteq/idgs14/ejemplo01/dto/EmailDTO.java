package mx.edu.uteq.idgs14.ejemplo01.dto;

import lombok.Data;

@Data
public class EmailDTO {
    private String destinario;
    private String asunto;
    private String mensaje;

    public EmailDTO() {
    }

    public EmailDTO(String to, String object, String body) {
        this.destinario = to;
        this.asunto = object;
        this.mensaje = body;
    }

    

}
