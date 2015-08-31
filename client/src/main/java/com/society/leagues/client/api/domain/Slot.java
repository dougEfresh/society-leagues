package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.api.domain.converters.DateTimeDeSerializer;
import com.society.leagues.client.api.domain.converters.DateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class Slot extends LeagueObject {

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime time;
    Integer allocated;

    public Slot() {
    }

    public Slot(LocalDateTime time) {
        this.time = time;
        this.allocated = 0;
    }

    public Slot(String id) {
        this.id = id;
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
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id=" + getId() +
                "time=" + time +
                '}';
    }
}
