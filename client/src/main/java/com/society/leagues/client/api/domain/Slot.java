package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class Slot extends LeagueObject {

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    @NotNull LocalDateTime timeStamp;
    Integer allocated = 0;

    public Slot() {
    }

    public Slot(LocalDateTime time) {
        this.timeStamp = time;
        this.allocated = 0;
    }

    public Slot(String id) {
        this.id = id;
    }

    public static List<LocalDateTime> getDefault(LocalDateTime date) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime startDateNoon = date.withHour(11).withMinute(0).withSecond(0);
        for (int i = 0; i< 16; i++) {
            LocalDateTime dt = startDateNoon.plusMinutes(i*30);
            slots.add(dt);
        }

        return slots;
    }

    public LocalDateTime getTimeStamp(){
        return timeStamp;
    }

    @JsonIgnore
    public String getTime() {
        return timeStamp.toLocalTime().toString();
    }

    @JsonIgnore
    public LocalDateTime getLocalDateTime() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getAllocated() {
        return allocated;
    }

    public void setAllocated(Integer allocated) {
        this.allocated = allocated;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id=" + getId() +
                " timeStamp=" + timeStamp +
                '}';
    }
}
