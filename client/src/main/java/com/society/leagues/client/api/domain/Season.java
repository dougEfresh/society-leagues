package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.api.domain.converters.DateTimeDeSerializer;
import com.society.leagues.client.api.domain.converters.DateTimeSerializer;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

public class Season extends LeagueObject {

    @NotNull String name;
    @NotNull
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime startDate;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime endDate;
    @NotNull Integer rounds;
    @NotNull Status seasonStatus;
    @NotNull Division division;

    public Season(String name, LocalDateTime startDate, Integer rounds, Division division) {
        this.name = name;
        this.startDate = startDate;
        this.rounds = rounds;
        this.seasonStatus = Status.ACTIVE;
        this.division = division;
    }
    
    public Season(String name, LocalDateTime startDate, Integer rounds, Status status) {
        this.name = name;
        this.startDate = startDate;
        this.rounds = rounds;
        this.seasonStatus = status;
    }

    public Season() {
    }

    public Season(String id) {
        this.id = id;
    }

    public Status getSeasonStatus() {
        return seasonStatus;
    }

    public void setSeasonStatus(Status seasonStatus) {
        this.seasonStatus = seasonStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getRounds() {
        return rounds;
    }

    public void setRounds(Integer rounds) {
        this.rounds = rounds;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public boolean isActive() {
        return this.seasonStatus == Status.ACTIVE;
    }

    public boolean isNine(){
        return getDivision() ==  Division.NINE_BALL_CHALLENGE || getDivision() == Division.NINE_BALL_TUESDAYS;
    }


    @Override
    public String toString() {
        return "Season{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", rounds=" + rounds +
                ", seasonStatus=" + seasonStatus +
                ", division=" + division +
                '}';
    }
}
