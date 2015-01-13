package com.society.leagues.client;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter implements Converter<LocalDate> {

    @Override
    public void serialize(LocalDate object, ObjectWriter writer, Context ctx) throws Exception {
        if (object == null)
            return;
        
        writer.writeValue(object.format(DateTimeFormatter.ISO_DATE));
    }

    @Override
    public LocalDate deserialize(ObjectReader reader, Context ctx) throws Exception {
        if (reader.valueAsString() == null)
            return null;
        
        return LocalDate.parse(reader.valueAsString(),DateTimeFormatter.ISO_DATE);
    }
}

