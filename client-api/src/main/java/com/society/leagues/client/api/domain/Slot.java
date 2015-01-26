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
    
    public static List<Slot> getDefault(Date date) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i< 10; i++) {
            LocalDateTime dt = LocalDateTime.of(date.getYear(),date.getMonth()+1,date.getDate(),12,0,0);
            dt = dt.plusMinutes(i * 30);
            Slot slot = new Slot();
            slot.setId(i);
            slot.setDate(java.util.Date.from(dt.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            slots.add(slot);
        }

        return slots;
    }

}
