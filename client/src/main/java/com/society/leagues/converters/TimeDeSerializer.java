package com.society.leagues.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class TimeDeSerializer extends JsonDeserializer<LocalTime> {

     @Override
     public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
         if (jp == null ||jp.getText() == null)
             return null;

         return LocalTime.parse(jp.getText());
     }

     @Override
     public Class<?> handledType() {
         return LocalTime.class;
     }
 }
