package com.society.leagues.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TimeDeSerializer extends JsonDeserializer<List<LocalTime>> {
    ObjectMapper mapper = new ObjectMapper();

     @Override
     public List<LocalTime> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
         if (jp == null ||jp.getText() == null)
             return null;
         List<LocalTime> times = new ArrayList<>();

         while (jp.nextToken() != JsonToken.END_OBJECT) {
            if (jp.getCurrentToken() != JsonToken.END_ARRAY) {
                JsonParser jParser = mapper.getFactory().createParser(jp.getText());
                if (jParser != null && jParser.getText() != null)
                    times.add(LocalTime.parse(jParser.getText()));
            }
        }
         return times;
     }

 }
