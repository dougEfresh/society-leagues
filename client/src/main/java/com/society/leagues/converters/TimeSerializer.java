package com.society.leagues.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TimeSerializer extends JsonSerializer<List<LocalTime>> {

    @Override
    public void serialize(List<LocalTime> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (value == null) {
            return;
        }
        jgen.writeString(value.toString());
    }

    @Override
    public Class<?> handledType() {
        return List.class;
    }
}
