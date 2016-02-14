package com.society.leagues.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class TimeSerializer extends JsonSerializer<List<LocalTime>> {
    ObjectMapper mapper = new ObjectMapper();
    @Override
    public void serialize(List<LocalTime> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (value == null) {
            return;
        }
        List<String> times = new ArrayList<>();
        for (LocalTime localTime : value) {
            times.add(value.toString());
        }

        jgen.writeString(mapper.writeValueAsString(times));
    }
}
