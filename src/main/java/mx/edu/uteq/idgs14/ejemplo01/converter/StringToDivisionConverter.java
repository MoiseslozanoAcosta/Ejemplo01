package mx.edu.uteq.idgs14.ejemplo01.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.edu.uteq.idgs14.ejemplo01.model.Division;
import mx.edu.uteq.idgs14.ejemplo01.repository.DivisionRepository;

@Component
public class StringToDivisionConverter
        implements org.springframework.core.convert.converter.Converter<String, Division> {

    @Autowired
    private DivisionRepository divisionRepository;

    @Override
    public Division convert(String id) {
        if (id == null || id.isEmpty()) return null;
        return divisionRepository.findById(Integer.valueOf(id)).orElse(null);
    }
}