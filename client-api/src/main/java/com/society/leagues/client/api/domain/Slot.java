package com.society.leagues.client.api.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class Slot extends LeagueObject {
    LocalDateTime time;
    Integer allocated;

    public Slot() {
    }

    public static List<LocalDateTime> getDefault(LocalDateTime date) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime startDateNoon = date.withHour(12).withMinute(0).withSecond(0);
        for (int i = 0; i< 6; i++) {
            LocalDateTime dt = startDateNoon.plusMinutes(i*60);
            slots.add(dt);
        }

        return slots;
    }

    public LocalDateTime getLocalDateTime() {
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

    public String getTime() {
        return this.time.format(new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_DAY, 2).toFormatter());
    }
}
