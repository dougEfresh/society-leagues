package com.society.leagues.client.api.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Slot {
    Integer id;
    Date date;


    public Slot(Integer id, Date date) {
        this.id = id;
        this.date = date;
    }

    public Slot() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public static List<LocalDateTime> getDefault(LocalDateTime date) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime startDateNoon = date.withHour(12).withMinute(0).withSecond(0);
        for (int i = 0; i< 10; i++) {
            LocalDateTime dt = startDateNoon.plusMinutes(i*30);
            slots.add(dt);
        }

        return slots;
    }

}
