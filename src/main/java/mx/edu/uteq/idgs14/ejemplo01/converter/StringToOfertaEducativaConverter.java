package mx.edu.uteq.idgs14.ejemplo01.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.edu.uteq.idgs14.ejemplo01.model.OfertaEducativa;
import mx.edu.uteq.idgs14.ejemplo01.repository.OfertaEducativaRepository;

@Component
public class StringToOfertaEducativaConverter 
    implements org.springframework.core.convert.converter.Converter<String, OfertaEducativa> {

    @Autowired
    private OfertaEducativaRepository ofertaEducativaRepository;

    @Override
    public OfertaEducativa convert(String id) {
        if (id == null || id.isEmpty()) return null;
        return ofertaEducativaRepository.findById(Integer.valueOf(id)).orElse(null);
    }
}