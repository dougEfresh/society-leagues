package com.society.leagues.client.api.domain;

import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import com.society.leagues.client.DateConverter;

import java.sql.Date;
import java.time.format.DateTimeFormatter;

/**
 * Wrapper for GENSON
 */

public class LocalDate {
    
    String date;
    
    public static LocalDate now() {
        return new LocalDate();
    }

    public LocalDate() {
        date = java.time.LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    }

    public LocalDate(String date) {
        this.date = date;
    }

    @JsonIgnore
    public LocalDate(Date date) {
        if (date == null)
            return;
        
        this.date = date.toLocalDate().format(DateTimeFormatter.ISO_DATE);
    }
    
    @JsonIgnore
    public java.time.LocalDate getLocalDate() {
        return java.time.LocalDate.parse(date,DateTimeFormatter.ISO_DATE);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
