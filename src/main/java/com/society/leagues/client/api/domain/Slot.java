package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
        LocalDateTime startDateNoon = date.withHour(11).withMinute(0).withSecond(0);
        for (int i = 0; i< 8; i++) {
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

    @JsonIgnore
    public String getTime() {
        return this.time.format(new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(":00").toFormatter()
				);
				//                .appendValue(ChronoField.MINUTE_OF_DAY, 2).toFormatter());
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id=" + getId() +
                "time=" + time +
                '}';
    }
}
