package com.society.conf.spring;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class DateDeSerializer extends JsonDeserializer<LocalDateTime> {

     @Override
     public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
         return LocalDateTime.parse(jp.getText());
     }

     @Override
     public Class<?> handledType() {
         return LocalDateTime.class;
     }
 }
