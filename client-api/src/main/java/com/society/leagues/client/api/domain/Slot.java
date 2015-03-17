package com.society.leagues.client.api.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Slot extends LeagueObject {
    LocalDateTime time;
    Integer allocated;

    public Slot() {
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Integer getAllocated() {
        return allocated;
    }

    public void setAllocated(Integer allocated) {
        this.allocated = allocated;
    }
}
